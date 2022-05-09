package org.spring.ext.interfacecall.exception;

/**
 * @author 87260
 */
public class InterfaceCallInitException extends RuntimeException{
    public InterfaceCallInitException(String s) {
        super(s);
    }
    public InterfaceCallInitException(Exception e) {
        super(e);
    }
}
