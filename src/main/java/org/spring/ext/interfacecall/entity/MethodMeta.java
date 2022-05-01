package org.spring.ext.interfacecall.entity;


import org.spring.ext.interfacecall.annotation.Cache;
import org.spring.ext.interfacecall.handler.MethodHandler;
import org.spring.ext.interfacecall.annotation.GET;
import org.spring.ext.interfacecall.annotation.POST;
import org.spring.ext.interfacecall.annotation.Type;

import java.lang.reflect.Method;

public class MethodMeta {
    public String methodName;
    public POST post;
    public GET get;
    public Type type;
    public Cache cache;
    public MethodHandler methodHandler;
    public Method method;
}
