package com.johncnstn.auth.config;

import static com.johncnstn.auth.util.RandomUtils.generateBase64Secret;

import javax.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public class AppProperties {

    private Jwt jwt = new Jwt();

    private Url url = new Url();

    @Getter
    @Setter
    public static class Jwt {

        private String base64Secret = generateBase64Secret();

        @PositiveOrZero private long accessTokenValidity = 86400; // 1 day in seconds

        @PositiveOrZero private long refreshTokenValidity = 3888000; // 45 days in seconds

        public void setBase64Secret(String base64Secret) {
            if (StringUtils.isNotBlank(base64Secret)) {
                this.base64Secret = base64Secret;
            }
        }
    }

    @Getter
    @Setter
    public static class Url {

        private String swaggerUi = "http://localhost:3000";
    }
}
