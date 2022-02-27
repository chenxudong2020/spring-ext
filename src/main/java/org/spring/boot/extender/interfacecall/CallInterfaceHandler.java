package org.spring.boot.extender.interfacecall;

import org.spring.boot.extender.invoker.bean.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CallInterfaceHandler  implements InvocationHandler {
    private RestTemplate restTemplate;
    private CallProperties callProperties;

    public CallInterfaceHandler(RestTemplate restTemplate, CallProperties callProperties) {
        this.restTemplate = restTemplate;
        this.callProperties = callProperties;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this,args);
        }
        String interfaceUrl=callProperties.interfaceUrlMap.get(method.getName());
        String returnName=callProperties.returnMap.get(method.getName());
        return restTemplate.postForObject(interfaceUrl,args[0], Class.forName(returnName));

    }
}
