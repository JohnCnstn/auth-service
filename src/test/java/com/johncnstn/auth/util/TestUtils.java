package com.johncnstn.auth.util;

import static com.johncnstn.auth.util.RandomUtilKt.generatePassword;
import static java.util.UUID.randomUUID;

import com.github.javafaker.Faker;
import com.johncnstn.auth.entity.UserEntity;
import com.johncnstn.auth.entity.enums.UserRoleType;
import com.johncnstn.auth.generated.model.RefreshTokenRequest;
import com.johncnstn.auth.generated.model.SignInRequest;
import com.johncnstn.auth.generated.model.User;

public final class TestUtils {

    private static final Faker FAKE = Faker.instance();

    private TestUtils() {}

    public static String email() {
        return FAKE.internet().emailAddress();
    }

    public static User signUpRequest() {
        return new User().email(email()).password(generatePassword());
    }

    public static SignInRequest signInRequest(String email, String password) {
        return new SignInRequest().email(email).password(password);
    }

    public static RefreshTokenRequest refreshTokenRequest(String accessToken, String refreshToken) {
        return new RefreshTokenRequest().accessToken(accessToken).refreshToken(refreshToken);
    }

    public static UserEntity userEntity(UserRoleType role) {
        return UserEntity.builder()
                .id(randomUUID())
                .email(email())
                .passwordHash(generatePassword())
                .role(role)
                .build();
    }
}
