package org.spring.ext.interfacecall.proxy;

import org.spring.ext.interfacecall.ApiRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 87260
 */
public class ProxyRestTemplate extends ApiRestTemplate {
    @Override
    public List<HttpMessageConverter<?>> getMessageConverters() {
        StringHttpMessageConverter stringHttpMessageConverter= new StringHttpMessageConverter(StandardCharsets.UTF_8);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.APPLICATION_JSON);
        stringHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        List<HttpMessageConverter<?>> list=super.getMessageConverters();
        list.set(1,stringHttpMessageConverter);
        return list;
    }
}
