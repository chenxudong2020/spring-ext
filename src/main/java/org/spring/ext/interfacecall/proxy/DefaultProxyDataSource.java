package org.spring.ext.interfacecall.proxy;

import org.spring.ext.interfacecall.exception.InterfaceCallInitException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 默认从当前classpath下获取proxy。properties中获取代理数据
 * @author 87260
 */
public class DefaultProxyDataSource implements ProxyDataSource, ResourceLoaderAware {
    ResourceLoader resourceLoader;
    @Override
    public Hashtable<String, String> getProxyData() {
        Resource resource=resourceLoader.getResource("classpath:proxy.properties");
        Properties prop = new Properties();
        try {
            prop.load(resource.getInputStream());
        } catch (IOException e) {
            throw new InterfaceCallInitException(e);
        }
        Hashtable<String, String> hashtable=new Hashtable<>();
        Set<Map.Entry<Object,Object>> set=prop.entrySet();
        for(Map.Entry<Object,Object> map:set){
            hashtable.put((String)map.getKey(),(String)map.getValue());
        }
        return hashtable;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
         this.resourceLoader=resourceLoader;
    }
}
