package org.spring.ext.interfacecall;


import org.spring.ext.interfacecall.entity.Constant;
import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.handler.MethodHandler;
import org.spring.ext.interfacecall.handler.MethodHandlerWrapper;
import org.springframework.beans.factory.BeanFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author 87260
 */
public class CallInterfaceHandler implements InvocationHandler {
    private BeanFactory beanFactory;
    private String className;
    private Class<? extends ApiRestTemplate> restTemplateClass;
    private Class callBackClass;
    private boolean isCallBack;


    public CallInterfaceHandler(BeanFactory beanFactory,Class<? extends ApiRestTemplate> restTemplateClass) {
        this.beanFactory = beanFactory;
         this.restTemplateClass=restTemplateClass;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        CallProperties callProperties=beanFactory.getBean(CallProperties.class);
        String key = String.format(Constant.KEY_FORMAT, className, method.getName());
        MethodMeta methodMeta = callProperties.methodMetaMap.get(key);
        methodMeta.method = method;
        MethodHandler methodHandler = methodMeta.methodHandler;
        MethodHandlerWrapper methodHandlerWrapper = new MethodHandlerWrapper(methodHandler, methodMeta, args, beanFactory, callProperties, className,restTemplateClass,callBackClass,isCallBack);
        return methodHandlerWrapper.invoke(proxy,method,args);

    }


    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public Class getCallBackClass() {
        return callBackClass;
    }

    public void setCallBackClass(Class callBackClass) {
        this.callBackClass = callBackClass;
    }

    public boolean isCallBack() {
        return isCallBack;
    }

    public void setCallBack(boolean callBack) {
        isCallBack = callBack;
    }
}
