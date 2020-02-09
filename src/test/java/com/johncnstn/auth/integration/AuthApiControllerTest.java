package com.johncnstn.auth.integration;

import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.generated.model.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.johncnstn.auth.generated.api.AuthApi.signUpPath;
import static com.johncnstn.auth.util.TestUtilKt.signUpRequest;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthApiControllerTest extends AbstractIntegrationTest {

    @Test
    public void testSignUpAsUser_happyPath() throws Exception {
        // GIVEN
        var signUpRequest = signUpRequest();

        // WHEN
        var created = signUp(signUpRequest, "");

        // THEN
        assertSignUp(created, signUpRequest, UserRole.USER);
    }

    private User signUp(User request, String token) throws Exception {
        var requestBuilder = post(signUpPath);
        if (StringUtils.isNotBlank(token)) {
            requestBuilder.header(AUTHORIZATION, token);
        }
        requestBuilder.contentType(APPLICATION_JSON);
        requestBuilder.content(objectMapper.writeValueAsBytes(request));
        var response = mockMvc.perform(
                requestBuilder)
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        return objectMapper.readValue(response.getContentAsByteArray(), User.class);
    }

    private void assertSignUp(User actual, User expected, UserRole customer) {
        assertSoftly(it -> {
            it.assertThat(actual.getId()).isNotNull();
            it.assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
            it.assertThat(actual.getRole()).isEqualTo(customer);
        });
    }

}
