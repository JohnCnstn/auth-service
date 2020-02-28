package com.johncnstn.auth.service;

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.johncnstn.auth.entity.UserEntity;
import com.johncnstn.auth.entity.enums.UserRoleType;
import com.johncnstn.auth.generated.model.SignInRequest;
import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.generated.model.UserRole;
import com.johncnstn.auth.repository.UserRepository;
import com.johncnstn.auth.security.DomainGrantedAuthority;
import com.johncnstn.auth.security.DomainUserDetails;
import com.johncnstn.auth.security.JwtTokens;
import com.johncnstn.auth.security.TokensProvider;
import com.johncnstn.auth.service.impl.AuthServiceImpl;
import com.johncnstn.auth.unit.AbstractUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceTest extends AbstractUnitTest {

    @Mock private AuthenticationManager authenticationManager;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private TokensProvider tokensProvider;

    @Mock private UserRepository userRepository;

    @InjectMocks private AuthServiceImpl authService;

    @Test
    public void signUp() {
        // GIVEN
        var rawUser = new User();
        rawUser.setEmail("test@mail.com");
        rawUser.setPassword("demo123");

        var entityToReturn =
                new UserEntity(randomUUID(), rawUser.getEmail(), "xyz", UserRoleType.USER);

        when(passwordEncoder.encode(any())).thenReturn(any());
        when(userRepository.findByEmail(rawUser.getEmail())).thenReturn(entityToReturn);

        // WHEN
        var user = authService.signUp(rawUser, UserRole.USER);

        // THEN
        assertSoftly(
                it -> {
                    it.assertThat(user).isNotNull();
                    it.assertThat(user.getId()).isNotNull();
                    it.assertThat(user.getEmail()).isEqualTo(rawUser.getEmail());
                });

        verify(userRepository).save(any());
        verify(userRepository).findByEmail(any());
    }

    @Test
    public void signIn() {
        // GIVEN
        var request = new SignInRequest();
        request.setEmail("test@mail.com");
        request.setPassword("demo123");

        var principal = new DomainUserDetails(randomUUID(), DomainGrantedAuthority.USER);
        var authentication = new UsernamePasswordAuthenticationToken(principal, "demo123");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        var tokensToReturn =
                JwtTokens.builder()
                        .issuedAt(currentTimeMillis())
                        .accessToken("access token")
                        .accessExpiresIn(currentTimeMillis())
                        .refreshToken("refresh token")
                        .refreshExpiresIn(currentTimeMillis())
                        .userDetails(principal)
                        .build();

        when(tokensProvider.createTokens(authentication)).thenReturn(tokensToReturn);

        // WHEN
        var token = authService.signIn(request);

        // THEN
        assertSoftly(
                it -> {
                    it.assertThat(token).isNotNull();
                    it.assertThat(token.getIssuedAt()).isEqualTo(tokensToReturn.getIssuedAt());
                    it.assertThat(token.getAccessToken())
                            .isEqualTo(tokensToReturn.getAccessToken());
                    it.assertThat(token.getAccessExpiresIn())
                            .isEqualTo(tokensToReturn.getAccessExpiresIn());
                    it.assertThat(token.getRefreshToken())
                            .isEqualTo(tokensToReturn.getRefreshToken());
                    it.assertThat(token.getRefreshExpiresIn())
                            .isEqualTo(tokensToReturn.getRefreshExpiresIn());
                });
    }
}
