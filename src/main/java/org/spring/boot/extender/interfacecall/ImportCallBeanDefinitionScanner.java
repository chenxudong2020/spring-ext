package org.spring.boot.extender.interfacecall;

import org.spring.boot.extender.interfacecall.annotation.Body;
import org.spring.boot.extender.interfacecall.annotation.Head;
import org.spring.boot.extender.interfacecall.annotation.InterfaceClient;
import org.spring.boot.extender.interfacecall.annotation.POST;
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
        Class tClass;
        for (BeanDefinitionHolder holder : beanDefinitions) {

            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) holder.getBeanDefinition();
            callProperties.parameterMetaMap.putAll(getParameterMeta(beanDefinition));
            AnnotationAttributes annAttr = AnnotationAttributes.fromMap(beanDefinition.getMetadata().getAnnotationAttributes(InterfaceClient.class.getName()));
            String value = annAttr.getString("value");
            Set<MethodMetadata> methodMetadataSet = beanDefinition.getMetadata().getAnnotatedMethods(POST.class.getName());

            for (MethodMetadata methodMetadata : methodMetadataSet) {
                String interfaceUrlSuffix = AnnotationAttributes.fromMap(methodMetadata.getAnnotationAttributes(POST.class.getName())).getString("value");
                String interfaceUrl = String.format("%s/%s", value, interfaceUrlSuffix);
                String methodName = methodMetadata.getMethodName();
                String returnName = methodMetadata.getReturnTypeName();
                String key = String.format("%s-%s", beanDefinition.getBeanClassName(), methodName);

                callProperties.interfaceUrlMap.put(key, interfaceUrl);
                callProperties.returnMap.put(key, returnName);
            }
            genericBeanDefinition = (GenericBeanDefinition) holder.getBeanDefinition();
            genericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(Objects.requireNonNull(genericBeanDefinition.getBeanClassName()));
            genericBeanDefinition.setBeanClass(CallInterfaceFactoryBean.class);
        }


        return beanDefinitions;
    }

    private Map<String, List<ParameterMeta>> getParameterMeta(AnnotatedBeanDefinition beanDefinition) {
        Class tClass;
        Map<String, List<ParameterMeta>> map=new ConcurrentHashMap<>();
        try {
            tClass = ClassUtils.forName(beanDefinition.getBeanClassName(), classLoader);
            int bodyCount =0;
            int count=0;
            Method[] methods = tClass.getDeclaredMethods();
            ParameterMeta parameterMeta = null;
            List<ParameterMeta> list=new ArrayList<>();
            for (Method x : methods) {
                Parameter[] parameters = x.getParameters();
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
