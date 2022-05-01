package org.spring.ext.interfacecall.handler;

import org.spring.ext.interfacecall.entity.CacheMeta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheHandler {

    private CacheHandler() {
    }

    private static final CacheHandler cacheHandler = new CacheHandler();

    public static CacheHandler getInstance() {
        return cacheHandler;
    }

    public Map<String, CacheMeta> cacheList = new ConcurrentHashMap<>(500);


}
