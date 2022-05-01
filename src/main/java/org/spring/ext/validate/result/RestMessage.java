package org.spring.ext.validate.result;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;


/**
 * 接口返回消息
 * @author cheny
 *
 */
public class RestMessage<T> implements Message,Serializable{
    private static final long serialVersionUID = -1865510446859810360L;

    private boolean success;

    private String message;

    private String code;

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
        return JSON.toJSONString(this);
    }
}
