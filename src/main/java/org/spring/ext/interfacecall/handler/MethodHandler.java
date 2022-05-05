package org.spring.ext.interfacecall.handler;

import org.spring.ext.interfacecall.CallProperties;
import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

public interface MethodHandler {



     default Object invoke(MethodHandler methodHandler, MethodMeta method, Object[] args, BeanFactory beanFactory, CallProperties callProperties, String className){
          return this.invoke(methodHandler,method,args,beanFactory,callProperties,className);
     }

     Object doHandler(List<ParameterMeta> parameterMetas, HttpHeaders headers, Object args[], String url, String returnName, BeanFactory beanFactory, MediaType type) throws Throwable;


}
