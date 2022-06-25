package org.spring.ext.interfacecall;


import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * springmvc 集成需要的注解 启用快速调用三方接口的注解
 * @author 87260
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ImportCallBeanDefinitionRegistrar.class)
public @interface EnableInterfaceCall {
    //默认扫描需要使用的代理接口类路径
    String[] basePackage() default {};

    @AliasFor(value = "value")
    //调用第三方接口 方法注解url使用properties文件路径
    String[] locations() default {};

    @AliasFor(value = "locations")
    String[] value() default {};

    //调用第三方接口使用的RestTemplate class类
    Class<? extends ApiRestTemplate> restTemplateClass() default ApiRestTemplate.class;








}
