package org.spring.ext.interfacecall.proxy;

import org.spring.ext.interfacecall.ApiRestTemplate;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

/**
 * @author 87260
 */
public class ProxyRestTemplate extends ApiRestTemplate {
    @Override
    public List<HttpMessageConverter<?>> getMessageConverters() {
        ByteArrayHttpMessageConverter byteArrayHttpMessageConverter=new ByteArrayHttpMessageConverter();
        List<HttpMessageConverter<?>> list=super.getMessageConverters();
        list.set(1,byteArrayHttpMessageConverter);
        return list;
    }
}
