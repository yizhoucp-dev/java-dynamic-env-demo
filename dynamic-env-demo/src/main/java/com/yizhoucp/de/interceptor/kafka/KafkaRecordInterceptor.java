package com.yizhoucp.de.interceptor.kafka;

import com.yizhoucp.de.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * kafka消息拦截器
 */
@Component
@Slf4j
public class KafkaRecordInterceptor implements ConsumerInterceptor<String, String> {
    /**
     * 判断是否通过环境
     */
    private boolean envCheck(Headers headers) {
        Header header = headers.lastHeader(EnvUtil.ENV_MARK);
        String envMark = null;
        if (header != null && header.value() != null) {
            envMark = new String(header.value(), StandardCharsets.UTF_8);
        }
        String podEnvMark = EnvUtil.getEnvMark();
        log.info("envMark -> {}, podEnvMark -> {}", envMark, podEnvMark);
        // 环境一致消费掉数据
        return Objects.equals(envMark, podEnvMark);
    }

    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        Map<TopicPartition, List<ConsumerRecord<String, String>>> newRecords = new HashMap<>();
        //遍历每个topic、partition
        for (TopicPartition topicPartition : records.partitions()) {
            //获取特定topic、partition下的消息列表
            List<ConsumerRecord<String, String>> recordList = records.records(topicPartition);
            //过滤
            List<ConsumerRecord<String, String>> filteredList = recordList.stream()
                    .filter(x -> this.envCheck(x.headers())).collect(Collectors.toList());
            //放入新的消息记录里
            newRecords.put(topicPartition, filteredList);
        }
        return new ConsumerRecords<>(newRecords);
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}

