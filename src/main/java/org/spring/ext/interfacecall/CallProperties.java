package org.spring.ext.interfacecall;



import org.spring.ext.interfacecall.entity.MethodMeta;
import org.spring.ext.interfacecall.entity.ParameterMeta;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 87260
 */
public class CallProperties {

    public Map<String, MethodMeta> methodMetaMap=new ConcurrentHashMap<>();
    public Map<String,String> interfaceUrlMap=new ConcurrentHashMap<>();
    public Map<String,String> returnMap=new ConcurrentHashMap<>();
    public Map<String, List<ParameterMeta>> parameterMetaMap=new ConcurrentHashMap<>();
    public boolean isCached;


}
