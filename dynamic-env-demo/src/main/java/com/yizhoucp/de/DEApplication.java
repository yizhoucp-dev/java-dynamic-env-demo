package com.yizhoucp.de;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DEApplication {
    public static void main(String[] args) {
        SpringApplication.run(DEApplication.class, args);
    }
}
