package org.spring.ext.interfacecall.annotation;

import java.lang.annotation.*;

/**
 * @author 87260
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Body {

    String value() default "";
}
