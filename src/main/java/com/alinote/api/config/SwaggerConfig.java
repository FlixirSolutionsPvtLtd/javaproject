package com.alinote.api.config;

import org.springframework.context.annotation.*;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.*;
import springfox.documentation.spring.web.plugins.*;
import springfox.documentation.swagger2.annotations.*;

import java.util.*;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("alinote-api")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.alinote.api.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Rest API for Alinote Mobile App",
                "API for use of Mobile App of Alinotes",
                "1.0",
                "Terms of service",
                new Contact("Sorizava IT team", "https://www.sorizava.co.kr/", "sorizavaitteam@sorizava.co.kr"),
                "License of API",
                "https://www.sorizava.co.kr/",
                Collections.emptyList());
    }
}
