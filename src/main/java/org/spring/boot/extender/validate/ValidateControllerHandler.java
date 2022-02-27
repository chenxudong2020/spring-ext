package org.spring.boot.extender.validate;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.extender.validate.result.RestMessage;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Post请求参数Validate框架验证
 */
/*@Aspect
@Component
@Primary
@Order(Integer.MAX_VALUE)
@ConditionalOnMissingBean(ImportValidateController.class)
*/
public class ValidateControllerHandler {

    private static final Logger logger = LoggerFactory.getLogger(ValidateControllerHandler.class);


    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping))")
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
                return RestMessage.newInstance(false, String.join(",", validationResult.getErrorMsg().values()));
            }
        }

            result = joinPoint.proceed();
            return result;
    }


}
