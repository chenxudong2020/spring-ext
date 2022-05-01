package org.spring.ext.invoker.controller;


import org.spring.ext.invoker.bean.Input;
import org.spring.ext.invoker.bean.Result;
import org.spring.ext.invoker.bean.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {


    @PostMapping("/getUser")
    public Result getUser(@RequestBody Input input){
        String id=input.getId();
        User user=new User();
        user.setName("1的name");
        Result result=new Result();
        result.setCode("200");
        result.setMessage("成功了");
        result.setData(user);
        return result;
    }
}
