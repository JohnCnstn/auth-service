package com.johncnstn.auth.security;

import java.util.UUID;
import lombok.Getter;

@Getter
public class DomainUserDetailsWithPassword extends DomainUserDetails {

    private final String password;

    public DomainUserDetailsWithPassword(
            UUID userId, String password, DomainGrantedAuthority role) {
        super(userId, role);
        this.password = password;
    }
}
