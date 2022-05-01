package org.spring.boot.extender.interfacecall.paramhandler;


import org.spring.boot.extender.interfacecall.annotation.Body;
import org.spring.boot.extender.interfacecall.annotation.Head;
import org.spring.boot.extender.interfacecall.annotation.Query;
import org.spring.boot.extender.interfacecall.annotation.Url;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;

import java.lang.reflect.Parameter;

public class HeadHandler extends HandlerChain {

    private HandlerChain handler;

    @Override
    public void setNext(HandlerChain handler) {
        this.handler = handler;
    }

    @Override
    public HandlerChain handler(HandlerRequest request) {
        ParameterMeta parameterMeta = request.getParameterMeta();
        Parameter parameter = request.getParameter();
        Head head = parameter.getAnnotation(Head.class);
        validate(request);
        if (null != head) {
            parameterMeta.head = head;
        }

        return handler;

    }

    @Override
    public void validate(HandlerRequest request) {
        Parameter parameter = request.getParameter();
        String parameterName = parameter.getName();
        Body body = parameter.getAnnotation(Body.class);
        Head head = parameter.getAnnotation(Head.class);
        Query query = parameter.getAnnotation(Query.class);
        Url url = parameter.getAnnotation(Url.class);
        boolean isNull = ( head == null);
        boolean isValidate = (head != null && url == null && body == null && query == null);
        if (!isNull && isValidate) {
            if (!isValidate) {
                throw new RuntimeException("head不能和其他，注解同一个参数:" + parameterName + "!");
            }

        }
    }
}
