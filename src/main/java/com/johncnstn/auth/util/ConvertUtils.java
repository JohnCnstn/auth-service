package com.johncnstn.auth.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConvertUtils {

    public static long secsToMillis(long secs) {
        return secs * 1000;
    }
}
