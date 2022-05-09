package org.spring.ext.interfacecall.paramhandler;

import org.spring.ext.interfacecall.entity.Constant;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ParamHandler {


    private HandlerRequest handlerRequest;



    public void handler(AnnotatedBeanDefinition beanDefinition, String interfaceClientValue, Method method, Parameter parameter,int parameterCount){
        if(parameter!=null){
            BodyAbstractHandler bodyHandler=new BodyAbstractHandler();
            HeadAbstractHandler headHandler=new HeadAbstractHandler();
            UrlAbstractHandler urlHandler=new UrlAbstractHandler();
            QueryAbstractHandler queryHandler=new QueryAbstractHandler();
            bodyHandler.setNext(headHandler);
            headHandler.setNext(urlHandler);
            urlHandler.setNext(queryHandler);
            HandlerRequest handlerRequest=new HandlerRequest();
            handlerRequest.setBeanDefinition(beanDefinition);
            String key = String.format(Constant.KEY_FORMAT, beanDefinition.getBeanClassName(), method.getName());
            handlerRequest.setKey(key);
            handlerRequest.setInterfaceClientValue(interfaceClientValue);
            handlerRequest.setParameter(parameter);
            handlerRequest.setParameterCount(parameterCount);
            handlerRequest.init();
            AbstractHandlerChain abstractHandlerChain =bodyHandler.handler(handlerRequest);
            while(abstractHandlerChain !=null){
                abstractHandlerChain = abstractHandlerChain.handler(handlerRequest);
            }
            this.handlerRequest=handlerRequest;
        }else{
            HandlerRequest handlerRequest=new HandlerRequest();
            handlerRequest.setBeanDefinition(beanDefinition);
            String key = String.format(Constant.KEY_FORMAT, beanDefinition.getBeanClassName(), method.getName());
            handlerRequest.setKey(key);
            handlerRequest.setInterfaceClientValue(interfaceClientValue);
            handlerRequest.setParameterCount(parameterCount);
            handlerRequest.init();
            this.handlerRequest=handlerRequest;
         }




    }

    public HandlerRequest getHandlerRequest() {
        return handlerRequest;
    }
}
