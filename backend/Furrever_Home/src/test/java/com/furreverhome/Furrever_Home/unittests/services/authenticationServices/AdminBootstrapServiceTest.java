package com.furreverhome.Furrever_Home.unittests.services.authenticationServices;

import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.enums.Role;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.authenticationServices.AdminBootstrapService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminBootstrapService adminBootstrapService;

    /**
     * Tests the creation of an admin account when no admin account exists.
     */
    @Test
    void testWhenNoAdminAccountExistsThenCreateAdminAccount() {
        // Arrange
        ReflectionTestUtils.setField(adminBootstrapService, "adminEmail", "admin@gmail.com");
        ReflectionTestUtils.setField(adminBootstrapService, "adminPassword", "Jp@32padhiyar");
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(null);

        // Act
        adminBootstrapService.createAdminAccountIfMissing();

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Tests the creation of an admin account when an admin account already exists.
     */
    @Test
    void testWhenAdminAccountExistsThenDoNotCreateAdminAccount() {
        // Arrange
        User existingAdmin = new User();
        existingAdmin.setEmail("admin@gmail.com");
        existingAdmin.setRole(Role.ADMIN);
        when(userRepository.findByRole(Role.ADMIN)).thenReturn(existingAdmin);

        // Act
        adminBootstrapService.createAdminAccountIfMissing();

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }
}
