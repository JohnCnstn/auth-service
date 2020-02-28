package com.johncnstn.auth.security;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum DomainGrantedAuthority implements GrantedAuthority {
    USER("user"),
    ADMIN("admin");

    private static final Map<String, DomainGrantedAuthority> MAP =
            Map.of(
                    "user", USER,
                    "admin", ADMIN);

    private final String authority;

    public static DomainGrantedAuthority fromAuthority(String authority) {
        return MAP.get(authority);
    }
}
