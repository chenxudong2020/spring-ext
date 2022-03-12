package org.spring.boot.extender.interfacecall.entity;

import org.spring.boot.extender.interfacecall.annotation.GET;
import org.spring.boot.extender.interfacecall.annotation.POST;
import org.spring.boot.extender.interfacecall.handler.MethodHandler;

public class MethodMeta {
    public String methodName;
    public POST post;
    public GET get;
    public MethodHandler methodHandler;
}
