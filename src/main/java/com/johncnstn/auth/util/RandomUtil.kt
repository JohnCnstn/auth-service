package com.johncnstn.auth.util

import com.johncnstn.auth.constraints.PasswordValidator
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.apache.commons.lang3.RandomStringUtils
import java.util.*


private const val DEFAULT_LENGTH = 32

private val BASE64_ENCODER = Base64.getEncoder()

fun generateBase64Secret(): String {
    return BASE64_ENCODER.encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).encoded)
}

fun generatePassword(): String {
    var password: String
    do {
        password = RandomStringUtils.randomAlphanumeric(DEFAULT_LENGTH)
    } while (!PasswordValidator.isValid(password))
    return password
}

