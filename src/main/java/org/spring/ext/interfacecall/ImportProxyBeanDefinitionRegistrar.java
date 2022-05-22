package org.spring.ext.interfacecall;

import org.spring.ext.interfacecall.proxy.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * @author 87260
 */
public class ImportProxyBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {
    BeanFactory beanFactory;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        boolean proxyDataConfigurationInit = this.isInBeanFactory(ProxyDataConfiguration.class);
        if (!proxyDataConfigurationInit) {
            this.registerBean(ProxyDataConfiguration.class, registry);
        }

        GenericBeanDefinition genericBeanDefinition=new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(ProxyRegistrar.class);
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(ProxyRegistrar.class.getName(),genericBeanDefinition);


    }


    private boolean isInBeanFactory(Class classz) {
        try {
            beanFactory.getBean(classz);
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    private void registerBean(Class classz,BeanDefinitionRegistry registry){
        GenericBeanDefinition genericBeanDefinition=new GenericBeanDefinition();
        genericBeanDefinition.setBeanClass(classz);
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(classz.getName(),genericBeanDefinition);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }
}
