package com.johncnstn.auth.security;

import static com.johncnstn.auth.util.TestUtils.email;
import static com.johncnstn.auth.util.TestUtils.userEntity;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.johncnstn.auth.entity.enums.UserRoleType;
import com.johncnstn.auth.repository.UserRepository;
import com.johncnstn.auth.unit.AbstractUnitTest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DomainUserDetailsServiceTest extends AbstractUnitTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private DomainUserDetailsService userDetailsService;

    @Test
    public void loadExistingUserShouldReturnUser() {
        // GIVEN
        var userEntity = userEntity(UserRoleType.USER);
        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));

        // WHEN
        var userDetails = userDetailsService.loadUserByUsername(userEntity.getEmail());

        // THEN
        assertSoftly(
                it -> {
                    it.assertThat(userDetails).isNotNull();
                    it.assertThat(userDetails.getUsername())
                            .isEqualTo(String.valueOf(userEntity.getId()));
                    it.assertThat(userDetails.getUserId()).isEqualTo(userEntity.getId());
                    it.assertThat(userDetails.getPassword())
                            .isEqualTo(userEntity.getPasswordHash());
                    it.assertThat(userDetails.getAuthorities())
                            .containsExactly(DomainGrantedAuthority.USER);
                });
    }

    @Test
    public void loadNotExistingUserShouldThrowException() {
        // GIVEN
        var email = email();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // WHEN
        Executable executable = () -> userDetailsService.loadUserByUsername(email);

        // THEN
        assertThrows(UsernameNotFoundException.class, executable);
    }
}
