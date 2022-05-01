package org.spring.ext.interfacecall.annotation;

import java.lang.annotation.*;
//缓存接口结果,参数需要重写toString()方法，过期时间默认两个小时
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    //过期时间
    long expire() default 7200l;

}
