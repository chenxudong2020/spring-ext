package org.spring.ext.interfacecall;

import org.spring.ext.interfacecall.CallProperties;
import org.spring.ext.interfacecall.annotation.Cache;
import org.spring.ext.interfacecall.annotation.GET;
import org.spring.ext.interfacecall.annotation.POST;
import org.spring.ext.interfacecall.annotation.Type;
import org.spring.ext.interfacecall.entity.Constant;
import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.spring.ext.interfacecall.exception.InterfaceCallInitException;
import org.spring.ext.interfacecall.handler.GetHandler;
import org.spring.ext.interfacecall.handler.PostHandler;
import org.spring.ext.interfacecall.paramhandler.ParamHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 87260
 */
public class ApiMethodCallback implements ReflectionUtils.MethodCallback {


    private BeanFactory beanFactory;
    private AnnotatedBeanDefinition beanDefinition;
    private String interfaceClientValue;
    private  Map<String, List<ParameterMeta>> map;



    public ApiMethodCallback(BeanFactory beanFactory, AnnotatedBeanDefinition beanDefinition, String interfaceClientValue, Map<String, List<ParameterMeta>> map) {
        this.beanFactory = beanFactory;
        this.beanDefinition = beanDefinition;
        this.interfaceClientValue = interfaceClientValue;
        this.map = map;
    }

    private boolean hasAnnotation(Method method, Class classz){
        return method.getAnnotation(classz)!=null;
    }
    private MethodMeta initMethod(Method method, AnnotatedBeanDefinition beanDefinition, String interfaceClientValue, Map<String, List<ParameterMeta>> map, CallProperties callProperties) {
        List<ParameterMeta> list=new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        ParamHandler paramHandler  = new ParamHandler();
        int parameterCount = 0;
        for (Parameter parameter : parameters) {
            paramHandler.handler(beanDefinition, interfaceClientValue, method, parameter, parameterCount);
            list.add(paramHandler.getHandlerRequest().getParameterMeta());
            parameterCount += 1;
        }
        if (paramHandler.getHandlerRequest()==null) {
            paramHandler.handler(beanDefinition, interfaceClientValue, method, null, parameterCount);
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


    private void initPostMethod(Method method, AnnotatedBeanDefinition beanDefinition, String interfaceClientValue, Map<String, List<ParameterMeta>> map, CallProperties callProperties) {
        MethodMeta methodMeta = initMethod(method, beanDefinition, interfaceClientValue, map, callProperties);
        methodMeta.methodHandler=beanFactory.getBean(PostHandler.class);
        String interfaceUrlSuffix = methodMeta.post.value();
        String interfaceUrl = interfaceUrlSuffix;
        if (!StringUtils.isEmpty(interfaceClientValue)) {
            interfaceUrl = String.format(Constant.URL_FORMAT, interfaceClientValue, interfaceUrlSuffix);
        }

        callProperties.interfaceUrlMap.put(methodMeta.methodName, interfaceUrl);
    }


    private void initGetMethod(Method method, AnnotatedBeanDefinition beanDefinition, String interfaceClientValue, Map<String, List<ParameterMeta>> map, CallProperties callProperties) {
        MethodMeta methodMeta = initMethod(method, beanDefinition, interfaceClientValue, map, callProperties);
        methodMeta.methodHandler=beanFactory.getBean(GetHandler.class);
        String interfaceUrlSuffix = methodMeta.get.value();
        String interfaceUrl = interfaceUrlSuffix;
        if (!StringUtils.isEmpty(interfaceClientValue)) {
            interfaceUrl = String.format(Constant.URL_FORMAT, interfaceClientValue, interfaceUrlSuffix);
        }
        callProperties.interfaceUrlMap.put(methodMeta.methodName, interfaceUrl);


    }


    @Override
    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
        CallProperties callProperties =  beanFactory.getBean(CallProperties.class);
        if (this.hasAnnotation(method,POST.class) && this.hasAnnotation(method,GET.class)) {
            throw new InterfaceCallInitException(method.getName() + "POST和GET不能注解同一个方法!");
        } else if (this.hasAnnotation(method,POST.class) && !this.hasAnnotation(method,GET.class)) {
            this.initPostMethod(method, beanDefinition, interfaceClientValue, map, callProperties);

        } else if (!this.hasAnnotation(method,POST.class) && this.hasAnnotation(method,GET.class)) {
            this.initGetMethod(method, beanDefinition, interfaceClientValue, map, callProperties);
        }
    }

}
