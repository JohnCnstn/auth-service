package com.johncnstn.auth.security;

import static com.johncnstn.auth.security.JwtTokenType.ACCESS;
import static com.johncnstn.auth.security.JwtTokenType.REFRESH;
import static com.johncnstn.auth.util.ConvertUtils.secsToMillis;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.lang.System.currentTimeMillis;

import com.johncnstn.auth.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String ROLE_KEY = "rol";
    private static final String TYPE_KEY = "typ";

    private final AppProperties appProperties;

    private Key key;
    private long accessTokenValidityMillis;
    private long refreshTokenValidityMillis;

    @PostConstruct
    public void init() {
        var base64Secret = appProperties.getJwt().getBase64Secret();
        var plainSecret = BASE64.decode(base64Secret);
        key = hmacShaKeyFor(plainSecret);

        var accessTokenValiditySecs = appProperties.getJwt().getAccessTokenValidity();
        accessTokenValidityMillis = secsToMillis(accessTokenValiditySecs);

        var refreshTokenValiditySecs = appProperties.getJwt().getRefreshTokenValidity();
        refreshTokenValidityMillis = secsToMillis(refreshTokenValiditySecs);
    }

    public JwtTokens createTokens(UsernamePasswordAuthenticationToken authentication) {
        var userDetails = (DomainUserDetails) authentication.getPrincipal();
        return createTokens(userDetails);
    }

    public JwtTokens createTokens(AbstractAuthenticationToken authentication) {
        var userDetails = (DomainUserDetails) authentication.getPrincipal();
        return createTokens(userDetails);
    }

    public JwtTokens createTokens(DomainUserDetails userDetails) {
        var currentTimeMillis = currentTimeMillis();
        var issuedAt = new Date(currentTimeMillis);

        var accessExpirationTimeMillis = currentTimeMillis + accessTokenValidityMillis;
        var accessExpirationTime = new Date(accessExpirationTimeMillis);
        var accessToken = createToken(userDetails, issuedAt, ACCESS, accessExpirationTime);

        var refreshExpirationTimeMillis = currentTimeMillis + refreshTokenValidityMillis;
        var refreshExpirationTime = new Date(refreshExpirationTimeMillis);
        var refreshToken = createToken(userDetails, issuedAt, REFRESH, refreshExpirationTime);

        return JwtTokens.builder()
                .issuedAt(issuedAt.getTime())
                .accessToken(accessToken)
                .accessExpiresIn(accessExpirationTime.getTime())
                .refreshToken(refreshToken)
                .refreshExpiresIn(refreshExpirationTime.getTime())
                .userDetails(userDetails)
                .build();
    }

    public JwtTokens refreshTokens(String accessToken, String refreshToken) {
        if (!isValidTokensForRefresh(accessToken, refreshToken)) {
            throw new BadCredentialsException("Bad credentials");
        }

        var authentication = getAuthentication(refreshToken);
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return createTokens((UsernamePasswordAuthenticationToken) authentication);
        } else if (authentication instanceof AbstractAuthenticationToken) {
            return createTokens((AbstractAuthenticationToken) authentication);
        } else {
            throw new IllegalStateException();
        }
    }

    public Authentication getAuthentication(String token) {
        var claims = getClaims(token, false);
        var role = DomainGrantedAuthority.fromAuthority(claims.get(ROLE_KEY, String.class));
        var userId = UUID.fromString(claims.getSubject());
        var userDetails = new DomainUserDetails(userId, role);
        return new UsernamePasswordAuthenticationToken(
                userDetails, token, userDetails.getAuthorities());
    }

    public boolean validateAccessToken(String token) {
        return validateToken(
                () ->
                        Jwts.parser()
                                .require(TYPE_KEY, ACCESS.getValue())
                                .setSigningKey(key)
                                .parseClaimsJws(token));
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(
                () ->
                        Jwts.parser()
                                .require(TYPE_KEY, REFRESH.getValue())
                                .setSigningKey(key)
                                .parseClaimsJws(token));
    }

    private boolean validateToken(Supplier<Jws<Claims>> supplier) {
        try {
            supplier.get();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT token.");
        } catch (IncorrectClaimException e) {
            log.debug("Incorrect JWT token.");
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.debug("JWT token compact of handler are invalid.");
        }
        return false;
    }

    private String createToken(
            DomainUserDetails userDetails, Date issuedAt, JwtTokenType type, Date expiresIn) {

        var builder = Jwts.builder();
        if (userDetails.getUserId() != null) {
            builder.setSubject(userDetails.getUserId().toString());
        }
        builder.setExpiration(expiresIn);
        builder.setIssuedAt(issuedAt);
        builder.claim(ROLE_KEY, userDetails.getRole().getAuthority());
        builder.claim(TYPE_KEY, type.getValue());
        builder.signWith(key, HS512);
        return builder.compact();
    }

    private Claims getClaims(String accessToken, boolean ignoreExpired) {
        Claims accessTokenClaims;
        try {
            accessTokenClaims =
                    Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            if (ignoreExpired) {
                accessTokenClaims = e.getClaims();
            } else {
                throw e;
            }
        }
        return accessTokenClaims;
    }

    private boolean isValidTokensForRefresh(String accessToken, String refreshToken) {
        var accessTokenClaims = getClaims(accessToken, true);
        var refreshTokenClaims = getClaims(refreshToken, false);
        return hasTypeClaimEqualTo(accessTokenClaims, ACCESS)
                && hasTypeClaimEqualTo(refreshTokenClaims, REFRESH)
                && hasEqualPayloadClaims(accessTokenClaims, refreshTokenClaims);
    }

    private boolean hasTypeClaimEqualTo(Claims claims, JwtTokenType tokenType) {
        return Objects.equals(claims.get(TYPE_KEY), tokenType.getValue());
    }

    private boolean hasEqualPayloadClaims(Claims firstClaims, Claims secondClaims) {
        return Objects.equals(firstClaims.getIssuedAt(), secondClaims.getIssuedAt())
                && Objects.equals(firstClaims.getSubject(), secondClaims.getSubject())
                && Objects.equals(firstClaims.get(ROLE_KEY), secondClaims.get(ROLE_KEY));
    }
}
