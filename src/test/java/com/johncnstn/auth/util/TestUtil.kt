package com.johncnstn.auth.util

import com.github.javafaker.Faker
import com.johncnstn.auth.entity.UserEntity
import com.johncnstn.auth.entity.enums.UserRoleEntity
import com.johncnstn.auth.generated.model.RefreshTokenRequest
import com.johncnstn.auth.generated.model.SignInRequest
import com.johncnstn.auth.generated.model.User
import java.util.UUID

private val FAKE: Faker = Faker.instance()

fun email(): String = FAKE.internet().emailAddress()

fun uuid(): UUID = UUID.randomUUID()

fun signUpRequest(): User = User()
        .email(email())
        .password(generatePassword())

fun signInRequest(email: String, password: String): SignInRequest = SignInRequest()
        .email(email)
        .password(password)

fun refreshTokenRequest(accessToken: String, refreshToken: String): RefreshTokenRequest = RefreshTokenRequest()
        .accessToken(accessToken)
        .refreshToken(refreshToken)

fun userEntity(role: UserRoleEntity): UserEntity = UserEntity
        .builder()
        .id(uuid())
        .email(email())
        .passwordHash(generatePassword())
        .role(role)
        .build()
