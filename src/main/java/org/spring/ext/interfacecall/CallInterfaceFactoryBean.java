package org.spring.ext.interfacecall;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallInterfaceFactoryBean<T> implements FactoryBean<T>,EnvironmentAware {


    private Class<T> callInterface;
    private BeanFactory beanFactory;
    private Environment environment;
    private List<Object> listResource;
    private Class<? extends APIRestTemplate> restTemplateClass;


    public CallInterfaceFactoryBean(Class<T> callInterface,List<Object> listResource,BeanFactory beanFactory, Class<? extends APIRestTemplate> restTemplateClass) {
        this.listResource=listResource;
        this.callInterface = callInterface;
        this.beanFactory=beanFactory;
        this.restTemplateClass=restTemplateClass;

    }

    @Override
    public T getObject(){
        return (T) Proxy.newProxyInstance(callInterface.getClassLoader(),
                new Class<?>[]{callInterface},
                new CallInterfaceHandler(beanFactory,callInterface.getName(),restTemplateClass)

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
    public void setEnvironment(Environment environment) {
        this.environment=environment;
        this.resolvePlaceholders();
    }


    protected void addResourceEnvironment(Object location, Environment environment){
        if(location!=null){
            ConfigurableEnvironment configurableEnvironment=(ConfigurableEnvironment)environment;
            try {
                DefaultResourceLoader defaultResourceLoader=new DefaultResourceLoader();
                Resource resource=defaultResourceLoader.getResource((String)location);
                configurableEnvironment.getPropertySources().addFirst(new ResourcePropertySource(new EncodedResource(resource)));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    protected void addResourceEnvironment(List<Object> listResource,Environment environment){
        if(listResource!=null){
            for(Object location:listResource){
                this.addResourceEnvironment(location,environment);
            }
        }

    }


    protected void resolvePlaceholders(){
        CallProperties callProperties = beanFactory.getBean(CallProperties.class);
        if(callProperties.isCached){return;}
        this.addResourceEnvironment(listResource,environment);
        Map<String, String> urlMap = new ConcurrentHashMap<>();
        callProperties.interfaceUrlMap.forEach((x, y) -> {
            y = environment.resolvePlaceholders(y);
            urlMap.put(x, y);
        });
        callProperties.interfaceUrlMap.putAll(urlMap);
        callProperties.isCached=true;
    }
}
