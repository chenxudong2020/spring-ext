package org.spring.boot.extender.invoker.api;


import org.spring.boot.extender.interfacecall.annotation.*;
import org.spring.boot.extender.invoker.bean.Input;
import org.spring.boot.extender.invoker.bean.Result;

@InterfaceClient("${baseUrl}")
public interface RemoteHttpApI {

    @POST
   Result getUser(Input input, @Url String url);

//    @POST("/getUser2")
//    @Cache(expire = 10)
//    Result getUser2(@Query("name") String name,@Query("aaa") String aaa);
}


