package org.spring.ext.interfacecall.annotation;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterfaceClient {

   String value() default "";
}