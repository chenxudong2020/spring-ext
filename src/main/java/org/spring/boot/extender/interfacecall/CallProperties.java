package org.spring.boot.extender.interfacecall;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallProperties {

    public Map<String,String> interfaceUrlMap=new ConcurrentHashMap<>();
    public Map<String,String> returnMap=new ConcurrentHashMap<>();
    public Map<String,String> headMap=new ConcurrentHashMap<>();

    private CallProperties(){};


    private static CallProperties callProperties=new CallProperties();

    public static CallProperties getInstance(){
        return callProperties;
    }
}
