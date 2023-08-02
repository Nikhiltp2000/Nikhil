package com.atdxt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.atdxt"})
public class JpaConnectionApplication {
    private static final Logger logger = LoggerFactory.getLogger(JpaConnectionApplication.class);

    public static void main(String[] args) {
        try {
            // This line will throw a NullPointerException intentionally
            String nullString = null;
            System.out.println(nullString.length());
        } catch (Exception e) {
            logger.error("An error occurred during application execution", e);
        }

        SpringApplication.run(JpaConnectionApplication.class, args);
    }


   /* @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("nikhiltp2924@gmail.com");
        mailSender.setPassword("tvabamhemoiwfskx");

        return mailSender;
    }*/
}

