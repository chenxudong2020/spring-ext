package org.spring.ext.interfacecall.paramhandler;

public abstract class HandlerChain {
    public abstract void setNext(HandlerChain handler);
    public abstract HandlerChain handler(HandlerRequest handler);
    public abstract void validate(HandlerRequest handler);
}
