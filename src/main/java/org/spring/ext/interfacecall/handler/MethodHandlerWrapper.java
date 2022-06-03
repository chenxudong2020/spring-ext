package org.spring.ext.interfacecall.handler;

import org.spring.ext.interfacecall.ApiRestTemplate;
import org.spring.ext.interfacecall.CallProperties;
import org.spring.ext.interfacecall.annotation.Cache;
import org.spring.ext.interfacecall.entity.CacheMeta;
import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodHandlerWrapper implements MethodHandler {


    private MethodHandler methodHandler;
    private MethodMeta methodMeta;
    private Object[] args;

    private BeanFactory beanFactory;
    private CallProperties callProperties;
    private String className;
    private Class<? extends ApiRestTemplate> restTemplateClass;
    private Class callBackClass;
    private boolean isCallBack;

    public MethodHandlerWrapper(MethodHandler methodHandler, MethodMeta method, Object[] args, BeanFactory beanFactory, CallProperties callProperties, String className, Class<? extends ApiRestTemplate> restTemplateClass, Class callBackClass, boolean isCallBack) {
        this.methodHandler = methodHandler;
        this.methodMeta = method;
        this.args = args;
        this.beanFactory = beanFactory;
        this.callProperties = callProperties;
        this.className = className;
        this.restTemplateClass=restTemplateClass;
        this.callBackClass=callBackClass;
        this.isCallBack=isCallBack;
    }


    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        String key = methodMeta.methodName;
        if (methodMeta.cache != null) {
            Cache cache = methodMeta.cache;
            long expire = cache.expire();
            for (Object obj : args) {
                key += obj.toString();
            }
            CacheHandler cacheHandler=beanFactory.getBean(CacheHandler.class);
            CacheMeta cacheMeta = cacheHandler.cacheList.get(key);
            Long currentTime=System.currentTimeMillis();
            boolean isExpire=cacheMeta != null && (currentTime - cacheMeta.currentTime >= cache.expire());
            if (cacheMeta == null || isExpire) {
                cacheMeta = new CacheMeta();
                cacheMeta.cache = cache;
                cacheMeta.object = invoke(proxy);
                cacheMeta.currentTime = currentTime;
                cacheHandler.cacheList.put(key, cacheMeta);
            }
            return cacheMeta.object;
        }


        return invoke(proxy);
    }


    public Object invoke(Object proxy) throws Throwable {
        String key = methodMeta.methodName;
        String interfaceUrl = callProperties.interfaceUrlMap.get(key);
        String returnName = callProperties.returnMap.get(key);
        Map<String, List<ParameterMeta>> parameterMetaMap = callProperties.parameterMetaMap;
        List<ParameterMeta> parameterMetas = parameterMetaMap.get(key);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType(methodMeta.type == null ? MediaType.APPLICATION_JSON_VALUE : methodMeta.type.value());
        headers.setContentType(type);
        headers.add("Accept", MediaType.ALL_VALUE);
        Map map = new HashMap(16);
        String url = interfaceUrl;
        if (args == null) {
            args = new Object[]{};
        }
        return this.doHandler(parameterMetas, headers, args, url, returnName, beanFactory, type,restTemplateClass,callBackClass,isCallBack,methodMeta.method);
    }


    @Override
    public Object doHandler(List<ParameterMeta> parameterMetas, HttpHeaders headers, Object[] args, String url, String returnName, BeanFactory beanFactory, MediaType type, Class<? extends ApiRestTemplate> restTemplateClass, Class callBackClass, boolean isCallBack, Method method) throws Throwable {
        return methodHandler.doHandler(parameterMetas, headers, args, url, returnName, beanFactory, type,restTemplateClass,callBackClass,isCallBack,method);
    }
}
