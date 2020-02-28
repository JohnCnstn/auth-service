package com.johncnstn.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtTokenType {
    ACCESS("access"),
    REFRESH("refresh");

    private final String value;
}
