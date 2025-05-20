package com.yizhoucp.de.api;

import com.yizhoucp.de.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class TestController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/api/cur-env")
    public String curEnv() {
        return EnvUtil.getEnvMark();
    }

    @GetMapping("/api/send-kafka-message")
    public String sendKafkaMessage(String message) {
        kafkaTemplate.send("de-test-topic", message);
        log.info("send kafka message -> {}", message);
        return "success";
    }
}
