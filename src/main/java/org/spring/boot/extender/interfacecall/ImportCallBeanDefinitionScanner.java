package org.spring.boot.extender.interfacecall;

import org.spring.boot.extender.interfacecall.annotation.*;
import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


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

    private Map<String, List<ParameterMeta>> getParameterMeta(AnnotatedBeanDefinition beanDefinition,String InterfaceClientValue) {
        Class tClass;
        Map<String, List<ParameterMeta>> map=new ConcurrentHashMap<>();
        CallProperties callProperties = CallProperties.getInstance();
        try {
            tClass = ClassUtils.forName(beanDefinition.getBeanClassName(), classLoader);
            int bodyCount =0;
            int count=0;
            Method[] methods = tClass.getDeclaredMethods();
            ParameterMeta parameterMeta = null;
            for (Method x : methods) {
                Parameter[] parameters = x.getParameters();
                List<ParameterMeta> list=new ArrayList<>();
                for (Parameter parameter : parameters) {
                    String name = parameter.getName();
                    parameterMeta=new ParameterMeta();
                    parameterMeta.parameterName=name;
                    Body body = parameter.getAnnotation(Body.class);
                    if (null != body) {
                        bodyCount=bodyCount+1;
                        parameterMeta.body=body;
                        if (bodyCount > 1) {
                            throw new RuntimeException(x.getName()+"只允许一个body注解！");
                        }
                        if(parameterMeta.head!=null){
                            throw new RuntimeException(x.getName()+"body和head不能注解一个参数!");
                        }
                        parameterMeta.bodyCount=count;
                    }
                    Head head = parameter.getAnnotation(Head.class);
                    if (null != head) {
                        parameterMeta.head=head;
                        parameterMeta.parameterCount=count;
                        if(parameterMeta.body!=null){
                            throw new RuntimeException(x.getName()+"body和head不能注解一个参数!");
                        }
                    }
                    count=count+1;
                    list.add(parameterMeta);
                }
                String key = String.format("%s-%s", beanDefinition.getBeanClassName(), x.getName());
                map.put(key,list);
                MethodMeta methodMeta=new MethodMeta();
                methodMeta.methodName=key;
                methodMeta.post=x.getAnnotation(POST.class);
                methodMeta.get=x.getAnnotation(GET.class);
                if(methodMeta.post!=null&&methodMeta.get!=null){
                    throw new RuntimeException(x.getName()+"post和get不能注解同一个方法!");
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
