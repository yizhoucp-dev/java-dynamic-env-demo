原理
- 发送消息时，可以通过消息头标识该消息是哪个环境发送的，详见:DynamicKafkaProducerInterceptor
- 不通环境消费者组名字加上环境后缀，详见:DynamicKafkaProducerInterceptor
- 消费消息时通过消息头中的环境标识判断该环境是否消费，详见:DynamicKafkaRecordInterceptor
- 消费者，详见:DynamicKafkaConsumer

测试发送消息
- /api/test/send-kafka-msg

环境相关
- 运维侧塞入环境标识:envMark