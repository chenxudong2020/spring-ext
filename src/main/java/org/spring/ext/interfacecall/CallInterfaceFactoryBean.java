package org.spring.ext.interfacecall;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.*;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallInterfaceFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware,EnvironmentAware {


    private Class<T> callInterface;
    private ApplicationContext applicationContext;
    private Environment environment;



    public CallInterfaceFactoryBean(Class<T> callInterface) {
        this.callInterface = callInterface;

    }

    @Override
    public T getObject(){
        return (T) Proxy.newProxyInstance(callInterface.getClassLoader(),
                new Class<?>[]{callInterface},
                new CallInterfaceHandler(applicationContext,callInterface.getName())

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
        this.applicationContext=applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
        this.resolvePlaceholders();
    }



    protected void resolvePlaceholders(){
        CallProperties callProperties = CallProperties.getInstance();
        if(callProperties.isCached){return;}
        Map<String, String> urlMap = new ConcurrentHashMap<>();
        callProperties.interfaceUrlMap.forEach((x, y) -> {
            y = environment.resolvePlaceholders(y);
            urlMap.put(x, y);
        });
        callProperties.interfaceUrlMap.putAll(urlMap);
        callProperties.isCached=true;
    }
}
