package org.spring.boot.extender.validate;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.extender.validate.result.Result;
import org.spring.boot.extender.validate.result.ResultConvertor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Post请求参数Validate框架验证
 */
@Aspect
@Primary
@Order(Integer.MAX_VALUE)
public class ValidateControllerHandler {

    private static final Logger logger = LoggerFactory.getLogger(ValidateControllerHandler.class);

    private Class<? extends ResultConvertor> resultRestMessage;


    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping))")
    public void controllerAspect() {
    }


    @Around("controllerAspect()")
    public Object logEis(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        Object[] objs = joinPoint.getArgs();
        if (objs.length != 0) {
           ValidationResult validationResult = new ValidationResult();
            for (Object param : objs) {
                if (param instanceof String) {
                    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                    Method method = signature.getMethod();
                    //TODO 判断是否要POST请求
                    RequestMapping requestMapping=method.getAnnotation(RequestMapping.class);
                    if(!HttpMethod.POST.equals(requestMapping.method())){
                        break;
                    }
                    Annotation[][] annotations = method.getParameterAnnotations();
                    for (int i = 0; i < annotations.length; i++) {
                        Annotation[] paramAnn = annotations[i];
                        //参数为空，直接下一个参数
                        if(param == null || paramAnn.length == 0){
                            continue;
                        }
                        for (Annotation annotation : paramAnn) {
                            if(annotation.annotationType().equals( ValidationEntity.class)){
                                //校验该参数
                                //TODO 校验参数
                                 ValidationEntity validationEntity = (ValidationEntity)annotation;
                                if (validationEntity != null) {
                                    Class<?> cla = validationEntity.value();
                                    resultRestMessage=validationEntity.result();
                                    String parmas = (String) param;
                                    ObjectMapper mapper=new ObjectMapper();
                                    validationResult = ValidationUtils.validateEntity(mapper.readValue(parmas,cla));
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
                return convertResult(false, String.join(",", validationResult.getErrorMsg().values()));
             }
        }

            result = joinPoint.proceed();
            return result;
    }

    protected Object convertResult(Boolean isSuccess, String message) throws Throwable {
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage(message);
        ResultConvertor obj = resultRestMessage.newInstance();
        return obj.convert(result);
    }


}
