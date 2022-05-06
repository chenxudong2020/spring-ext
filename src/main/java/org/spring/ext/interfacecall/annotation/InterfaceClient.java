package org.spring.ext.interfacecall.annotation;

import org.spring.ext.interfacecall.APIRestTemplate;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterfaceClient {

   String value() default "";

   Class<?> callBackClass() default java.lang.Class.class;
}