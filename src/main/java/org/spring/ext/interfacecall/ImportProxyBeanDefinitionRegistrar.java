package org.spring.ext.interfacecall;

import org.spring.ext.interfacecall.proxy.ProxyRegistrar;
import org.spring.ext.interfacecall.proxy.ProxyRestTemplate;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class ImportProxyBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        MultiValueMap<String, Object> map=importingClassMetadata.getAllAnnotationAttributes(EnableProxy.class.getName());
        Class<? extends ProxyRestTemplate> proxyRestTemplateClass=ProxyRestTemplate.class;
        if(map!=null){
            List<Object> proxyRestTemplateList=map.get("proxyRestTemplate");
            if(proxyRestTemplateList!=null) {
                for (Object proxyRestTemplateObject : proxyRestTemplateList) {
                    proxyRestTemplateClass=(Class<? extends ProxyRestTemplate>)proxyRestTemplateObject;
                    break;
                }
            }
        }
        this.registerBean(proxyRestTemplateClass,registry);
        GenericBeanDefinition genericBeanDefinition=new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(ProxyRegistrar.class);
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        genericBeanDefinition.getPropertyValues().add("proxyRestTemplateClass",proxyRestTemplateClass);
        registry.registerBeanDefinition(ProxyRegistrar.class.getName(),genericBeanDefinition);


    }

    private void registerBean(Class classz,BeanDefinitionRegistry registry){
        GenericBeanDefinition genericBeanDefinition=new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(classz);
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(classz.getName(),genericBeanDefinition);
    }
}
