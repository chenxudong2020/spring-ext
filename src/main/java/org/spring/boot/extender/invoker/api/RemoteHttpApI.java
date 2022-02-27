package org.spring.boot.extender.invoker.api;


import org.spring.boot.extender.interfacecall.annotation.Body;
import org.spring.boot.extender.interfacecall.annotation.InterfaceClient;
import org.spring.boot.extender.interfacecall.annotation.POST;
import org.spring.boot.extender.invoker.bean.Input;
import org.spring.boot.extender.invoker.bean.Result;

@InterfaceClient("${baseUrl}")
public interface RemoteHttpApI {


    @POST("${getUser}")
   Result getUser(Input input);
}


