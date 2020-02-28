package com.johncnstn.auth.security;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class DomainUserDetails implements UserDetails {

    private final Set<DomainGrantedAuthority> authorities;

    private final UUID userId;
    private final DomainGrantedAuthority role;

    public DomainUserDetails(UUID userId, DomainGrantedAuthority role) {
        this.authorities = Collections.singleton(role);
        this.userId = userId;
        this.role = role;
    }

    @Override
    public Set<DomainGrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Deprecated
    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Deprecated
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Deprecated
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Deprecated
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Deprecated
    @Override
    public boolean isEnabled() {
        return true;
    }
}
