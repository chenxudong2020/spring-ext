package org.spring.boot.extender.invoker.api;


import org.spring.boot.extender.interfacecall.annotation.*;
import org.spring.boot.extender.invoker.bean.Input;
import org.spring.boot.extender.invoker.bean.Result;

@InterfaceClient("${baseUrl}")
public interface RemoteHttpApI {

    @POST("${getUser}")
   Result getUser(@Body Input input, @Url String url);

    @GET("/getUser2?name={name}")
    Result getUser2(String name);
}


