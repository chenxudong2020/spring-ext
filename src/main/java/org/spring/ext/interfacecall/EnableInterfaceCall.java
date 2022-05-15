package org.spring.ext.interfacecall;


import org.spring.ext.interfacecall.proxy.ProxyRestTemplate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * springmvc 集成需要的注解
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


    //是否启用代理模块 默认false
    boolean proxyEnable() default false;


    //是否启用代理默认调用RestTemplate Class类
    Class<? extends ProxyRestTemplate> proxyRestTemplate() default ProxyRestTemplate.class;

}
