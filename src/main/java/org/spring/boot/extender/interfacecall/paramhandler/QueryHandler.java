package org.spring.boot.extender.interfacecall.paramhandler;


import org.spring.boot.extender.interfacecall.annotation.*;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;

import java.lang.reflect.Parameter;

public class QueryHandler extends HandlerChain{

    private HandlerChain handler;

    @Override
    public void setNext(HandlerChain handler) {
        this.handler = handler;
    }
    @Override
    public HandlerChain handler(HandlerRequest request) {
        ParameterMeta parameterMeta=request.getParameterMeta();
        Parameter parameter=request.getParameter();
        Query query = parameter.getAnnotation(Query.class);
        validate(request);
        if (null != query) {
            parameterMeta.query=query;
        }

         return handler;

    }

    @Override
    public void validate(HandlerRequest request) {
        Parameter parameter=request.getParameter();
        String parameterName=parameter.getName();
        String key=request.getKey();
        Body body = parameter.getAnnotation(Body.class);
        Head head = parameter.getAnnotation(Head.class);
        Query query=parameter.getAnnotation(Query.class);
        Url url=parameter.getAnnotation(Url.class);
        boolean isNull=(query==null);
        boolean isValidate=(query!=null&&url==null&&head==null&&body==null);
        if(!isNull){
            if(!isValidate){
                throw new RuntimeException(key+" query不能和其他，注解同一个参数:"+parameterName+"!");
            }
        }
        //Query注解暂时只支持一个 待扩展
    }
}
