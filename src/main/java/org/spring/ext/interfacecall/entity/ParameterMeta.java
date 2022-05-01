package org.spring.ext.interfacecall.entity;



import org.spring.ext.interfacecall.annotation.Body;
import org.spring.ext.interfacecall.annotation.Head;
import org.spring.ext.interfacecall.annotation.Query;
import org.spring.ext.interfacecall.annotation.Url;

public class ParameterMeta {
    public int bodyCount=0;
    public Body body;
    public int parameterCount=0;
    public Head head;
    public String parameterName;
    public Url url;
    public Query query;




}
