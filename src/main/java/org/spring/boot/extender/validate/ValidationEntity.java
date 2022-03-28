package org.spring.boot.extender.validate;

import org.spring.boot.extender.validate.result.ResultConvertor;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.PARAMETER;

@Documented
@Inherited
@Target(PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ValidationEntity {

    Class<?> value() ;

    Class<? extends ResultConvertor> result() default ResultConvertor.class;

}
