package org.spring.ext.interfacecall.exception;

/**
 * @author 87260
 */
public class InterfaceInvokeException extends RuntimeException{
    public InterfaceInvokeException(String s) {
        super(s);
    }
    public InterfaceInvokeException(RuntimeException e) {
        super(e);
    }
}
