package org.spring.boot.extender.interfacecall.annotation;

import org.springframework.http.MediaType;

import java.awt.*;
import java.lang.annotation.*;

/**
 * 请求头类型
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Type {

    String value() default  MediaType.APPLICATION_JSON_VALUE;
}
