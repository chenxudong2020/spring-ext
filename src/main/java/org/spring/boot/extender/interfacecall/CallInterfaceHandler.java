package org.spring.boot.extender.interfacecall;


import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CallInterfaceHandler  implements InvocationHandler {
    private RestTemplate restTemplate;
    private CallProperties callProperties;
    private String className;

    public CallInterfaceHandler(RestTemplate restTemplate, CallProperties callProperties,String className) {
        this.restTemplate = restTemplate;
        this.callProperties = callProperties;
        this.className=className;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this,args);
        }
        String key=String.format("%s-%s",className,method.getName());
        MethodMeta methodMeta=callProperties.methodMetaMap.get(key);
        return methodMeta.methodHandler.invoke(proxy,method,args,restTemplate,callProperties,className);
    }
}
