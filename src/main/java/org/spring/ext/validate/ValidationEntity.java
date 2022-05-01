package org.spring.ext.validate;

import org.spring.ext.validate.result.ResultConvertor;

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
