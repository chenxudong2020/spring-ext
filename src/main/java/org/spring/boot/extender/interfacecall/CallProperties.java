package org.spring.boot.extender.interfacecall;

import org.spring.boot.extender.interfacecall.entity.ParameterMeta;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CallProperties {

    public Map<String,String> interfaceUrlMap=new ConcurrentHashMap<>();
    public Map<String,String> returnMap=new ConcurrentHashMap<>();
    public Map<String, List<ParameterMeta>> parameterMetaMap=new ConcurrentHashMap<>();

    private CallProperties(){};


    private static CallProperties callProperties=new CallProperties();

    public static CallProperties getInstance(){
        return callProperties;
    }
}
