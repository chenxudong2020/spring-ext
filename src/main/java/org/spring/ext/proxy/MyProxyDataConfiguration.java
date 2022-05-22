package org.spring.ext.proxy;

import org.spring.ext.interfacecall.proxy.DefaultProxyDataSource;
import org.spring.ext.interfacecall.proxy.ProxyDataConfiguration;
import org.spring.ext.interfacecall.proxy.ProxyDataSource;
import org.springframework.context.annotation.Configuration;


/**
 * @author 87260
 */
@Configuration
public class MyProxyDataConfiguration extends ProxyDataConfiguration {

    @Override
    public ProxyDataSource getProxyDataSource(){
        DefaultProxyDataSource defaultProxyDataSource=new DefaultProxyDataSource();
        return defaultProxyDataSource;
    }
}
