package org.spring.boot.extender.interfacecall;



import org.spring.boot.extender.interfacecall.annotation.GET;
import org.spring.boot.extender.interfacecall.annotation.InterfaceClient;
import org.spring.boot.extender.interfacecall.annotation.POST;
import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;
import org.spring.boot.extender.interfacecall.handler.GetHandler;
import org.spring.boot.extender.interfacecall.handler.PostHandler;
import org.spring.boot.extender.interfacecall.paramhandler.ParamHandler;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ImportCallBeanDefinitionScanner extends ClassPathBeanDefinitionScanner implements ResourceLoaderAware {
    private final ClassLoader classLoader;


    public ImportCallBeanDefinitionScanner(BeanDefinitionRegistry registry, ClassLoader classLoader) {
        super(registry, false);
        this.classLoader = classLoader;

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        super.setResourceLoader(resourceLoader);
    }


    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        CallProperties callProperties = CallProperties.getInstance();
        GenericBeanDefinition genericBeanDefinition;
        for (BeanDefinitionHolder holder : beanDefinitions) {

            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) holder.getBeanDefinition();
            AnnotationAttributes annAttr = AnnotationAttributes.fromMap(beanDefinition.getMetadata().getAnnotationAttributes(InterfaceClient.class.getName()));
            String value = annAttr.getString("value");
            callProperties.parameterMetaMap.putAll(getParameterMeta(beanDefinition,value));
            genericBeanDefinition = (GenericBeanDefinition) holder.getBeanDefinition();
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(genericBeanDefinition.getBeanClassName()));
            genericBeanDefinition.setBeanClass(CallInterfaceFactoryBean.class);
        }


        return beanDefinitions;
    }

    private Map<String, List<ParameterMeta>> getParameterMeta(AnnotatedBeanDefinition beanDefinition, String InterfaceClientValue) {
        Class tClass;
        Map<String, List<ParameterMeta>> map=new ConcurrentHashMap<>();
        CallProperties callProperties = CallProperties.getInstance();
        try {
            tClass = ClassUtils.forName(beanDefinition.getBeanClassName(), classLoader);
            Method[] methods = tClass.getDeclaredMethods();
             for (Method x : methods) {
                Parameter[] parameters = x.getParameters();
                List<ParameterMeta> list=new ArrayList<>();
                ParamHandler paramHandler=null;
                int parameterCount=0;
                for (Parameter parameter : parameters){
                     paramHandler=new ParamHandler();
                     paramHandler.handler(beanDefinition,InterfaceClientValue,x,parameter,parameterCount);
                     list.add(paramHandler.getHandlerRequest().getParameterMeta());
                     parameterCount+=1;
                }
                String key = paramHandler.getHandlerRequest().getKey();
                map.put(key,list);
                MethodMeta methodMeta=new MethodMeta();
                methodMeta.methodName=key;
                methodMeta.post=x.getAnnotation(POST.class);
                methodMeta.get=x.getAnnotation(GET.class);
                if(methodMeta.post!=null&&methodMeta.get!=null){
                    throw new RuntimeException(x.getName()+"post和get不能注解同一个方法!");
                }else if(methodMeta.post!=null&&methodMeta.get==null){
                    methodMeta.methodHandler=new PostHandler();
                }else if(methodMeta.post==null&&methodMeta.get!=null){
                    methodMeta.methodHandler=new GetHandler();
                }else{
                    throw new RuntimeException(x.getName()+"post和get必须注解一个方法!");
                }
                callProperties.methodMetaMap.put(key,methodMeta);
                String interfaceUrlSuffix =null;
                if(methodMeta.post!=null){
                     interfaceUrlSuffix =methodMeta.post.value();
                }else {
                     interfaceUrlSuffix =methodMeta.get.value();
                }
                String interfaceUrl =interfaceUrlSuffix;
                if(!StringUtils.isEmpty(InterfaceClientValue)){
                    interfaceUrl = String.format("%s/%s", InterfaceClientValue, interfaceUrlSuffix);
                }
                String returnName =x.getReturnType().getName();
                callProperties.interfaceUrlMap.put(key, interfaceUrl);
                callProperties.returnMap.put(key, returnName);

            }
            return map;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean isCandidate = false;
        if (beanDefinition.getMetadata().isIndependent()) {
            if (!beanDefinition.getMetadata().isAnnotation()) {
                isCandidate = true;
            }
        }
        return isCandidate;
    }

}
