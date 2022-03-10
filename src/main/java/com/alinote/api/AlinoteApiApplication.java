package com.alinote.api;


import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.repository.config.*;
import org.springframework.scheduling.annotation.*;

@SpringBootApplication
@EnableAsync
@EnableMongoRepositories
@PropertySource(value = {
        "classpath:validation-messages.properties"})
@ComponentScan(basePackages = {
        "com.alinote.api"
})
public class AlinoteApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlinoteApiApplication.class, args);
    }
}

