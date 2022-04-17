package org.spring.boot.extender.interfacecall;


import org.spring.boot.extender.interfacecall.annotation.Cache;
import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.spring.boot.extender.interfacecall.handler.HandlerProxy;
import org.spring.boot.extender.interfacecall.handler.MethodHandler;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class CallInterfaceHandler implements InvocationHandler {
    private RestTemplate restTemplate;
    private String className;
    private Map<String,String> interfaceUrlMap;


    public CallInterfaceHandler(RestTemplate restTemplate, Map<String,String> interfaceUrlMap,String className) {
        this.restTemplate = restTemplate;
        this.className = className;
        this.interfaceUrlMap=interfaceUrlMap;

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        CallProperties callProperties=CallProperties.getInstance();
        String key = String.format("%s-%s", className, method.getName());
        MethodMeta methodMeta = callProperties.methodMetaMap.get(key);
        methodMeta.method = method;
        MethodHandler methodHandler = methodMeta.methodHandler;
        HandlerProxy handlerProxy = new HandlerProxy(methodHandler, methodMeta, args, restTemplate, callProperties, className);
        MethodHandler methodHandlerProxy = (MethodHandler) Proxy.newProxyInstance(this.getClass().getClassLoader(), methodHandler.getClass().getInterfaces(), handlerProxy);
        return methodHandlerProxy.invoke(methodHandler, methodMeta, args, restTemplate, callProperties, className);



    }
}
