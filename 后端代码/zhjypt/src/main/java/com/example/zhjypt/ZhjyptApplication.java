package com.example.zhjypt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.zhjypt.mapper")
public class ZhjyptApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhjyptApplication.class, args);
    }

}
