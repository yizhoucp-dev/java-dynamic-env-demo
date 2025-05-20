package com.yizhoucp.de.biz.consumer;

import com.yizhoucp.de.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = {"de-test-topic"})
    public void onMessage(ConsumerRecord<?, ?> record){
        log.info("onMessage - value -> {}, envMark -> {}", record.value(), EnvUtil.getEnvMark());
    }
}
