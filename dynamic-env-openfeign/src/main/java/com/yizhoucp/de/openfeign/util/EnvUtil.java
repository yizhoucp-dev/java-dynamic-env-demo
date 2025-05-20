package com.yizhoucp.de.openfeign.util;

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
        return isBaseEnv(getEnvMark());
    }

    public static boolean isBaseEnv(String envMark) {
        return envMark == null || !envMark.contains(BASE_ENV_SPLIT);
    }
}
