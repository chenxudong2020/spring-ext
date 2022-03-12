package org.spring.boot.extender.interfacecall.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.boot.extender.interfacecall.CallProperties;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetHandler implements MethodHandler  {
    private static final Logger LOG = LoggerFactory.getLogger(PostHandler.class);
    @Override
    public Object invoke(Object proxy, Method method, Object[] args, RestTemplate restTemplate, CallProperties callProperties, String className) throws Throwable {
        String key=String.format("%s-%s",className,method.getName());
        String interfaceUrl=callProperties.interfaceUrlMap.get(key);
        String returnName=callProperties.returnMap.get(key);
         Map<String, List<ParameterMeta>> parameterMetaMap=callProperties.parameterMetaMap;
        List<ParameterMeta> parameterMetas=parameterMetaMap.get(key);

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        Map map=new HashMap();
        String url=interfaceUrl;
        for(ParameterMeta parameterMeta:parameterMetas){
            if(null!=parameterMeta.head){
                headers.add(parameterMeta.head.value(), args[parameterMeta.parameterCount].toString());
            } else if (null != parameterMeta.url) {
                url = (String)args[parameterMeta.parameterCount];
            }
            map.put(parameterMeta.parameterName,args[parameterMeta.parameterCount]);

        }
        LOG.info(String.format("-->get:%s",url));
        HttpEntity formEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET,formEntity,Class.forName(returnName),map).getBody();
    }
}
