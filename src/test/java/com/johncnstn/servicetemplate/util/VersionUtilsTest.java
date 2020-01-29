package com.johncnstn.servicetemplate.util;

import com.johncnstn.servicetemplate.unit.AbstractUnitTest;
import org.junit.jupiter.api.Test;

import static com.johncnstn.servicetemplate.util.VersionUtils.getAppVersion;
import static org.assertj.core.api.Assertions.assertThat;

public class VersionUtilsTest extends AbstractUnitTest {

    @Test
    public void testGetAppVersion() {
        //GIVEN
        //WHEN
        var appVersion = getAppVersion();
        //THEN
        assertThat(appVersion).isNotNull();
    }

}

