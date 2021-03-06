package org.spring.ext.interfacecall.paramhandler;



import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.spring.ext.interfacecall.annotation.Body;
import org.spring.ext.interfacecall.annotation.Head;
import org.spring.ext.interfacecall.annotation.Query;
import org.spring.ext.interfacecall.annotation.Url;
import org.spring.ext.interfacecall.exception.InterfaceCallInitException;

import java.lang.reflect.Parameter;

/**
 * @author 87260
 */
public class BodyAbstractHandler extends AbstractHandlerChain {

    private AbstractHandlerChain handler;

    @Override
    public void setNext(AbstractHandlerChain handler) {
        this.handler = handler;
    }


    @Override
    public AbstractHandlerChain handler(HandlerRequest request) {
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
                throw new InterfaceCallInitException(key + " body不能和其他，注解同一个参数:" + parameterName + "!");
            }

        }

    }
}
