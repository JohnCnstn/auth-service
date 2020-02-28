package com.johncnstn.auth.security;

import static com.johncnstn.auth.security.JwtTokenType.ACCESS;
import static com.johncnstn.auth.security.JwtTokenType.REFRESH;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static java.lang.System.currentTimeMillis;

import com.johncnstn.auth.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
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
public class TokensProvider {

    private static final String ROLE_KEY = "rol";
    private static final String TYPE_KEY = "typ";

    private final AppProperties appProperties;

    private Key key;
    private long accessTokenValidityMillis;
    private long refreshTokenValidityMillis;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(BASE64.decode(appProperties.getJwt().getBase64Secret()));
        this.accessTokenValidityMillis = appProperties.getJwt().getAccessTokenValidity() * 1000;
        this.refreshTokenValidityMillis = appProperties.getJwt().getRefreshTokenValidity() * 1000;
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

        var accessExpiresIn = new Date(currentTimeMillis + accessTokenValidityMillis);
        var accessToken = createToken(userDetails, issuedAt, ACCESS, accessExpiresIn);

        var refreshExpiresIn = new Date(currentTimeMillis + refreshTokenValidityMillis);
        var refreshToken = createToken(userDetails, issuedAt, REFRESH, refreshExpiresIn);

        // TODO @yegor.usoltsev: add handler for ExpiredJwtException

        return JwtTokens.builder()
                .issuedAt(issuedAt.getTime())
                .accessToken(accessToken)
                .accessExpiresIn(accessExpiresIn.getTime())
                .refreshToken(refreshToken)
                .refreshExpiresIn(refreshExpiresIn.getTime())
                .userDetails(userDetails)
                .build();
    }

    public JwtTokens refreshTokens(String accessToken, String refreshToken) {
        var accessTokenClaims = getClaims(accessToken, true);
        var refreshTokenClaims = getClaims(refreshToken, false);
        if (!validateClaimsAreSame(accessTokenClaims, refreshTokenClaims)) {
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

    private boolean validateClaimsAreSame(Claims accessTokenClaims, Claims refreshTokenClaims) {
        return Objects.equals(accessTokenClaims.get(TYPE_KEY), ACCESS.getValue())
                || Objects.equals(REFRESH.getValue(), refreshTokenClaims.get(TYPE_KEY))
                || Objects.equals(accessTokenClaims.getIssuedAt(), refreshTokenClaims.getIssuedAt())
                || Objects.equals(accessTokenClaims.getSubject(), refreshTokenClaims.getSubject())
                || Objects.equals(
                        accessTokenClaims.get(ROLE_KEY), refreshTokenClaims.get(ROLE_KEY));
    }
}
