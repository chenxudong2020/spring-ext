package org.spring.boot.extender;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Main {

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
    public static void main(String[] args) {
        try {
            SpringApplication.run(Main.class, args);
        }catch (Exception e){
            e.printStackTrace();
        }



    }

}