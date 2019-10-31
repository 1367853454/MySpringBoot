package com.hello.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//使用括号里面的代码不使用自动配置@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@SpringBootApplication
public class HelloSpringBootApplication {

    @GetMapping("")
    public String sayHi(){
        return "hello Spring Boot";
    }


    public static void main(String[] args) {
        SpringApplication.run(HelloSpringBootApplication.class, args);
    }

}
