package org.spring.boot.extender.interfacecall.paramhandler;

import org.spring.boot.extender.interfacecall.entity.Constant;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParamHandler {


    private HandlerRequest handlerRequest;



    public void handler(AnnotatedBeanDefinition beanDefinition, String InterfaceClientValue, Method method, Parameter parameter,int parameterCount){
        if(parameter!=null){
            BodyHandler bodyHandler=new BodyHandler();
            HeadHandler headHandler=new HeadHandler();
            UrlHandler urlHandler=new UrlHandler();
            QueryHandler queryHandler=new QueryHandler();
            bodyHandler.setNext(headHandler);
            headHandler.setNext(urlHandler);
            urlHandler.setNext(queryHandler);
            HandlerRequest handlerRequest=new HandlerRequest();
            handlerRequest.setBeanDefinition(beanDefinition);
            String key = String.format(Constant.keyFormat, beanDefinition.getBeanClassName(), method.getName());
            handlerRequest.setKey(key);
            handlerRequest.setInterfaceClientValue(InterfaceClientValue);
            handlerRequest.setParameter(parameter);
            handlerRequest.setParameterCount(parameterCount);
            handlerRequest.init();
            HandlerChain handlerChain=bodyHandler.handler(handlerRequest);
            while(handlerChain!=null){
                handlerChain=handlerChain.handler(handlerRequest);
            }
            this.handlerRequest=handlerRequest;
        }else{
            HandlerRequest handlerRequest=new HandlerRequest();
            handlerRequest.setBeanDefinition(beanDefinition);
            String key = String.format(Constant.keyFormat, beanDefinition.getBeanClassName(), method.getName());
            handlerRequest.setKey(key);
            handlerRequest.setInterfaceClientValue(InterfaceClientValue);
            handlerRequest.setParameterCount(parameterCount);
            handlerRequest.init();
            this.handlerRequest=handlerRequest;
         }




    }

    public HandlerRequest getHandlerRequest() {
        return handlerRequest;
    }
}
