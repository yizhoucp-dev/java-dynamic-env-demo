package com.yizhoucp.de.util;

public class EnvUtil {
    private EnvUtil() {}

    public static final String ENV_MARK = "envMark";

    public static final String BASE_ENV_SPLIT = "-";

    private static String envMark;

    public static String getEnvMark() {
        if (envMark == null) {
            envMark = System.getenv(ENV_MARK);
        }
        return envMark;
    }

    public static boolean isBaseEnv() {
        return getEnvMark() == null || !getEnvMark().contains(BASE_ENV_SPLIT);
    }
}
