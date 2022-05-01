package org.spring.ext.interfacecall.handler;


import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.util.*;

public class PostHandler implements MethodHandler {
    private static final Logger LOG = LoggerFactory.getLogger(PostHandler.class);

    @Override
    public Object doHandler(List<ParameterMeta> parameterMetas, HttpHeaders headers, Object args[], String url, String returnName, ApplicationContext applicationContext, MediaType type) throws Throwable {
        int bodyCount = 0;
        List<Object> listObjs = new ArrayList<>(Arrays.asList(args));
        for (ParameterMeta parameterMeta : parameterMetas) {
            if (null != parameterMeta.head) {
                headers.add(parameterMeta.head.value(), args[parameterMeta.parameterCount].toString());
                listObjs.remove(args[parameterMeta.parameterCount]);
            } else if (null != parameterMeta.body) {
                bodyCount = parameterMeta.bodyCount;
            } else if (null != parameterMeta.url) {
                if (args[parameterMeta.parameterCount] instanceof String) {
                    url = (String) args[parameterMeta.parameterCount];
                    listObjs.remove(args[parameterMeta.parameterCount]);
                }
            } else if (null != parameterMeta.query) {
                if (url.indexOf("?") == -1) {
                    url += "?";
                }
                url += String.format("&%s=%s", parameterMeta.query.value(), args[parameterMeta.parameterCount]);
                listObjs.remove(args[parameterMeta.parameterCount]);
            }
        }
        LOG.info(String.format("-->post:%s", url));
        args = listObjs.toArray();
        //TODO 20220323 解决"application/x-www-form-urlencoded"调用传参bug
        MultiValueMap<Object, Object> map = new LinkedMultiValueMap<>();
        if (type.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
            for (Object obj : args) {
                Object jsonObject = obj;
                if (obj instanceof String) {
                    jsonObject = JSONObject.parseObject((String) obj);
                    for (Map.Entry entry : ((JSONObject) jsonObject).entrySet()) {
                        map.add(entry.getKey(), entry.getValue());
                    }
                } else {
                    BeanMap beanMap = BeanMap.create(obj);
                    for (Object x : beanMap.keySet()) {
                        map.add(x, (beanMap.get(x) instanceof String) ? beanMap.get(x) : JSONObject.toJSONString(beanMap.get(x)));
                    }

                }

            }
            args[0] = map;

        }
        HttpEntity formEntity = new HttpEntity<>(args.length == 0 ? null : args.length == 1 ? args[0] : args, headers);
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        return restTemplate.postForObject(url, formEntity, Class.forName(returnName));
    }
}