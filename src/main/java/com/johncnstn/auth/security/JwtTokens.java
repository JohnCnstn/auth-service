package com.johncnstn.auth.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokens {

    private final long issuedAt;
    private final String accessToken;
    private final long accessExpiresIn;
    private final String refreshToken;
    private final long refreshExpiresIn;
    private final DomainUserDetails userDetails;
}
