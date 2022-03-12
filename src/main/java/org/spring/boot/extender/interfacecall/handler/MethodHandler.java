package org.spring.boot.extender.interfacecall.handler;

import org.spring.boot.extender.interfacecall.CallProperties;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

public interface MethodHandler {
    Object invoke(Object proxy, Method method, Object[] args, RestTemplate restTemplate, CallProperties callProperties, String className) throws Throwable;

}
