package com.johncnstn.auth.util

import com.github.javafaker.Faker
import com.johncnstn.auth.generated.model.RefreshTokenRequest
import com.johncnstn.auth.generated.model.SignInRequest
import com.johncnstn.auth.generated.model.User

private val FAKE: Faker = Faker.instance()

fun signUpRequest(): User = User()
        .email(FAKE.internet().emailAddress())
        .password(generatePassword())

fun signInRequest(email: String, password: String): SignInRequest = SignInRequest()
        .email(email)
        .password(password)

fun refreshTokenRequest(accessToken: String, refreshToken: String): RefreshTokenRequest = RefreshTokenRequest()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
