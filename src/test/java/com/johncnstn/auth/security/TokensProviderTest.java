package com.johncnstn.auth.security;

import com.johncnstn.auth.config.AppProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Date;
import java.util.UUID;

import static com.johncnstn.auth.security.DomainGrantedAuthority.USER;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class TokensProviderTest {

    private static AppProperties appProperties;
    private static TokensProvider tokensProvider;

    @BeforeAll
    public static void setUp() {
        appProperties = new AppProperties();
        tokensProvider = new TokensProvider(appProperties);
        tokensProvider.init();
    }

    @Test
    public void testCreateTokens() {
        // GIVEN
        var principal = new DomainUserDetails(UUID.randomUUID(), USER);
        var authentication = new UsernamePasswordAuthenticationToken(principal, "password");

        // WHEN
        JwtTokens tokens = tokensProvider.createTokens(authentication);

        // THEN
        assertSoftly(it -> {
            it.assertThat(tokens).isNotNull();
            it.assertThat(tokens.getIssuedAt()).isGreaterThan(0);
            it.assertThat(tokens.getIssuedAt()).isLessThanOrEqualTo(currentTimeMillis());
            it.assertThat(tokens.getAccessToken()).isNotNull();
            it.assertThat(tokens.getAccessExpiresIn()).isEqualTo(tokens.getIssuedAt() + 86400000L);
            it.assertThat(tokens.getRefreshToken()).isNotNull();
            it.assertThat(tokens.getRefreshExpiresIn()).isEqualTo(tokens.getIssuedAt() + 3888000000L);
        });
    }

    @Test
    public void testRefreshTokens() {
        // GIVEN
        var id = UUID.randomUUID();
        var issuedAt = new Date();
        var userDetails = new DomainUserDetails(id, USER);
        var accessToken = createToken(
                userDetails,
                issuedAt,
                JwtTokenType.ACCESS,
                new Date(2020, 5, 10));

        var refreshToken = createToken(
                userDetails,
                issuedAt,
                JwtTokenType.REFRESH,
                new Date(2020, 5, 10));

        // WHEN
        JwtTokens tokens = tokensProvider.refreshTokens(accessToken, refreshToken);

        // THEN
        assertSoftly(it -> {
            it.assertThat(tokens).isNotNull();
            it.assertThat(tokens.getIssuedAt()).isGreaterThan(0);
            it.assertThat(tokens.getIssuedAt()).isLessThanOrEqualTo(currentTimeMillis());
            it.assertThat(tokens.getAccessToken()).isNotNull();
            it.assertThat(tokens.getAccessExpiresIn()).isEqualTo(tokens.getIssuedAt() + 86400000L);
            it.assertThat(tokens.getRefreshToken()).isNotNull();
            it.assertThat(tokens.getRefreshExpiresIn()).isEqualTo(tokens.getIssuedAt() + 3888000000L);
        });
    }

    private String createToken(DomainUserDetails userDetails, Date issuedAt, JwtTokenType type, Date expiresIn) {
        var builder = Jwts.builder();
        if (userDetails.getUserId() != null) {
            builder.setSubject(userDetails.getUserId().toString());
        }
        builder.setExpiration(expiresIn);
        builder.setIssuedAt(issuedAt);
        builder.claim("rol", userDetails.getRole().getAuthority());
        builder.claim("typ", type.getValue());
        builder.signWith(Keys.hmacShaKeyFor(BASE64.decode(appProperties.getJwt().getBase64Secret())), HS512);
        return builder.compact();
    }
}
