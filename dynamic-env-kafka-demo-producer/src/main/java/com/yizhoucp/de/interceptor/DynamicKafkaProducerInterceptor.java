package com.yizhoucp.de.interceptor;


import com.yizhoucp.de.util.DynamicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class DynamicKafkaProducerInterceptor implements ProducerInterceptor<String, String> {

    private String env;

    private Environment environment;

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        log.info("拦截器拦截到消息 ：{} env : {}", record, env);
        Headers headers = record.headers();
        if (StringUtils.isEmpty(env) || (!env.contains(DynamicConstant.DEV_ENV) && !env.contains(DynamicConstant.TEST_ENV))) {
            log.info("非开发环境，直接发送 env : {}", env);
            return record;
        }

        String envMark = environment.getProperty(DynamicConstant.GLOBAL_ENV_NAME);
        if (StringUtils.isNotBlank(envMark)) {
            headers.add(new RecordHeader(DynamicConstant.GLOBAL_ENV_NAME, envMark.getBytes(StandardCharsets.UTF_8)));
            log.info("添加环境标识：{}", envMark);
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

    // https://docs.spring.io/spring-kafka/docs/2.5.0.RC1/reference/html/#interceptors
    @Override
    public void configure(Map<String, ?> configs) {
        env = (String) configs.get("envBean");
        environment = (Environment) configs.get("environmentBean");
        log.info("获取到传入的Bean");
    }
}
