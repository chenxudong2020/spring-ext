package org.spring.ext.interfacecall.handler;

import org.spring.ext.interfacecall.ApiRestTemplate;
import org.spring.ext.interfacecall.CallProperties;
import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.lang.reflect.Method;
import java.util.List;

public interface MethodHandler {


     /**
      *  中转方法
      * @param methodHandler
      * @param method
      * @param args
      * @param beanFactory
      * @param callProperties
      * @param className
      * @return
      */
     default Object invoke(MethodHandler methodHandler, MethodMeta method, Object[] args, BeanFactory beanFactory, CallProperties callProperties, String className){
          return this.invoke(methodHandler,method,args,beanFactory,callProperties,className);
     }

     /**执行具体http方法
      * @param parameterMetas
      * @param headers
      * @param args
      * @param url
      * @param returnName
      * @param beanFactory
      * @param type
      * @param restTemplateClass
      * @param callBackClass
      * @param isCallBack
      * @param method
      * @return
      * @throws Throwable
      */
     Object doHandler(List<ParameterMeta> parameterMetas, HttpHeaders headers, Object[] args, String url, String returnName, BeanFactory beanFactory, MediaType type, Class<? extends ApiRestTemplate> restTemplateClass, Class callBackClass, boolean isCallBack, Method method) throws Throwable;


}
