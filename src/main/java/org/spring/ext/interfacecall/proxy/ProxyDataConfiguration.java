package org.spring.ext.interfacecall.proxy;

/**
 * 默认proxyData获取配置类 如果需要定制需要继承此类重写getProxyDataSource方法,重写getProxyRestTemplate方法
 * @author 87260
 */
public class ProxyDataConfiguration {

    /**
     * 获取代理数据的接口
     * @return
     */
    public ProxyDataSource getProxyDataSource(){
        DefaultProxyDataSource defaultProxyDataSource=new DefaultProxyDataSource();
        return defaultProxyDataSource;
    }

    /**
     * 访问三方接口使用的RestTemplate
     * @return
     */
    public ProxyRestTemplate getProxyRestTemplate(){
        ProxyRestTemplate proxyRestTemplate=new ProxyRestTemplate();
        return proxyRestTemplate;
    }
}
