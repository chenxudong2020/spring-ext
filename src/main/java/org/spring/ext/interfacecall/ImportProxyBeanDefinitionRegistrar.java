package org.spring.ext.interfacecall;

import org.spring.ext.interfacecall.proxy.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import javax.servlet.ServletRegistration;
import java.util.*;

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



        ProxyDataConfiguration proxyDataConfiguration=beanFactory.getBean(ProxyDataConfiguration.class);
        ProxyDataSource proxyDataSource= proxyDataConfiguration.getProxyDataSource();
        Map<String, String> maps= proxyDataSource.getProxyData();
        maps.forEach((x,y)->{
            GenericBeanDefinition genericBeanDefinition=new GenericBeanDefinition();
            genericBeanDefinition.setBeanClass(ProxyServlet.class);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServletRegistrationBean.class);
            builder.addPropertyValue("name", x);
            builder.addPropertyValue("servlet", genericBeanDefinition);
            Map<String, String> initParameters = new LinkedHashMap();
            initParameters.put(ProxyServlet.P_TARGET_URI, y);
            initParameters.put(ProxyServlet.P_LOG, "false");
            builder.addPropertyValue("initParameters", initParameters);
            Set<String> urlMappings=new HashSet<>();
            urlMappings.add(x);
            builder.addPropertyValue("urlMappings", urlMappings);
            registry.registerBeanDefinition(x,builder.getBeanDefinition());
        });




    }

    /**
     * 判断beanFactory是否存在含有指定Class的对象
     * @param classz
     * @return
     */
    private boolean isInBeanFactory(Class classz) {
        return beanFactory.getBeanProvider(classz).stream().count()!=0;
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
