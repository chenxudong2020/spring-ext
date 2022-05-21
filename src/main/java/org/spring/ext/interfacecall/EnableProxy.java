package org.spring.ext.interfacecall;


import org.spring.ext.interfacecall.proxy.DefaultProxyDataSource;
import org.spring.ext.interfacecall.proxy.ProxyDataSource;
import org.spring.ext.interfacecall.proxy.ProxyRestTemplate;
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
@Import(ImportProxyBeanDefinitionRegistrar.class)
public @interface EnableProxy {

    @AliasFor(value = "proxyRestTemplate")
    Class<? extends ProxyRestTemplate> value() default ProxyRestTemplate.class;
    //是否启用代理默认调用RestTemplate Class类
    @AliasFor(value = "value")
    Class<? extends ProxyRestTemplate> proxyRestTemplate() default ProxyRestTemplate.class;

    //代理数据获取接口 默认DefaultProxyDataSource
    Class<? extends ProxyDataSource> proxyDataSource() default DefaultProxyDataSource.class;


}
