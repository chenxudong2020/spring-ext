package org.spring.ext.interfacecall.proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 87260
 */
public class ProxyRegistrar implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;




    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ProxyDataConfiguration proxyDataConfiguration=beanFactory.getBean(ProxyDataConfiguration.class);
        ProxyDataSource proxyDataSource= proxyDataConfiguration.getProxyDataSource();
        ProxyRestTemplate proxyRestTemplate=proxyDataConfiguration.getProxyRestTemplate();
        Map<String,String>  proxyData =proxyDataSource.getProxyData();

        if(null!=proxyData){
            this.registerProxyServletBean(proxyData,proxyRestTemplate,beanDefinitionRegistry);
        }

    }


    private void registerProxyServletBean(Map<String,String> proxyData,ProxyRestTemplate proxyRestTemplate,BeanDefinitionRegistry registry){
        Map<String, ServletWrappingController> registerHandlers=new HashMap<>();
        Set<String> set=proxyData.keySet();
        for(String name:set) {
            String proxyUrlMapping = name;
            String proxy = proxyData.get(name);
            if (proxy == null) {
                throw new ProxyInitException("proxy配置不能为空");
            }
            if (proxy.toLowerCase().indexOf("http") == -1 && proxy.toLowerCase().indexOf("https") == -1) {
                throw new ProxyInitException("proxy配置必须以http或者https开头！");
            }

            GenericBeanDefinition proxyControlleGenericBeanDefinition=new GenericBeanDefinition();
            proxyControlleGenericBeanDefinition.setBeanClass(ProxyController.class);
            proxyControlleGenericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(proxyRestTemplate);
            proxyControlleGenericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(proxyUrlMapping);
            proxyControlleGenericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(proxy);
            if(registry.containsBeanDefinition(proxyUrlMapping)){
                registry.removeBeanDefinition(proxyUrlMapping);
            }
            registry.registerBeanDefinition(proxyUrlMapping,proxyControlleGenericBeanDefinition);
            registerHandlers.put(proxyUrlMapping,(ServletWrappingController)beanFactory.getBean(proxyUrlMapping));
        }

        GenericBeanDefinition servletRegistrationBeanGenericBeanDefinition=new GenericBeanDefinition();
        servletRegistrationBeanGenericBeanDefinition.setBeanClass(ProxyUrlHandlerMapping.class);
        servletRegistrationBeanGenericBeanDefinition.getConstructorArgumentValues().addGenericArgumentValue(registerHandlers);
        if(registry.containsBeanDefinition(ProxyUrlHandlerMapping.class.getName())){
            registry.removeBeanDefinition(ProxyUrlHandlerMapping.class.getName());
        }
        registry.registerBeanDefinition(ProxyUrlHandlerMapping.class.getName(),servletRegistrationBeanGenericBeanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {


    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }
}
