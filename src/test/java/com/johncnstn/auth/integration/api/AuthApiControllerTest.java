package com.johncnstn.auth.integration.api;

import static com.johncnstn.auth.generated.api.AuthApi.refreshTokenPath;
import static com.johncnstn.auth.generated.api.AuthApi.signInPath;
import static com.johncnstn.auth.generated.api.AuthApi.signUpPath;
import static com.johncnstn.auth.util.TestUtils.refreshTokenRequest;
import static com.johncnstn.auth.util.TestUtils.signInRequest;
import static com.johncnstn.auth.util.TestUtils.signUpRequest;
import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.johncnstn.auth.generated.model.AuthResponse;
import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.generated.model.UserRole;
import com.johncnstn.auth.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

public class AuthApiControllerTest extends AbstractIntegrationTest {

    @Test
    public void signUpAsUser_happyPath() throws Exception {
        // GIVEN
        var request = signUpRequest();

        // WHEN
        var response = signUp(request);

        // THEN
        assertSignUp(response, request);
    }

    @Test
    public void signIn_happyPath() throws Exception {
        // GIVEN
        var signUpRequest = signUpRequest();
        createUser(signUpRequest);
        var request = signInRequest(signUpRequest.getEmail(), signUpRequest.getPassword());

        // WHEN
        var response =
                mockMvc.perform(
                                post(signInPath)
                                        .contentType(APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsBytes(request)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();
        var body = objectMapper.readValue(response.getContentAsByteArray(), AuthResponse.class);

        // THEN
        assertAuthResponse(body);
    }

    @Test
    public void refreshToken_happyPath() throws Exception {
        // GIVEN
        var signUpRequest = signUpRequest();
        var userEntity = createUser(signUpRequest);
        var oldTokens = tokensForUser(userEntity);
        var request = refreshTokenRequest(oldTokens.getAccessToken(), oldTokens.getRefreshToken());

        // WHEN
        var response =
                mockMvc.perform(
                                post(refreshTokenPath)
                                        .contentType(APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsBytes(request)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();
        var body = objectMapper.readValue(response.getContentAsByteArray(), AuthResponse.class);

        // THEN
        assertSoftly(
                it -> {
                    it.assertThat(tokenProvider.validateAccessToken(body.getAccessToken()))
                            .isTrue();
                    it.assertThat(tokenProvider.validateRefreshToken(body.getRefreshToken()))
                            .isTrue();
                    it.assertThat(body.getIssuedAt()).isGreaterThan(oldTokens.getIssuedAt());
                    it.assertThat(body.getAccessExpiresIn())
                            .isGreaterThan(oldTokens.getAccessExpiresIn());
                    it.assertThat(body.getRefreshExpiresIn())
                            .isGreaterThan(oldTokens.getRefreshExpiresIn());
                });
    }

    private User signUp(User request) throws Exception {
        var requestBuilder = post(signUpPath);
        requestBuilder.contentType(APPLICATION_JSON);
        requestBuilder.content(objectMapper.writeValueAsBytes(request));
        var response =
                mockMvc.perform(requestBuilder)
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse();
        return objectMapper.readValue(response.getContentAsByteArray(), User.class);
    }

    private void assertSignUp(User actual, User expected) {
        assertSoftly(
                it -> {
                    it.assertThat(actual.getId()).isNotNull();
                    it.assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
                    it.assertThat(actual.getRole()).isEqualTo(UserRole.USER);
                });
    }

    private void assertAuthResponse(AuthResponse response) {
        assertSoftly(
                it -> {
                    it.assertThat(tokenProvider.validateAccessToken(response.getAccessToken()))
                            .isTrue();
                    it.assertThat(tokenProvider.validateRefreshToken(response.getRefreshToken()))
                            .isTrue();
                    it.assertThat(response.getIssuedAt()).isLessThan(currentTimeMillis());
                    it.assertThat(response.getAccessExpiresIn()).isGreaterThan(currentTimeMillis());
                    it.assertThat(response.getRefreshExpiresIn())
                            .isGreaterThan(currentTimeMillis());
                });
    }
}
