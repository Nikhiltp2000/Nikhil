package com.atdxt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.atdxt")
public class ArithmeticWebApplication {

    public static void main(String[] args) {

        SpringApplication.run(ArithmeticWebApplication.class, args);
    }
}
