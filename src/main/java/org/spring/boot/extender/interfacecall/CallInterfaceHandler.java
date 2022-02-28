package org.spring.boot.extender.interfacecall;

import org.spring.boot.extender.interfacecall.annotation.Body;
import org.spring.boot.extender.invoker.bean.Result;
import org.springframework.aop.support.AopUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
        /*Parameter[] parameters=method.getParameters();
        for(Parameter parameter:parameters){
            Body body=parameter.getAnnotation(Body.class);
        }
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity formEntity = new HttpEntity<>(args[0], headers);*/
        if(args.length==1){
            String errorMsg=String.format("调用接口%s传递的参数多余一个！",interfaceUrl);
            throw new RuntimeException(errorMsg);
        }
        return restTemplate.postForObject(interfaceUrl,args[0], Class.forName(returnName));

    }
}
