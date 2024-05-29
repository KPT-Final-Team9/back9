package com.core.back9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Back9Application {

    public static void main(String[] args) {
        SpringApplication.run(Back9Application.class, args);
    }

}
