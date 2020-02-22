package com.johncnstn.auth.integration.api

import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.net.HttpHeaders.AUTHORIZATION
import com.johncnstn.auth.generated.api.AuthApi.*
import com.johncnstn.auth.generated.model.Token
import com.johncnstn.auth.generated.model.User
import com.johncnstn.auth.generated.model.UserRole
import com.johncnstn.auth.integration.AbstractIntegrationTest
import com.johncnstn.auth.util.refreshTokenRequest
import com.johncnstn.auth.util.signInRequest
import com.johncnstn.auth.util.signUpRequest
import org.apache.commons.lang3.StringUtils
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.lang.System.currentTimeMillis


class AuthApiControllerTest : AbstractIntegrationTest() {

    @Test
    fun signUpAsUser_happyPath() {
        // GIVEN
        val signUpRequest = signUpRequest()

        // WHEN
        val created = signUp(signUpRequest)

        // THEN
        assertSignUp(created, signUpRequest, UserRole.USER)
    }

    @Test
    fun signIn_happyPath() {
        // GIVEN
        val signUpRequest = signUpRequest()
        createUser(signUpRequest)
        val request = signInRequest(signUpRequest.email, signUpRequest.password)

        // WHEN
        val response = mockMvc.perform(
                post(signInPath)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk)
                .andReturn().response
        val body = objectMapper.readValue<Token>(response.contentAsByteArray)

        // THEN
        assertToken(body)
    }

    @Test
    fun refreshToken_happyPath() {
        // GIVEN
        val signUpRequest = signUpRequest()
        val userEntity = createUser(signUpRequest)
        val oldTokens = tokensForUser(userEntity)
        val request = refreshTokenRequest(oldTokens.accessToken, oldTokens.refreshToken)

        // WHEN
        val response = mockMvc.perform(
                post(refreshTokenPath)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk)
                .andReturn().response
        val body = objectMapper.readValue<Token>(response.contentAsByteArray)

        // THEN
        assertSoftly {
            it.assertThat(tokensProvider.validateAccessToken(body.accessToken)).isTrue
            it.assertThat(tokensProvider.validateRefreshToken(body.refreshToken)).isTrue
            it.assertThat(body.issuedAt).isGreaterThan(oldTokens.issuedAt)
            it.assertThat(body.accessExpiresIn).isGreaterThan(oldTokens.accessExpiresIn)
            it.assertThat(body.refreshExpiresIn).isGreaterThan(oldTokens.refreshExpiresIn)
        }
    }

    private fun signUp(request: User, token: String = ""): User {
        val requestBuilder = post(signUpPath)
        if (StringUtils.isNotBlank(token)) {
            requestBuilder.header(AUTHORIZATION, token)
        }
        requestBuilder.contentType(APPLICATION_JSON)
        requestBuilder.content(objectMapper.writeValueAsBytes(request))
        val response = mockMvc.perform(
                requestBuilder)
                .andExpect(status().isCreated)
                .andReturn().response
        return objectMapper.readValue(response.contentAsByteArray)
    }

    private fun assertSignUp(actual: User, expected: User, customer: UserRole) {
        assertSoftly {
            it.assertThat(actual.id).isNotNull()
            it.assertThat(actual.email).isEqualTo(expected.email)
            it.assertThat(actual.role).isEqualTo(customer)
        }
    }

    private fun assertToken(actual: Token) {
        assertSoftly {
            it.assertThat(tokensProvider.validateAccessToken(actual.accessToken)).isTrue
            it.assertThat(tokensProvider.validateRefreshToken(actual.refreshToken)).isTrue
            it.assertThat(actual.issuedAt).isLessThan(currentTimeMillis())
            it.assertThat(actual.accessExpiresIn).isGreaterThan(currentTimeMillis())
            it.assertThat(actual.refreshExpiresIn).isGreaterThan(currentTimeMillis())
        }
    }

}
