package org.spring.boot.extender.validate;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.PARAMETER;

@Documented
@Inherited
@Target(PARAMETER)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ValidationEntity {

    Class<?> value() ;

}
