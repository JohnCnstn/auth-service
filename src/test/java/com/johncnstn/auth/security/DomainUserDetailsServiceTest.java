package com.johncnstn.auth.security;

import com.johncnstn.auth.entity.UserEntity;
import com.johncnstn.auth.entity.enums.UserRoleEntity;
import com.johncnstn.auth.repository.UserRepository;
import com.johncnstn.auth.unit.AbstractUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DomainUserDetailsServiceTest extends AbstractUnitTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void testLoadExistingUser() {
        // GIVEN
        var userDetailsService = new DomainUserDetailsService(userRepository);
        var email = "test@mail.com";
        var userEntity = new UserEntity(UUID.randomUUID(), email, "demo123", UserRoleEntity.USER);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(userEntity);

        // WHEN
        var userDetails = userDetailsService.loadUserByUsername(email);

        // THEN
        assertSoftly(it -> {
            it.assertThat(userDetails).isNotNull();
            it.assertThat(userDetails.getUsername()).isEqualTo(String.valueOf(userEntity.getId()));
            it.assertThat(userDetails.getUserId()).isEqualTo(userEntity.getId());
            it.assertThat(userDetails.getPassword()).isEqualTo(userEntity.getPasswordHash());
            it.assertThat(userDetails.getAuthorities()).containsExactly(DomainGrantedAuthority.USER);
        });
    }

    @Test
    public void testLoadNotExistingUser() {
        // GIVEN
        var userDetailsService = new DomainUserDetailsService(userRepository);
        var email = "test@mail.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        // WHEN
        Executable executable = () -> userDetailsService.loadUserByUsername(email);

        // THEN
        assertThrows(UsernameNotFoundException.class, executable);
    }
}
