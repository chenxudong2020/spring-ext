package org.spring.boot.extender.interfacecall.handler;

import org.spring.boot.extender.interfacecall.CallProperties;
import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public interface MethodHandler {



     default Object invoke(MethodHandler methodHandler, MethodMeta method, Object[] args, RestTemplate restTemplate, CallProperties callProperties, String className){
          return this.invoke(methodHandler,method,args,restTemplate,callProperties,className);
     }

     Object doHandler(List<ParameterMeta> parameterMetas, HttpHeaders headers, Object args[], String url, String returnName, RestTemplate restTemplate, MediaType type) throws Throwable;


}
