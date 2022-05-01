package org.spring.boot.extender.interfacecall.paramhandler;


import org.spring.boot.extender.interfacecall.annotation.*;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;

import java.lang.reflect.Parameter;

public class BodyHandler extends HandlerChain {

    private HandlerChain handler;

    @Override
    public void setNext(HandlerChain handler) {
        this.handler = handler;
    }


    @Override
    public HandlerChain handler(HandlerRequest request) {
        ParameterMeta parameterMeta = request.getParameterMeta();
        Parameter parameter = request.getParameter();
        String parameterName = parameter.getName();
        Body body = parameter.getAnnotation(Body.class);
        validate(request);
        if (null != body) {
            parameterMeta.bodyCount += 1;
            parameterMeta.body = body;
        }

        return handler;

    }

    @Override
    public void validate(HandlerRequest request) {
        Parameter parameter = request.getParameter();
        String parameterName = parameter.getName();
        String key = request.getKey();
        Body body = parameter.getAnnotation(Body.class);
        Head head = parameter.getAnnotation(Head.class);
        Query query = parameter.getAnnotation(Query.class);
        Url url = parameter.getAnnotation(Url.class);
        boolean isNull = (body == null);
        boolean isValidate = (body != null && url == null && head == null && query == null);
        if (!isNull) {
            if (!isValidate) {
                throw new RuntimeException(key + " body不能和其他，注解同一个参数:" + parameterName + "!");
            }

        }

    }
}
