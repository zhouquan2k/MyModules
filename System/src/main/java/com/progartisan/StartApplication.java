package com.progartisan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import com.progartisan.component.data.impl.EntityMetaRegistrar;

@EnableAsync
@SpringBootApplication(scanBasePackages = { "com.progartisan" })
public class StartApplication {

    public static void main(String[] args) {

        new EntityMetaRegistrar().initClasses("com.progartisan.module");

        SpringApplication.run(StartApplication.class, args);
    }

}
