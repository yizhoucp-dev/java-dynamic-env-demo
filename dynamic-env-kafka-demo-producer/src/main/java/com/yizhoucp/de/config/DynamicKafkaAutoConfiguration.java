package com.yizhoucp.de.config;

import com.yizhoucp.de.interceptor.DynamicKafkaProducerInterceptor;
import com.yizhoucp.de.interceptor.DynamicKafkaRecordInterceptor;
import com.yizhoucp.de.util.DynamicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
@Slf4j
public class DynamicKafkaAutoConfiguration {

    @Autowired
    private KafkaProperties properties;

    @Value("${spring.profiles.active}")
    String env;

    @Resource
    Environment environment;

    @Bean
    @ConditionalOnMissingBean(DynamicKafkaRecordInterceptor.class)
    public DynamicKafkaRecordInterceptor kafkaRecordInterceptor() {
        return new DynamicKafkaRecordInterceptor();
    }

    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> kafkaProducerFactory,
                                                       ProducerListener<Object, Object> kafkaProducerListener) {
        KafkaTemplate<Object, Object> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        kafkaTemplate.setDefaultTopic(properties.getTemplate().getDefaultTopic());
        return kafkaTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(ProducerListener.class)
    public ProducerListener<Object, Object> kafkaProducerListener() {
        return new LoggingProducerListener<>();
    }

    /**
     * 创建Kafka生产者工厂
     *
     * 该方法配置并返回一个Kafka生产者工厂，该工厂用于创建Kafka生产者实例
     * 生产者实例用于向Kafka主题发送消息此工厂配置了生产者属性，
     * 包括环境Bean和事务ID前缀（如果提供）
     *
     * @return ProducerFactory<?, ?> 返回一个泛型的Kafka生产者工厂
     */
    @Bean
    public ProducerFactory<?, ?> kafkaProducerFactory() {
        // 加载并构建生产者属性
        Map<String, Object> producerProperties = this.properties.buildProducerProperties();
        // 添加动态Kafka生产者拦截器类配置
        producerProperties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, DynamicKafkaProducerInterceptor.class.getName());
        // 添加环境Bean到生产者属性
        producerProperties.put("envBean", env);
        // 添加环境变量Bean到生产者属性
        producerProperties.put("environmentBean", environment);
        // 创建一个默认的Kafka生产者工厂
        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(
                producerProperties);
        // 获取事务ID前缀配置
        String transactionIdPrefix = this.properties.getProducer().getTransactionIdPrefix();
        // 如果事务ID前缀不为空，则设置到工厂
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        // 返回配置好的Kafka生产者工厂
        return factory;
    }

    /**
     * 配置 Kafka 监听器容器工厂
     * <p>
     *
     * @param consumerFactory 消费者工厂，用于创建Kafka消费者
     * @return 返回配置好的ConcurrentKafkaListenerContainerFactory实例
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory) {
        // 记录当前环境信息
        log.info("KafkaListenerContainerFactory env : {}", env);

        // 创建并初始化Kafka监听器容器工厂
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordInterceptor(kafkaRecordInterceptor());

        // 根据环境自定义容器设置
        factory.setContainerCustomizer(container -> {
            // 仅在开发或测试环境下修改groupId
            if (StringUtils.isEmpty(env) || (!env.contains(DynamicConstant.DEV_ENV) && !env.contains(DynamicConstant.TEST_ENV))) {
                log.info("非开发环境，不修改 groupId env : {}", env);
                return;
            }
            String originalGroupId = container.getContainerProperties().getGroupId();
            String envMark = environment.getProperty(DynamicConstant.GLOBAL_ENV_NAME);
            log.info("originalGroupId : {} envMark : {}", originalGroupId, envMark);

            // 在开发或测试环境下，修改groupId以区分不同的环境
            if (Objects.nonNull(originalGroupId) && StringUtils.isNotBlank(envMark)
                    && (envMark.contains(DynamicConstant.DEV_ENV_PREFIX) || envMark.contains(DynamicConstant.TEST_ENV_PREFIX))) {
                String newGroupId = originalGroupId + "_" + envMark;
                container.getContainerProperties().setGroupId(newGroupId);
                log.info("修改 groupId originalGroupId : {} newGroupId : {}", originalGroupId, newGroupId);
            }
        });

        return factory;
    }
}
