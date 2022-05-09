package org.spring.ext.interfacecall;



import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.spring.ext.interfacecall.annotation.*;
import org.spring.ext.interfacecall.exception.InterfaceCallInitException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ImportCallBeanDefinitionScanner extends ClassPathBeanDefinitionScanner implements ResourceLoaderAware {
    private final ClassLoader classLoader;
    private List<Object> listResource;
    private BeanFactory beanFactory;
    private Class<? extends ApiRestTemplate> restTemplateClass;
    private BeanDefinitionRegistry registry;
    public ImportCallBeanDefinitionScanner(BeanDefinitionRegistry registry, ClassLoader classLoader,List<Object> listResource, BeanFactory beanFactory,Class<? extends ApiRestTemplate> restTemplateClass) {
        super(registry, false);
        this.classLoader = classLoader;
        this.listResource=listResource;
        this.beanFactory=beanFactory;
        this.restTemplateClass=restTemplateClass;
        this.registry=registry;

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        super.setResourceLoader(resourceLoader);
    }


    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        CallProperties callProperties = beanFactory.getBean(CallProperties.class);
        GenericBeanDefinition genericBeanDefinition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) holder.getBeanDefinition();
            AnnotationAttributes annAttr = AnnotationAttributes.fromMap(beanDefinition.getMetadata().getAnnotationAttributes(InterfaceClient.class.getName()));
            Class callBackClass=annAttr.getClass("callBackClass");
            String value = annAttr.getString("value");
            callProperties.parameterMetaMap.putAll(getParameterMeta(beanDefinition, value));
            genericBeanDefinition = (GenericBeanDefinition) holder.getBeanDefinition();
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(genericBeanDefinition.getBeanClassName()));
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(listResource);
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory);
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(restTemplateClass.getName());
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(callBackClass.getName());
            boolean isCallBack=false;
            try {
                isCallBack=genericBeanDefinition.resolveBeanClass(classLoader).isAssignableFrom(callBackClass);
            }catch (ClassNotFoundException e){

            }
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(isCallBack);
            if(isCallBack){
                GenericBeanDefinition callBackClassGenericBeanDefinition=new GenericBeanDefinition();
                callBackClassGenericBeanDefinition.setBeanClass(callBackClass);
                callBackClassGenericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                registry.registerBeanDefinition(callBackClass.getName(),callBackClassGenericBeanDefinition);
            }

            genericBeanDefinition.setBeanClass(CallInterfaceFactoryBean.class);



        }

        return beanDefinitions;
    }

    private Map<String, List<ParameterMeta>> getParameterMeta(AnnotatedBeanDefinition beanDefinition, String interfaceClientValue) {
        Class beanClass =null;
        try {
            beanClass = ClassUtils.forName(beanDefinition.getBeanClassName(), classLoader);
        }catch (ClassNotFoundException e){
            throw new InterfaceCallInitException(e);
        }

        Map<String, List<ParameterMeta>> map = new ConcurrentHashMap<>(16);
        ApiMethodCallback apiMethodCallback=new ApiMethodCallback(beanFactory,beanDefinition,interfaceClientValue,map);
        ReflectionUtils.doWithMethods(beanClass,apiMethodCallback);
        return map;

    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean isCandidate = false;
        if (beanDefinition.getMetadata().isIndependent() && beanDefinition.getMetadata().isInterface()) {
            isCandidate = true;
        }
        return isCandidate;
    }


}
