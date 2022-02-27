package org.spring.boot.extender.validate;


import org.spring.boot.extender.validate.result.ResultConvertor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ImportValidateController.class)
public @interface EnableInterfaceValidate {
    String[] basePackage() default {};
    Class<? extends ResultConvertor> result() default ResultConvertor.class;
}
