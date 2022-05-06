package org.spring.ext.interfacecall;



import org.spring.ext.interfacecall.entity.Constant;
import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.spring.ext.interfacecall.handler.GetHandler;
import org.spring.ext.interfacecall.handler.PostHandler;
import org.spring.ext.interfacecall.paramhandler.ParamHandler;
import org.spring.ext.interfacecall.annotation.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ImportCallBeanDefinitionScanner extends ClassPathBeanDefinitionScanner implements ResourceLoaderAware {
    private final ClassLoader classLoader;
    private List<Object> listResource;
    private BeanFactory beanFactory;
    private Class<? extends APIRestTemplate> restTemplateClass;
    private BeanDefinitionRegistry registry;
    public ImportCallBeanDefinitionScanner(BeanDefinitionRegistry registry, ClassLoader classLoader,List<Object> listResource, BeanFactory beanFactory,Class<? extends APIRestTemplate> restTemplateClass) {
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

    private Map<String, List<ParameterMeta>> getParameterMeta(AnnotatedBeanDefinition beanDefinition, String InterfaceClientValue) {
        CallProperties callProperties =  beanFactory.getBean(CallProperties.class);
        Class beanClass =null;
        try {
            beanClass = ClassUtils.forName(beanDefinition.getBeanClassName(), classLoader);
        }catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }

        Map<String, List<ParameterMeta>> map = new ConcurrentHashMap<>();
        ReflectionUtils.doWithMethods(beanClass, new ReflectionUtils.MethodCallback() {
            private boolean hasAnnotation(Method method,Class classz){
                return method.getAnnotation(classz)!=null;
            }

            private MethodMeta initMethod(Method method, AnnotatedBeanDefinition beanDefinition, String InterfaceClientValue, Map<String, List<ParameterMeta>> map, CallProperties callProperties) {
                List<ParameterMeta> list=new ArrayList<>();
                Parameter[] parameters = method.getParameters();
                ParamHandler paramHandler  = new ParamHandler();
                int parameterCount = 0;
                for (Parameter parameter : parameters) {
                    paramHandler.handler(beanDefinition, InterfaceClientValue, method, parameter, parameterCount);
                    list.add(paramHandler.getHandlerRequest().getParameterMeta());
                    parameterCount += 1;
                }
                if (paramHandler.getHandlerRequest()==null) {
                    paramHandler.handler(beanDefinition, InterfaceClientValue, method, null, parameterCount);
                }
                String key = paramHandler.getHandlerRequest().getKey();
                map.put(key, list);
                MethodMeta methodMeta = new MethodMeta();
                methodMeta.methodName = key;
                methodMeta.post =method.getAnnotation(POST.class);
                methodMeta.get = method.getAnnotation(GET.class);
                methodMeta.cache = method.getAnnotation(Cache.class);
                methodMeta.type = method.getAnnotation(Type.class);
                callProperties.methodMetaMap.put(key, methodMeta);
                String returnName = method.getReturnType().getName();
                callProperties.returnMap.put(methodMeta.methodName, returnName);
                return methodMeta;

            }


            private void initPostMethod(Method method, AnnotatedBeanDefinition beanDefinition, String InterfaceClientValue, Map<String, List<ParameterMeta>> map, CallProperties callProperties) {
                MethodMeta methodMeta = initMethod(method, beanDefinition, InterfaceClientValue, map, callProperties);
                methodMeta.methodHandler=beanFactory.getBean(PostHandler.class);
                String interfaceUrlSuffix = methodMeta.post.value();
                String interfaceUrl = interfaceUrlSuffix;
                if (!StringUtils.isEmpty(InterfaceClientValue)) {
                    interfaceUrl = String.format(Constant.urlFormat, InterfaceClientValue, interfaceUrlSuffix);
                }

                callProperties.interfaceUrlMap.put(methodMeta.methodName, interfaceUrl);
            }


            private void initGetMethod(Method method, AnnotatedBeanDefinition beanDefinition, String InterfaceClientValue, Map<String, List<ParameterMeta>> map, CallProperties callProperties) {
                MethodMeta methodMeta = initMethod(method, beanDefinition, InterfaceClientValue, map, callProperties);
                methodMeta.methodHandler=beanFactory.getBean(GetHandler.class);
                String interfaceUrlSuffix = methodMeta.get.value();
                String interfaceUrl = interfaceUrlSuffix;
                if (!StringUtils.isEmpty(InterfaceClientValue)) {
                    interfaceUrl = String.format(Constant.urlFormat, InterfaceClientValue, interfaceUrlSuffix);
                }
                callProperties.interfaceUrlMap.put(methodMeta.methodName, interfaceUrl);


            }


            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (this.hasAnnotation(method,POST.class) && this.hasAnnotation(method,GET.class)) {
                    throw new RuntimeException(method.getName() + "POST和GET不能注解同一个方法!");
                } else if (this.hasAnnotation(method,POST.class) && !this.hasAnnotation(method,GET.class)) {
                    this.initPostMethod(method, beanDefinition, InterfaceClientValue, map, callProperties);

                } else if (!this.hasAnnotation(method,POST.class) && this.hasAnnotation(method,GET.class)) {
                    this.initGetMethod(method, beanDefinition, InterfaceClientValue, map, callProperties);
                }
            }
        });


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
