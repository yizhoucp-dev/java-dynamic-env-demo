package com.yizhoucp.de.biz.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * kafka 消费者
 */
@Slf4j
@Component
public class DynamicKafkaConsumer {

    @KafkaListener(topics = "kafka_test_topic", groupId = "kafka_test_topic_group")
    public void testConsumer(String msg) {
        log.info("消息消费完成 msg : {}", msg);
    }

}
