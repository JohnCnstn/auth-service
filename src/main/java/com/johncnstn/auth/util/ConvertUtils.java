package com.johncnstn.auth.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConvertUtils {

    private static final int MILLIS_IN_SEC_COUNT = 1000;

    public static long secsToMillis(long secs) {
        return secs * MILLIS_IN_SEC_COUNT;
    }
}
