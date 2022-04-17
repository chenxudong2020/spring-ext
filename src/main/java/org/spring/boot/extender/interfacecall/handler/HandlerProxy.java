package org.spring.boot.extender.interfacecall.handler;

import org.spring.boot.extender.interfacecall.CallProperties;
import org.spring.boot.extender.interfacecall.annotation.Cache;
import org.spring.boot.extender.interfacecall.entity.CacheMeta;
import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerProxy implements InvocationHandler, MethodHandler {


    private MethodHandler methodHandler;
    private MethodMeta methodMeta;
    private Object[] args;
    private RestTemplate restTemplate;
    private CallProperties callProperties;
    private String className;

    public HandlerProxy(MethodHandler methodHandler, MethodMeta method, Object[] args, RestTemplate restTemplate, CallProperties callProperties, String className) {
        this.methodHandler = methodHandler;
        this.methodMeta = method;
        this.args = args;
        this.restTemplate = restTemplate;
        this.callProperties = callProperties;
        this.className = className;
    }

    @Override
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
            CacheMeta cacheMeta = CacheHandler.getInstance().cacheList.get(key);
            if (cacheMeta == null || (cacheMeta != null && (new Date().getTime() - cacheMeta.currentTime >= cache.expire()))) {
                cacheMeta = new CacheMeta();
                cacheMeta.cache = cache;
                cacheMeta.object = invoke(proxy);
                cacheMeta.currentTime = new Date().getTime();
                CacheHandler.getInstance().cacheList.put(key, cacheMeta);
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
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        Map map = new HashMap();
        String url = interfaceUrl;
        if (args == null) {
            args = new Object[]{};
        }
        return this.doHandler(parameterMetas, headers, args, url, returnName, restTemplate, type);
    }


    @Override
    public Object doHandler(List<ParameterMeta> parameterMetas, HttpHeaders headers, Object[] args, String url, String returnName, RestTemplate restTemplate, MediaType type) throws Throwable {
        return methodHandler.doHandler(parameterMetas, headers, args, url, returnName, restTemplate, type);
    }
}
