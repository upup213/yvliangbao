package com.yvliangbao.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("======================================");
        System.out.println("         余量宝网关服务启动成功！        ");
        System.out.println("======================================");
    }
}
