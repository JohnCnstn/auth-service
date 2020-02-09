package com.johncnstn.auth.service;

import com.johncnstn.auth.entity.UserEntity;
import com.johncnstn.auth.entity.enums.UserRoleEntity;
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
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest extends AbstractUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokensProvider tokensProvider;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testSignUp() {
        // GIVEN
        var rawUser = new User();
        rawUser.setEmail("test@mail.com");
        rawUser.setPassword("demo123");

        var entityToReturn = new UserEntity(
                UUID.randomUUID(),
                rawUser.getEmail(),
                "xyz",
                UserRoleEntity.USER);

        when(userRepository.findByEmail(rawUser.getEmail())).thenReturn(entityToReturn);
        var authService = buildAuthService();

        // WHEN
        var user = authService.signUp(rawUser, UserRole.USER);

        // THEN
        assertSoftly(it -> {
            it.assertThat(user).isNotNull();
            it.assertThat(user.getId()).isNotNull();
            it.assertThat(user.getEmail()).isEqualTo(rawUser.getEmail());
        });

        verify(userRepository).save(any());
        verify(userRepository).findByEmail(any());
    }

    @Test
    public void testSignIn() {
        // GIVEN
        var request = new SignInRequest();
        request.setEmail("test@mail.com");
        request.setPassword("demo123");

        var principal = new DomainUserDetails(UUID.randomUUID(), DomainGrantedAuthority.USER);
        var authentication = new UsernamePasswordAuthenticationToken(principal, "demo123");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        var tokensToReturn = JwtTokens.builder()
                .issuedAt(currentTimeMillis())
                .accessToken("access token")
                .accessExpiresIn(currentTimeMillis())
                .refreshToken("refresh token")
                .refreshExpiresIn(currentTimeMillis())
                .userDetails(principal)
                .build();

        when(tokensProvider.createTokens(authentication)).thenReturn(tokensToReturn);
        var authService = buildAuthService();

        // WHEN
        var token = authService.signIn(request);

        // THEN
        assertSoftly(it -> {
            it.assertThat(token).isNotNull();
            it.assertThat(token.getIssuedAt()).isEqualTo(tokensToReturn.getIssuedAt());
            it.assertThat(token.getAccessToken()).isEqualTo(tokensToReturn.getAccessToken());
            it.assertThat(token.getAccessExpiresIn()).isEqualTo(tokensToReturn.getAccessExpiresIn());
            it.assertThat(token.getRefreshToken()).isEqualTo(tokensToReturn.getRefreshToken());
            it.assertThat(token.getRefreshExpiresIn()).isEqualTo(tokensToReturn.getRefreshExpiresIn());
        });
    }

    private AuthService buildAuthService() {
        return new AuthServiceImpl(authenticationManager, passwordEncoder, tokensProvider, userRepository);
    }
}
