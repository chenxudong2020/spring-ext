package org.spring.ext.interfacecall.handler;

import org.spring.ext.interfacecall.entity.CacheMeta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheHandler {

    public Map<String, CacheMeta> cacheList = new ConcurrentHashMap<>(5000);


}
