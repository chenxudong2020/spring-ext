package org.spring.boot.extender.validate.result;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 接口返回消息
 * @author cheny
 *
 */
@ApiModel(description= "返回响应数据")
public class RestMessage<T> implements Message,Serializable{
    private static final long serialVersionUID = -1865510446859810360L;
    @ApiModelProperty(value = "是否成功")
    private boolean success;
    @ApiModelProperty(value = "消息对象")
    private String message;
    @ApiModelProperty(value = "消息代码")
    private String code;
    @ApiModelProperty(value = "返回对象")
    private T data;

    public RestMessage(){

    }


    public static <T> RestMessage<T> newInstance(boolean success,String message){
        return new RestMessage<T>(success,message,null);
    }

    public static <T> RestMessage<T> newInstance(boolean success,String message,T data){
        return new RestMessage<T>(success,message,data);
    }

    public static <T> RestMessage<T> newInstance(boolean success,String code,String message,T data){
        return new RestMessage<T>(success,code,message,data);
    }

    public RestMessage(boolean success,String message,T data){
        this.success = success;
        this.message = message;
        this.data = data;
        this.code="200";
    }

    public RestMessage(boolean success,String code,String message,T data){
        this.success = success;
        this.message = message;
        this.data = data;
        this.code=code;
    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public String toJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <S> RestMessage<S> parseJsonString(String jsonstr,TypeReference<RestMessage<S>> typeReference) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RestMessage<S> rest = mapper.readValue(jsonstr, typeReference);
        return rest;
    }

    public static <S> RestMessage<S> parseJsonString(String jsonstr) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RestMessage<S> rest = mapper.readValue(jsonstr, new TypeReference<RestMessage<S>>(){});
        return rest;
    }

    public static <S> RestMessage<List<S>> parseJsonStringForList(String jsonstr) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        RestMessage<List<S>> rest = mapper.readValue(jsonstr, new TypeReference<RestMessage<List<S>>>(){});
        return rest;
    }



}
