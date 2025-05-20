package com.yizhoucp.de.interceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yizhoucp.de.util.DynamicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.listener.RecordInterceptor;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * kafka消息拦截器
 */
@Slf4j
public class DynamicKafkaRecordInterceptor implements RecordInterceptor<String, String> {

    // 动态环境使用的 非基础开发环境的前缀
    private static final String DEV_ENV_PREFIX = "dev-";

    // 动态环境使用的 非基础测试环境的前缀
    private static final String TEST_ENV_PREFIX = "test-";

    // 动态环境使用的 开发环境
    public static final String DEV_ENV = "dev";

    // 动态环境使用的 测试环境
    public static final String TEST_ENV = "test";


    @Value("${spring.profiles.active}")
    String env;

    @Resource
    Environment environment;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value(("${spring.application.name}"))
    private String applicationName;

    @Override
    public ConsumerRecord<String, String> intercept(ConsumerRecord<String, String> record) {
        log.info("入参 record : {}", JSON.toJSONString(record));
        Headers headers = record.headers();
        if (this.envCheck(headers)) {
            return record;
        }
        log.info("未通过环境判断丢弃该消息 record : {}", JSON.toJSONString(record));
        return null;
    }

    /**
     * 判断是否通过环境
     */
    private Boolean envCheck(Headers headers) {
        log.info("开始环境检查 headers : {} env : {}", JSON.toJSONString(headers), env);
        if (StringUtils.isEmpty(env) || (!env.contains(DynamicConstant.DEV_ENV) && !env.contains(DynamicConstant.TEST_ENV))) {
            log.info("非开发测试环境放过 env : {}", env);
            return true;
        }
        Header envMarkHeader = headers.lastHeader(DynamicConstant.GLOBAL_ENV_NAME);
        log.info("获取envMarkHeader : {}", envMarkHeader);
        String msgEnvMark = null;
        if (envMarkHeader != null && envMarkHeader.value() != null) {
            msgEnvMark = new String(envMarkHeader.value(), StandardCharsets.UTF_8);
        }
        String podEnvMark = environment.getProperty(DynamicConstant.GLOBAL_ENV_NAME);
        // 当前环境与消息的环境不一致
        if (!Objects.equals(msgEnvMark, podEnvMark)) {
            if (Objects.nonNull(podEnvMark) && (podEnvMark.startsWith(DEV_ENV_PREFIX) || podEnvMark.startsWith(TEST_ENV_PREFIX))) {
                log.info("环境不匹配且是非基础环境拦截 msgEnvMark : {} podEnvMark : {} env : {}", msgEnvMark, podEnvMark, env);
                return false;
            }

            // 判断是否存在对应的环境
            String key = this.getDynamicPrefix() + msgEnvMark;
            boolean hasKey = Boolean.TRUE.equals(redisTemplate.hasKey(key));
            // 如果不存在对应环境，且当前环境是 DEV_ENV 或 TEST_ENV，则消费
            boolean res = !hasKey && (DEV_ENV.equals(podEnvMark) || TEST_ENV.equals(podEnvMark));
            return res;
        }
        // 环境一致消费掉数据
        log.info("环境匹配放过 msgEnvMark : {} podEnvMark : {} env : {}", msgEnvMark, podEnvMark, env);
        return true;
    }

    private String getDynamicPrefix() {
        String prefix = "";
        try {
            prefix = Lists.newArrayList(applicationName.split("-")).get(0) + "-";
        } catch (Exception e) {
            log.error("获取 prefix 失败", e);
        }
        return prefix;
    }
}

