package com.johncnstn.auth.integration;

import static com.johncnstn.auth.mapper.UserMapper.USER_MAPPER;
import static com.johncnstn.auth.security.JwtFilter.TOKEN_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.johncnstn.auth.entity.UserEntity;
import com.johncnstn.auth.entity.enums.UserRoleType;
import com.johncnstn.auth.generated.model.User;
import com.johncnstn.auth.initializer.PostgresInitializer;
import com.johncnstn.auth.repository.UserRepository;
import com.johncnstn.auth.security.JwtTokens;
import com.johncnstn.auth.security.TokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {PostgresInitializer.class})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @Autowired protected MockMvc mockMvc;

    @Autowired protected ObjectMapper objectMapper;

    @Autowired protected TokenProvider tokenProvider;

    @Autowired protected UserRepository userRepository;

    @Autowired protected PasswordEncoder passwordEncoder;

    @AfterEach
    public void clearDatabase() {
        userRepository.deleteAll();
    }

    protected UserEntity createUser(User user) {
        return createUser(user, UserRoleType.USER);
    }

    private UserEntity createUser(User request, UserRoleType role) {
        var userEntity = USER_MAPPER.toEntity(request);
        userEntity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(role);
        return userRepository.save(userEntity);
    }

    protected String accessTokenForUser(UserEntity entity) {
        return TOKEN_PREFIX + tokensForUser(entity).getAccessToken();
    }

    protected JwtTokens tokensForUser(UserEntity entity) {
        var details = USER_MAPPER.toUserDetails(entity);
        return tokenProvider.createTokens(details);
    }
}
