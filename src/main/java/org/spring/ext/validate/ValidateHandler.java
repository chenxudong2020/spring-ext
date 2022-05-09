package org.spring.ext.validate;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ext.validate.result.Result;
import org.spring.ext.validate.result.ResultContain;
import org.spring.ext.validate.result.ResultConvertor;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ValidateHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(ValidateHandler.class);


    private Object target;
    private Class<? extends ResultConvertor> resultRestMessage;

    public ValidateHandler(Object target, Class<? extends ResultConvertor> resultRestMessage) {
        this.target = target;
        this.resultRestMessage = resultRestMessage;
    }

    protected Object validateParams(Object[] objs, Method method) throws Throwable {
        Object result = null;
        if (objs.length != 0) {
            ValidationResult validationResult = new ValidationResult();
            for (Object param : objs) {
                if (param instanceof String) {
                    Annotation[][] annotations = method.getParameterAnnotations();
                    for (int i = 0; i < annotations.length; i++) {
                        Annotation[] paramAnn = annotations[i];
                        //参数为空，直接下一个参数
                        if (param == null || paramAnn.length == 0) {
                            continue;
                        }
                        for (Annotation annotation : paramAnn) {
                            if (annotation.annotationType().equals(ValidationEntity.class)) {
                                //校验该参数
                                //TODO 校验参数
                                ValidationEntity validationEntity = (ValidationEntity) annotation;
                                if (validationEntity != null) {
                                    Class<?> cla = validationEntity.value();
                                    String parmas = (String) param;
                                    validationResult = ValidationUtils.validateEntity(JSON.parseObject(parmas,cla));
                                    if (validationResult.isHasErrors()) {
                                        break;
                                    }
                                }

                            }
                        }
                    }


                } else {
                    validationResult = ValidationUtils.validateEntity(param);
                    if (validationResult.isHasErrors()) {
                        break;
                    }
                }

            }
            if (validationResult.isHasErrors()) {
                Class returnClass = method.getReturnType();
                return convertResult(false, String.join(",", validationResult.getErrorMsg().values()), returnClass);
            }
        }

        result = method.invoke(target, objs);
        return result;
    }

    protected Object convertResult(Boolean isSuccess, String message, Class returnClass) throws Throwable {

        Result result = new Result();
        result.setSuccess(false);
        result.setMessage(message);
        ResultConvertor obj = resultRestMessage.newInstance();
        Object returnObj = obj.convert(result);
        if (returnObj.getClass() == returnClass) {
            return returnObj;
        } else {
            Object returnClsObj = returnClass.newInstance();
            BeanMap beanMap = BeanMap.create(returnClsObj);
            ResultContain.values();
            String defaultResultMessage="message";
            String defaultResultMsg="msg";
            for(ResultContain resultContain:ResultContain.values()){
                if (beanMap.containsKey(resultContain.getResultType()) && returnClass.getDeclaredField(resultContain.getResultType()).getType() == resultContain.getDefaultType()) {
                    beanMap.put(resultContain.getResultType(),defaultResultMessage.equals(resultContain.getResultType())||defaultResultMsg.equals(resultContain.getResultType())?message:resultContain.getDefaultValue());
                }
            }
            return returnClsObj;
        }


    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return AopUtils.getTargetClass(proxy).toString();
        }
        if ("equals".equals(method.getName())) {
            return method.invoke(AopUtils.getTargetClass(proxy), args);
        }
        if ("hashCode".equals(method.getName())) {
            return AopUtils.getTargetClass(proxy).hashCode();
        }
        return validateParams(args, method);
    }
}
