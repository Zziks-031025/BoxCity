package com.boxcity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.boxcity.mapper")
@EnableScheduling
public class BoxcityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoxcityApplication.class, args);
    }
}
