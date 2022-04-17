package org.spring.boot.extender.interfacecall.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.extender.interfacecall.CallProperties;
import org.spring.boot.extender.interfacecall.annotation.Cache;
import org.spring.boot.extender.interfacecall.entity.CacheMeta;
import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetHandler implements MethodHandler  {
    private static final Logger LOG = LoggerFactory.getLogger(PostHandler.class);


    @Override
    public Object doHandler(List<ParameterMeta> parameterMetas,HttpHeaders headers,Object args[],String url,String returnName,RestTemplate restTemplate,MediaType type) throws Throwable {
        Map map=new HashMap();
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
        return restTemplate.exchange(url, HttpMethod.GET,formEntity,Class.forName(returnName),map).getBody();
    }
}
