package org.spring.ext.interfacecall;


import org.spring.ext.interfacecall.entity.Constant;
import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.handler.MethodHandler;
import org.spring.ext.interfacecall.handler.MethodHandlerWrapper;
import org.springframework.beans.factory.BeanFactory;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CallInterfaceHandler implements InvocationHandler {
    private BeanFactory beanFactory;
    private final String className;
    private Class<? extends ApiRestTemplate> restTemplateClass;
    private Class callBackClass;
    private boolean isCallBack;


    public CallInterfaceHandler(BeanFactory beanFactory, String className, Class<? extends ApiRestTemplate> restTemplateClass, Class callBackClass, boolean isCallBack) {
        this.beanFactory = beanFactory;
        this.className = className;
        this.restTemplateClass=restTemplateClass;
        this.callBackClass=callBackClass;
        this.isCallBack=isCallBack;

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
}
