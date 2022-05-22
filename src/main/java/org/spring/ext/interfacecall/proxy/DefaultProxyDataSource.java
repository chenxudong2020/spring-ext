package org.spring.ext.interfacecall.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 默认从当前classpath下获取proxy.properties中获取代理数据
 * @author 87260
 */
public class DefaultProxyDataSource implements ProxyDataSource {

    @Override
    public Map<String, String> getProxyData() {
        Map<String,String> map=new HashMap<>(16);
        ResourceBundle resource= ResourceBundle.getBundle("proxy");
        Set<String> set=resource.keySet();
        for(String key:set){
            map.put(key,resource.getString(key));
        }
        return map;
    }


}
