package com.johncnstn.auth.service.impl;

import static com.johncnstn.auth.generated.model.UserRole.USER;
import static com.johncnstn.auth.mapper.RoleMapper.ROLE_MAPPER;
import static com.johncnstn.auth.mapper.TokenMapper.TOKEN_MAPPER;
import static com.johncnstn.auth.mapper.UserMapper.USER_MAPPER;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import com.johncnstn.auth.entity.UserEntity;
import com.johncnstn.auth.generated.model.RefreshTokenRequest;
import com.johncnstn.auth.generated.model.SignInRequest;
import com.johncnstn.auth.generated.model.SignUpRequest;
import com.johncnstn.auth.generated.model.Token;
import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.generated.model.UserRole;
import com.johncnstn.auth.repository.UserRepository;
import com.johncnstn.auth.security.JwtTokens;
import com.johncnstn.auth.security.TokenProvider;
import com.johncnstn.auth.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User signUp(SignUpRequest request) {
        return signUp(request, USER);
    }

    private User signUp(SignUpRequest request, UserRole role) {
        var passwordHash = hashPassword(request.getPassword());
        var email = prepareEmail(request.getEmail());
        var roleType = ROLE_MAPPER.toType(role);
        var userEntity = new UserEntity();
        userEntity.setPasswordHash(passwordHash);
        userEntity.setEmail(email);
        userEntity.setRole(roleType);
        var savedEntity = userRepository.save(userEntity);
        return USER_MAPPER.toModel(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Token signIn(SignInRequest request) {
        var email = prepareEmail(request.getEmail());
        var password = request.getPassword();
        var authToken = buildAuthToken(email, password);
        var authentication = authenticate(authToken);
        var token = createTokens(authentication);
        return TOKEN_MAPPER.toModel(token);
    }

    @Override
    @Transactional(readOnly = true)
    public Token refreshToken(RefreshTokenRequest request) {
        var accessToken = request.getAccessToken();
        var refreshToken = request.getRefreshToken();
        var token = refreshTokens(accessToken, refreshToken);
        return TOKEN_MAPPER.toModel(token);
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String prepareEmail(String rawEmail) {
        return lowerCase(rawEmail);
    }

    private Authentication buildAuthToken(String email, String password) {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

    private Authentication authenticate(Authentication authToken) {
        return authenticationManager.authenticate(authToken);
    }

    private JwtTokens createTokens(Authentication authentication) {
        return tokenProvider.createTokens((UsernamePasswordAuthenticationToken) authentication);
    }

    private JwtTokens refreshTokens(String accessToken, String refreshToken) {
        return tokenProvider.refreshTokens(accessToken, refreshToken);
    }
}
