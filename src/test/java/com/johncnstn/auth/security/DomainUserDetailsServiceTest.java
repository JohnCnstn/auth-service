package com.johncnstn.auth.security;

import com.johncnstn.auth.entity.enums.UserRoleEntity;
import com.johncnstn.auth.repository.UserRepository;
import com.johncnstn.auth.unit.AbstractUnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.johncnstn.auth.util.TestUtilKt.email;
import static com.johncnstn.auth.util.TestUtilKt.userEntity;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class DomainUserDetailsServiceTest extends AbstractUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DomainUserDetailsService userDetailsService;

    @Test
    public void loadExistingUserShouldReturnUser() {
        // GIVEN
        var userEntity = userEntity(UserRoleEntity.USER);
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(userEntity);

        // WHEN
        var userDetails = userDetailsService.loadUserByUsername(userEntity.getEmail());

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
    public void loadNotExistingUserShouldThrowException() {
        // GIVEN
        var email = email();
        when(userRepository.findByEmail(email)).thenReturn(null);

        // WHEN
        Executable executable = () -> userDetailsService.loadUserByUsername(email);

        // THEN
        assertThrows(UsernameNotFoundException.class, executable);
    }

}
