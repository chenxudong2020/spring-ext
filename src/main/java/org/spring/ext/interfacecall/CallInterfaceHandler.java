package org.spring.ext.interfacecall;


import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.handler.MethodHandler;
import org.spring.ext.interfacecall.handler.MethodHandlerWrapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CallInterfaceHandler implements InvocationHandler {
    private BeanFactory beanFactory;
    private final String className;
    private Class<? extends APIRestTemplate> restTemplateClass;


    public CallInterfaceHandler(BeanFactory beanFactory, String className,Class<? extends APIRestTemplate> restTemplateClass) {
        this.beanFactory = beanFactory;
        this.className = className;
        this.restTemplateClass=restTemplateClass;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        CallProperties callProperties=beanFactory.getBean(CallProperties.class);
        String key = String.format("%s-%s", className, method.getName());
        MethodMeta methodMeta = callProperties.methodMetaMap.get(key);
        methodMeta.method = method;
        MethodHandler methodHandler = methodMeta.methodHandler;
        MethodHandlerWrapper methodHandlerWrapper = new MethodHandlerWrapper(methodHandler, methodMeta, args, beanFactory, callProperties, className,restTemplateClass);
        return methodHandlerWrapper.invoke(proxy,method,args);

    }
}
