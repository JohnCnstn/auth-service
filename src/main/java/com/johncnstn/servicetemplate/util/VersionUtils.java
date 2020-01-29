package com.johncnstn.servicetemplate.util;

import lombok.experimental.UtilityClass;

import static java.lang.System.getenv;
import static org.apache.commons.lang3.StringUtils.firstNonEmpty;

@UtilityClass
public class VersionUtils {

    public static String getAppVersion() {
        return firstNonEmpty(getenv("APP_VERSION"), "1970-01-01-00-00-00-unknown");
    }

}
