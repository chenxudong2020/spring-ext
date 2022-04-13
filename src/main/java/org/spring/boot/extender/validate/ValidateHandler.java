package org.spring.boot.extender.validate;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.extender.validate.result.Result;
import org.spring.boot.extender.validate.result.ResultConvertor;
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
            if (beanMap.containsKey("message") && returnClass.getDeclaredField("message").getType() == String.class)
                beanMap.put("message", message);
            if (beanMap.containsKey("msg") && returnClass.getDeclaredField("msg").getType() == String.class)
                beanMap.put("msg", message);
            if (beanMap.containsKey("code") && returnClass.getDeclaredField("code").getType() == String.class)
                beanMap.put("code", "");
            if (beanMap.containsKey("code") && returnClass.getDeclaredField("code").getType() == Integer.class)
                beanMap.put("code", 500);
            if (beanMap.containsKey("success") && returnClass.getDeclaredField("success").getType() == Boolean.class)
                beanMap.put("success", false);
            if (beanMap.containsKey("data"))
                beanMap.put("data", null);
            return returnClsObj;
        }


    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return AopUtils.getTargetClass(proxy).toString();
        }
        if (method.getName().equals("equals")) {
            return method.invoke(AopUtils.getTargetClass(proxy), args);
        }
        if (method.getName().equals("hashCode")) {
            return AopUtils.getTargetClass(proxy).hashCode();
        }
        return validateParams(args, method);
    }
}
