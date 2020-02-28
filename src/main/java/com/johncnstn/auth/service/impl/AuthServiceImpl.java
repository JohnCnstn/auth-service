package com.johncnstn.auth.service.impl;

import static com.johncnstn.auth.mapper.RoleMapper.ROLE_MAPPER;
import static com.johncnstn.auth.mapper.TokenMapper.TOKEN_MAPPER;
import static com.johncnstn.auth.mapper.UserMapper.USER_MAPPER;
import static org.apache.commons.lang3.StringUtils.lowerCase;

import com.johncnstn.auth.generated.model.RefreshTokenRequest;
import com.johncnstn.auth.generated.model.SignInRequest;
import com.johncnstn.auth.generated.model.Token;
import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.generated.model.UserRole;
import com.johncnstn.auth.repository.UserRepository;
import com.johncnstn.auth.security.TokensProvider;
import com.johncnstn.auth.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokensProvider tokensProvider;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User signUp(User user, UserRole userRole) {
        var userEntity = USER_MAPPER.toEntity(user);
        var role = ROLE_MAPPER.toType(userRole);
        userEntity.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        userEntity.setEmail(lowerCase(user.getEmail()));
        userEntity.setRole(role);
        userRepository.save(userEntity);
        var savedUser = userRepository.findByEmail(user.getEmail());
        return USER_MAPPER.toModel(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Token signIn(SignInRequest signInRequest) {
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        lowerCase(signInRequest.getEmail()), signInRequest.getPassword());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var token =
                tokensProvider.createTokens((UsernamePasswordAuthenticationToken) authentication);
        return TOKEN_MAPPER.toModel(token);
    }

    @Override
    @Transactional(readOnly = true)
    public Token refreshToken(RefreshTokenRequest refreshTokenRequest) {
        var token =
                tokensProvider.refreshTokens(
                        refreshTokenRequest.getAccessToken(),
                        refreshTokenRequest.getRefreshToken());
        return TOKEN_MAPPER.toModel(token);
    }
}
