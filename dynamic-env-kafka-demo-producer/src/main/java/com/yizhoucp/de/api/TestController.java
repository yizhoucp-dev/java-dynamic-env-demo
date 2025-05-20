package com.yizhoucp.de.api;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yizhoucp.de.util.DynamicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Duration;

@Slf4j
@RestController
public class TestController {

    // 动态环境使用的 开发环境
    public static final String DEV_ENV = "dev";

    // 动态环境使用的 测试环境
    public static final String TEST_ENV = "test";

    /* 全局的环境名字信息 */
    public static final String GLOBAL_ENV_NAME = "envMark";

    public static final String DYNAMIC_KAFKA_CACHE_PREFIX = "polaris-";

    @Value(("${spring.application.name}"))
    private String applicationName;

    @Value("${spring.profiles.active}")
    String env;

    @Autowired
    Environment environment;

    @Resource
    private KafkaTemplate kafkaTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 发送消息到 Kafka
     * 该方法通过HTTP POST请求接收一个JSON对象作为消息内容，并将其发送到指定的Kafka主题
     * 主要用途是为了解耦消息的生产和消费，通过Kafka作为中间件来异步处理消息
     *
     * @param msg 要发送的JSON消息，通过请求体传递
     * @return 如果消息发送成功，返回"success"
     */
    @PostMapping("/api/test/send-kafka-msg")
    public String sendKafkaMsg(@RequestBody JSONObject msg) {
        // 发送消息到Kafka主题，这里使用了kafkaTemplate来发送消息
        // 第一个参数是主题名，第二个参数是发送的消息内容
        kafkaTemplate.send(DynamicConstant.KAFKA_TEST_TOPIC, msg.toJSONString()).addCallback(
                success -> log.info("发送消息成功 msg : {}", msg.toJSONString()),
                failure -> log.error("发送消息失败 msg : {}", msg.toJSONString())
        );
        return "success";
    }

    /**
     * 心跳包
     *
     * @return
     */
    @GetMapping("/api/dynamic/kafka/heart/registration-heart-beat")
    public Boolean registrationHeartBeat() {
        return registrationHeartBeatHandle();
    }

    /**
     * 心跳包 简易服务注册功能
     *
     * @return
     */
    public Boolean registrationHeartBeatHandle() {
        String envMark = environment.getProperty(DynamicConstant.GLOBAL_ENV_NAME);
        if (StringUtils.isBlank(envMark)) {
            log.error("环境标识为空，请检查配置 envMark : {}", envMark);
            return false;
        }
        if (!DEV_ENV.equals(env) && !TEST_ENV.equals(env)) {
            return true;
        }
        redisTemplate.opsForValue().set(getDynamicPrefix() + environment.getProperty(DynamicConstant.GLOBAL_ENV_NAME), System.currentTimeMillis(), Duration.ofSeconds(30));
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
