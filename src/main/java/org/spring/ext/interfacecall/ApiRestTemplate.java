package org.spring.ext.interfacecall;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


/**
 * @author 87260
 */
public class ApiRestTemplate extends RestTemplate{

    /**
     * 修改默认的jackson转换器 序列化和非序列化忽略不存在字段
     * 段少于实体类字段：正常通过，没有的字段会被赋予默认值
     * 当字段多于实体类字段   此时会报错，这个错翻译过来就是，一个无法识别的field。
     * @return
     */
    @Override
    public List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> list=super.getMessageConverters();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter=new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.getObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        List<HttpMessageConverter<?>> newList=new ArrayList<>(list);
        for(int i=0;i<list.size();i++){
            HttpMessageConverter httpMessageConverter=list.get(i);
            if(httpMessageConverter instanceof MappingJackson2HttpMessageConverter){
                newList.remove(httpMessageConverter);
                newList.add(i,mappingJackson2HttpMessageConverter);
            }
        }

        return newList;
    }
}
