package org.spring.boot.extender.interfacecall.entity;


import org.spring.boot.extender.interfacecall.annotation.*;
import org.spring.boot.extender.interfacecall.handler.MethodHandler;

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
