package org.spring.ext.interfacecall.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ext.interfacecall.ApiRestTemplate;
import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetHandler implements MethodHandler  {
    private static final Logger LOG = LoggerFactory.getLogger(PostHandler.class);


    @Override
    public Object doHandler(List<ParameterMeta> parameterMetas, HttpHeaders headers, Object[] args, String url, String returnName, BeanFactory beanFactory, MediaType type, Class<? extends ApiRestTemplate> restTemplateClass, Class callBackClass, boolean isCallBack, Method method) throws Throwable {
        Map map=new HashMap(16);
        for(ParameterMeta parameterMeta:parameterMetas){
            if(null!=parameterMeta.head){
                headers.add(parameterMeta.head.value(), args[parameterMeta.parameterCount].toString());
            } else if (null != parameterMeta.url) {
                if(args[parameterMeta.parameterCount] instanceof String) {
                    url = (String) args[parameterMeta.parameterCount];
                }
            }
            else if (null != parameterMeta.query) {
                if(url.indexOf("?")==-1){
                    url+="?";
                }
                url+=String.format("&%s=%s",parameterMeta.query.value(),args[parameterMeta.parameterCount]);

            }
            map.put(parameterMeta.parameterName,args[parameterMeta.parameterCount]);

        }
        LOG.info(String.format("-->get:%s",url));
        HttpEntity formEntity = new HttpEntity<>(headers);
        ApiRestTemplate restTemplate =beanFactory.getBean(restTemplateClass);
        return restTemplate.exchange(url, HttpMethod.GET,formEntity,Class.forName(returnName),map).getBody();
    }
}
