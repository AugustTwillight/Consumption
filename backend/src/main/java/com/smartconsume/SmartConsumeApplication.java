package com.smartconsume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartConsumeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartConsumeApplication.class, args);
    }

}
