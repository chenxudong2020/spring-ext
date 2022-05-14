package org.spring.ext;


import org.spring.ext.interfacecall.EnableInterfaceCall;
import org.spring.ext.interfacecall.proxy.ProxyServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Servlet;

@SpringBootApplication
@EnableInterfaceCall
@PropertySource(name="my",value = "classpath:my.properties")
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