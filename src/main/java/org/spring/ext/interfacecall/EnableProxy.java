package org.spring.ext.interfacecall;


import org.springframework.context.annotation.Import;

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

}
