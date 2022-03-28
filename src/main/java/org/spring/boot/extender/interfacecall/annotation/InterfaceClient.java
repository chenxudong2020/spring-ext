package org.spring.boot.extender.interfacecall.annotation;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterfaceClient {

   String value() default "";

   String[] scanBasePackages() default {};
}