package org.spring.boot.extender.interfacecall.handler;

import org.spring.boot.extender.interfacecall.CallProperties;
import org.spring.boot.extender.interfacecall.annotation.Cache;
import org.spring.boot.extender.interfacecall.entity.CacheMeta;
import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public interface MethodHandler {
    default Object invoke(Object proxy, MethodMeta method, Object[] args, RestTemplate restTemplate, CallProperties callProperties, String className, Map<String,String> interfaceUrlMap) throws Throwable {
        String key = method.methodName;
        if (method.cache != null) {
            Cache cache = method.cache;
            long expire = cache.expire();
            for(Object obj:args){
                key+=obj.toString();
            }
            CacheMeta cacheMeta = CacheHandler.getInstance().cacheList.get(key);
            if (cacheMeta == null || (cacheMeta != null && (new Date().getTime() - cacheMeta.currentTime >= cache.expire()))) {
                cacheMeta=new CacheMeta();
                cacheMeta.cache = cache;
                cacheMeta.object = doHandler(proxy, method, args, restTemplate, callProperties, className,  interfaceUrlMap);
                cacheMeta.currentTime = new Date().getTime();
                CacheHandler.getInstance().cacheList.put(key,cacheMeta);
            }
            return cacheMeta.object;
        }

        return doHandler(proxy, method, args, restTemplate, callProperties, className,interfaceUrlMap);
    }

    Object doHandler(Object proxy, MethodMeta method, Object[] args, RestTemplate restTemplate, CallProperties callProperties, String className, Map<String,String> interfaceUrlMap) throws Throwable;


}
