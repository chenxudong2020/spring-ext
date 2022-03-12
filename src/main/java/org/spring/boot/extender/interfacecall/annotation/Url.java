package org.spring.boot.extender.interfacecall.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Url {

    String value() default "";
}
