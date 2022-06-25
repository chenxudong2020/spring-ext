package org.spring.ext.interfacecall.proxy;

public class ProxyDataConfiguration {

    /**
     * 获取代理数据的接口
     * @return
     */
    public ProxyDataSource getProxyDataSource(){
        DefaultProxyDataSource defaultProxyDataSource=new DefaultProxyDataSource();
        return defaultProxyDataSource;
    }
}
