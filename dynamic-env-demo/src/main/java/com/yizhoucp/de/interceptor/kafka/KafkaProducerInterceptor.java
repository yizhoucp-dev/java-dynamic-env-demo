package com.yizhoucp.de.interceptor.kafka;


import com.yizhoucp.de.util.EnvUtil;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class KafkaProducerInterceptor implements ProducerInterceptor<String, String> {

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        Headers headers = record.headers();

        String envMark = EnvUtil.getEnvMark();
        if (StringUtils.hasLength(envMark)) {
            headers.add(new RecordHeader(EnvUtil.ENV_MARK, envMark.getBytes(StandardCharsets.UTF_8)));
            return new ProducerRecord<>(record.topic(), record.partition(), record.timestamp(),
                    record.key(), record.value(), headers);
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
