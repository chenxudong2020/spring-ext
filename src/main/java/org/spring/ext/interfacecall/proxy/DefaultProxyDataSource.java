package org.spring.ext.interfacecall.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class DefaultProxyDataSource implements ProxyDataSource{

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
