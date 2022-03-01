package org.spring.boot.extender.interfacecall;

import org.spring.boot.extender.interfacecall.annotation.Body;
import org.spring.boot.extender.interfacecall.entity.MethodMeta;
import org.spring.boot.extender.interfacecall.entity.ParameterMeta;
import org.spring.boot.extender.invoker.bean.Result;
import org.springframework.aop.support.AopUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallInterfaceHandler  implements InvocationHandler {
    private RestTemplate restTemplate;
    private CallProperties callProperties;
    private String className;

    public CallInterfaceHandler(RestTemplate restTemplate, CallProperties callProperties,String className) {
        this.restTemplate = restTemplate;
        this.callProperties = callProperties;
        this.className=className;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this,args);
        }
        String key=String.format("%s-%s",className,method.getName());
        String interfaceUrl=callProperties.interfaceUrlMap.get(key);
        String returnName=callProperties.returnMap.get(key);
        MethodMeta methodMeta=callProperties.methodMetaMap.get(key);
        Map<String, List<ParameterMeta>> parameterMetaMap=callProperties.parameterMetaMap;
        List<ParameterMeta> parameterMetas=parameterMetaMap.get(key);
        int bodyCount=0;
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        Map map=new HashMap();
        for(ParameterMeta parameterMeta:parameterMetas){
            if(null!=parameterMeta.head){
                headers.add(parameterMeta.head.value(), args[parameterMeta.parameterCount].toString());
            }else if(null!=parameterMeta.body){
                bodyCount= parameterMeta.bodyCount;
            }
            map.put(parameterMeta.parameterName,args[parameterMeta.parameterCount]);

        }

        if(methodMeta.post!=null){
            HttpEntity formEntity = new HttpEntity<>(args[bodyCount], headers);
            return restTemplate.postForObject(interfaceUrl,formEntity, Class.forName(returnName));
        }else if(methodMeta.get!=null){
            HttpEntity formEntity = new HttpEntity<>(headers);
            return restTemplate.exchange(interfaceUrl, HttpMethod.GET,formEntity,Class.forName(returnName),map).getBody();
         }
        return null;


    }
}
