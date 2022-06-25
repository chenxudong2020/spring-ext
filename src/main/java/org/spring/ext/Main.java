package org.spring.ext;


import org.spring.ext.interfacecall.EnableInterfaceCall;
import org.spring.ext.interfacecall.EnableProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableProxy
@EnableInterfaceCall(value = "classpath:my.properties")
@PropertySource(name="proxy",value = "classpath:proxy.properties")
public class Main {


    public static void main(String[] args) {
        try {
            SpringApplication.run(Main.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }

    }






}