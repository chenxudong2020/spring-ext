package org.spring.boot.extender.interfacecall;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallInterfaceFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware, EnvironmentAware {


    private Class<T> callInterface;
    private ApplicationContext applicationContext;
    private Environment environment;
    private List<Object> listResource;

    @Override
    public void setEnvironment(Environment environment) {

         //加载系统中的property文件到environment
        for(Object resourceStringObject:listResource){
            DefaultResourceLoader defaultResourceLoader=new DefaultResourceLoader();
            Resource resource=defaultResourceLoader.getResource((String)resourceStringObject);
            ConfigurableEnvironment configurableEnvironment=(ConfigurableEnvironment)environment;
            try {
                configurableEnvironment.getPropertySources().addFirst(new ResourcePropertySource(new EncodedResource(resource)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        CallProperties callProperties = CallProperties.getInstance();
        Map<String, String> urlMap = new ConcurrentHashMap<>();
        callProperties.interfaceUrlMap.forEach((x, y) -> {
            y = environment.resolvePlaceholders(y);
            urlMap.put(x, y);
        });
        callProperties.interfaceUrlMap.putAll(urlMap);
    }

    /**
     * 必须提供构造方法
     *
     * @param callInterface
     */
    public CallInterfaceFactoryBean(Class<T> callInterface, List<Object> listResource) {
        this.callInterface = callInterface;
        this.listResource=listResource;
    }

    @Override
    public T getObject() throws Exception {
        CallProperties callProperties = CallProperties.getInstance();
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        return (T) Proxy.newProxyInstance(callInterface.getClassLoader(),
                new Class<?>[]{callInterface},
                new CallInterfaceHandler(restTemplate, callProperties.interfaceUrlMap,callInterface.getName())

        );

    }

    @Override
    public Class<?> getObjectType() {
        return callInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
