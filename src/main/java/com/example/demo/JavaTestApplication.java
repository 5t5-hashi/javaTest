package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类
 * 备注：
 * 1. @SpringBootApplication 是核心注解，自动开启配置、组件扫描
 * 2. main 方法是程序入口，SpringApplication.run 负责启动内置 Tomcat 服务器
 */
@SpringBootApplication
@MapperScan("com.example.demo.mapper") // 扫描 Mapper 接口所在的包
public class JavaTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaTestApplication.class, args);
        System.out.println(">>> 项目启动成功！访问地址：http://localhost:8080/hello <<<");
    }
}
