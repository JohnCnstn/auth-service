package com.johncnstn.auth.util;

import static com.johncnstn.auth.constraints.PasswordValidator.isPasswordValid;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.security.Keys.secretKeyFor;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.Base64;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RandomUtils {

    private static final int DEFAULT_LENGTH = 32;
    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    public static String generateBase64Secret() {
        return BASE64_ENCODER.encodeToString(secretKeyFor(HS512).getEncoded());
    }

    public static String generatePassword() {
        String password;
        do {
            password = randomAlphanumeric(DEFAULT_LENGTH);
        } while (!isPasswordValid(password));
        return password;
    }
}
