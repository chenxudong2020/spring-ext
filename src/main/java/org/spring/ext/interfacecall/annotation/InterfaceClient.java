package org.spring.ext.interfacecall.annotation;

import java.lang.annotation.*;


/**
 * @author 87260
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterfaceClient {

   String value() default "";

   Class<?> callBackClass() default java.lang.Class.class;
}