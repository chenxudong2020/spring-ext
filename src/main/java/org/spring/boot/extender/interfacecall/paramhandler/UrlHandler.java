package org.spring.boot.extender.interfacecall.paramhandler;


import org.spring.boot.extender.interfacecall.annotation.*;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;

import java.lang.reflect.Parameter;

public class UrlHandler extends HandlerChain{

    private HandlerChain handler;

    @Override
    public void setNext(HandlerChain handler) {
        this.handler = handler;
    }

    @Override
    public HandlerChain handler(HandlerRequest request) {
        ParameterMeta parameterMeta=request.getParameterMeta();
        Parameter parameter=request.getParameter();
        Url url = parameter.getAnnotation(Url.class);
        validate(request);
        if (null != url) {
            parameterMeta.url=url;
        }
         return handler;
    }

    @Override
    public void validate(HandlerRequest request) {
        Parameter parameter=request.getParameter();
        String parameterName=parameter.getName();
        Body body = parameter.getAnnotation(Body.class);
        Head head = parameter.getAnnotation(Head.class);
        Query query=parameter.getAnnotation(Query.class);
        Url url=parameter.getAnnotation(Url.class);
        String key=request.getKey();
        boolean isNull=(url==null);
        boolean isValidate=(url!=null&&body==null&&head==null&&query==null);
        if(!isNull){
            if(!isValidate){
                throw new RuntimeException(key+" url不能和其他，注解同一个参数:"+parameterName+"!");
            }

        }

    }
}
