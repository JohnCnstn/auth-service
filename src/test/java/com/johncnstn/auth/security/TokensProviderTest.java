package com.johncnstn.auth.security;

import com.johncnstn.auth.config.AppProperties;
import com.johncnstn.auth.unit.AbstractUnitTest;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static com.johncnstn.auth.security.DomainGrantedAuthority.USER;
import static com.johncnstn.auth.security.JwtTokenType.ACCESS;
import static com.johncnstn.auth.security.JwtTokenType.REFRESH;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class TokensProviderTest extends AbstractUnitTest {

    private static AppProperties appProperties;
    private static TokensProvider tokensProvider;
    private static long accessTokenValidityMillis;
    private static long refreshTokenValidityMillis;

    @BeforeAll
    public static void setUp() {
        appProperties = new AppProperties();
        tokensProvider = new TokensProvider(appProperties);
        tokensProvider.init();
        accessTokenValidityMillis = appProperties.getJwt().getAccessTokenValidity() * 1000;
        refreshTokenValidityMillis = appProperties.getJwt().getRefreshTokenValidity() * 1000;
    }

    @Test
    public void createTokens() {
        // GIVEN
        var principal = new DomainUserDetails(randomUUID(), USER);
        var authentication = new UsernamePasswordAuthenticationToken(principal, "password");

        // WHEN
        JwtTokens tokens = tokensProvider.createTokens(authentication);

        // THEN
        assertSoftly(it -> {
            it.assertThat(tokens).isNotNull();
            it.assertThat(tokens.getIssuedAt()).isGreaterThan(0);
            it.assertThat(tokens.getIssuedAt()).isLessThanOrEqualTo(currentTimeMillis());
            it.assertThat(tokens.getAccessToken()).isNotNull();
            it.assertThat(tokens.getRefreshToken()).isNotNull();

            var expectedAccessExpiresIn = tokens.getIssuedAt() + accessTokenValidityMillis;
            it.assertThat(tokens.getAccessExpiresIn()).isEqualTo(expectedAccessExpiresIn);

            var expectedRefreshExpiresIn = tokens.getIssuedAt() + refreshTokenValidityMillis;
            it.assertThat(tokens.getRefreshExpiresIn()).isEqualTo(expectedRefreshExpiresIn);
        });
    }

    @Test
    public void refreshTokens() {
        // GIVEN
        var id = randomUUID();
        var issuedAt = new Date();
        var userDetails = new DomainUserDetails(id, USER);
        var accessToken = createToken(
                userDetails,
                issuedAt,
                ACCESS,
                Date.from(LocalDate
                        .of(2020, 5, 10)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()));

        var refreshToken = createToken(
                userDetails,
                issuedAt,
                REFRESH,
                Date.from(LocalDate
                        .of(2020, 5, 10)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()));

        // WHEN
        JwtTokens tokens = tokensProvider.refreshTokens(accessToken, refreshToken);

        // THEN
        assertSoftly(it -> {
            it.assertThat(tokens).isNotNull();
            it.assertThat(tokens.getIssuedAt()).isGreaterThan(0);
            it.assertThat(tokens.getIssuedAt()).isLessThanOrEqualTo(currentTimeMillis());
            it.assertThat(tokens.getAccessToken()).isNotNull();
            it.assertThat(tokens.getRefreshToken()).isNotNull();

            var expectedAccessExpiresIn = tokens.getIssuedAt() + accessTokenValidityMillis;
            it.assertThat(tokens.getAccessExpiresIn()).isEqualTo(expectedAccessExpiresIn);

            var expectedRefreshExpiresIn = tokens.getIssuedAt() + refreshTokenValidityMillis;
            it.assertThat(tokens.getRefreshExpiresIn()).isEqualTo(expectedRefreshExpiresIn);
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
        builder.signWith(hmacShaKeyFor(BASE64.decode(appProperties.getJwt().getBase64Secret())), HS512);
        return builder.compact();
    }

}
