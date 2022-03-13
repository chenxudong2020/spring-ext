package org.spring.boot.extender.interfacecall.paramhandler;

public abstract class HandlerChain {
    public abstract void setNext(HandlerChain handler);
    public abstract void handler(HandlerRequest handler);
    public abstract void validate(HandlerRequest handler);
}
