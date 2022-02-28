package org.spring.boot.extender.interfacecall;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CallInterfaceFactoryBean<T> implements FactoryBean<T>, EnvironmentAware, ApplicationContextAware {


    private Class<T> callInterface;
    private ApplicationContext applicationContext;
    private Environment environment;

    /**
     * 必须提供构造方法
     *
     * @param callInterface
     */
    public CallInterfaceFactoryBean(Class<T> callInterface) {
        this.callInterface = callInterface;
    }

    @Override
    public T getObject() throws Exception {
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        CallProperties callProperties = CallProperties.getInstance();
        return (T) Proxy.newProxyInstance(callInterface.getClassLoader(),
                new Class<?>[]{callInterface},
                new CallInterfaceHandler(restTemplate, callProperties)

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
        CallProperties callProperties = CallProperties.getInstance();
        Map<String,String> urlMap=new ConcurrentHashMap<>();
        callProperties.interfaceUrlMap.forEach((x,y)->{
            y=environment.resolvePlaceholders(y);
            urlMap.put(x,y);
        });
        callProperties.interfaceUrlMap.putAll(urlMap);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
