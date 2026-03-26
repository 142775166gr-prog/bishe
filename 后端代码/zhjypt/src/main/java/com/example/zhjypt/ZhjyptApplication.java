package com.example.zhjypt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧教育平台（zhjypt）Spring Boot 启动类
 * <p>
 * 职责：
 *   1. 启动整个 Spring 容器
 *   2. 通过 @MapperScan 扫描 mapper 包，自动注册所有cation.class, args);
 **/

 @SpringBootApplication
 @MapperScan("com.example.zhjypt.mapper")
 public class ZhjyptApplication {

 public static void main(String[] args) {
    SpringApplication.run(ZhjyptApplication.class, args);
 }

 }

