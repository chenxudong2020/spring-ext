package org.spring.ext.invoker.api;


import org.spring.ext.invoker.bean.Input;
import org.spring.ext.invoker.bean.Result;
import org.spring.ext.interfacecall.annotation.InterfaceClient;
import org.spring.ext.interfacecall.annotation.POST;
import org.spring.ext.interfacecall.annotation.Url;

@InterfaceClient("${baseUrl}")
public interface RemoteHttpApI {

    @POST
    Result getUser(Input input, @Url String url);

//    @POST("/getUser2")
//    @Cache(expire = 10)
//    Result getUser2(@Query("name") String name,@Query("aaa") String aaa);
}


