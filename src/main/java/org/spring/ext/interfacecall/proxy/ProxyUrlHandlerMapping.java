package org.spring.ext.interfacecall.proxy;

import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @author 87260
 */
public class ProxyUrlHandlerMapping extends AbstractUrlHandlerMapping {
    private volatile boolean dirty = true;

    private Map<String, ServletWrappingController> registerHandlers;

    public ProxyUrlHandlerMapping(Map<String, ServletWrappingController> registerHandlers) {
        setOrder(Integer.MIN_VALUE);
        this.registerHandlers=registerHandlers;
    }


    @Override
    protected Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {
        if (this.dirty) {
            synchronized (this) {
                if (this.dirty) {
                    registerHandlers();
                    this.dirty = false;
                }
            }
        }
        return super.lookupHandler(urlPath, request);
    }

    private void registerHandlers() {
        registerHandlers.forEach((x, y) -> {
            registerHandler(x, y);
        });

    }



}
