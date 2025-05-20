package com.yizhoucp.de.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DynamicConstant {

    // 动态环境使用的 开发环境
    public static final String DEV_ENV = "dev";

    // 动态环境使用的 测试环境
    public static final String TEST_ENV = "test";

    public static final String GLOBAL_ENV_NAME = "envMark";

    public static final String KAFKA_TEST_TOPIC = "kafka_test_topic";

    // 动态环境使用的 非基础开发环境的前缀
    public static final String DEV_ENV_PREFIX = "dev-";

    // 动态环境使用的 非基础测试环境的前缀
    public static final String TEST_ENV_PREFIX = "test-";
}
