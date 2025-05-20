package com.yizhoucp.de;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@ComponentScan(basePackages = "com.yizhoucp.de")
@SpringBootApplication
public class DynamicKafkaApplication {
    public static void main(String[] args) {
        SpringApplication.run(DynamicKafkaApplication.class, args);
        log.info("Dynamic Kafka 启动完成....");
    }
}
