package org.spring.ext.interfacecall;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
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

/**
 * @author 87260
 */
public class CallInterfaceFactoryBean<T> implements FactoryBean<T>,EnvironmentAware {


    private Class<T> callInterface;
    private BeanFactory beanFactory;
    private Environment environment;
    private List<Object> listResource;
    private Class<? extends ApiRestTemplate> restTemplateClass;
    private Class callBackClass;
    private boolean isCallBack;
    private CallInterfaceHandler callInterfaceHandler;


    public CallInterfaceFactoryBean(Class<T> callInterface, List<Object> listResource, BeanFactory beanFactory, Class<? extends ApiRestTemplate> restTemplateClass, Class callBackClass, boolean isCallBack,CallInterfaceHandler callInterfaceHandler) {
        this.listResource=listResource;
        this.callInterface = callInterface;
        this.beanFactory=beanFactory;
        this.restTemplateClass=restTemplateClass;
        this.callBackClass=callBackClass;
        this.isCallBack=isCallBack;
        this.callInterfaceHandler=callInterfaceHandler;

    }

    @Override
    public T getObject(){
        callInterfaceHandler.setCallBack(isCallBack);
        callInterfaceHandler.setCallBackClass(callBackClass);
        callInterfaceHandler.setClassName(callInterface.getName());
        return (T) Proxy.newProxyInstance(callInterface.getClassLoader(),
                new Class<?>[]{callInterface},
                callInterfaceHandler
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
        Map<String, String> urlMap = new ConcurrentHashMap<>(16);
        callProperties.interfaceUrlMap.forEach((x, y) -> {
            y = environment.resolvePlaceholders(y);
            urlMap.put(x, y);
        });
        callProperties.interfaceUrlMap.putAll(urlMap);
        callProperties.isCached=true;
    }
}
