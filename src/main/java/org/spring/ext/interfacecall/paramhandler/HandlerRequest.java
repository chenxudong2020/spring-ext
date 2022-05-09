package org.spring.ext.interfacecall.paramhandler;


import org.spring.ext.interfacecall.entity.ParameterMeta;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class HandlerRequest {

    private Parameter parameter;
    private ParameterMeta parameterMeta=new ParameterMeta();
    private AnnotatedBeanDefinition beanDefinition;
    private  String interfaceClientValue;
    private List<ParameterMeta> list=new ArrayList<>();
    private String key;
    private int parameterCount;


    public void init(){
        if(parameter!=null){
            String name = parameter.getName();
            parameterMeta.parameterName=name;
            parameterMeta.parameterCount=parameterCount;
        }

    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }

    public ParameterMeta getParameterMeta() {
        return parameterMeta;
    }

    public void setParameterMeta(ParameterMeta parameterMeta) {
        this.parameterMeta = parameterMeta;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    public AnnotatedBeanDefinition getBeanDefinition() {
        return beanDefinition;
    }

    public void setBeanDefinition(AnnotatedBeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public String getInterfaceClientValue() {
        return interfaceClientValue;
    }

    public void setInterfaceClientValue(String interfaceClientValue) {
        this.interfaceClientValue = interfaceClientValue;
    }

    public List<ParameterMeta> getList() {
        return list;
    }

    public void setList(List<ParameterMeta> list) {
        this.list = list;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
