package com.johncnstn.auth.security;

import static com.johncnstn.auth.exception.ExceptionUtils.usernameNotFound;
import static com.johncnstn.auth.mapper.UserMapper.USER_MAPPER;

import com.johncnstn.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DomainUserDetails loadUserByUsername(String username) {
        return loadUserByEmail(username);
    }

    private DomainUserDetails loadUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .map(USER_MAPPER::toUserDetails)
                .orElseThrow(
                        usernameNotFound(
                                String.format(
                                        "User with email %s was not found in the database",
                                        email)));
    }
}
