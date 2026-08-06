package com.furreverhome.Furrever_Home.unittests.services.authenticationServices;

import com.furreverhome.Furrever_Home.dto.GenericResponse;
import com.furreverhome.Furrever_Home.dto.user.PasswordDto;
import com.furreverhome.Furrever_Home.entities.PasswordResetToken;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.repository.PasswordTokenRepository;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.authenticationServices.impl.PasswordResetServiceImpl;
import com.furreverhome.Furrever_Home.services.emailservice.EmailService;
import com.furreverhome.Furrever_Home.services.jwtservices.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordTokenRepository passwordTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setVerified(true);
    }

    /**
     * Tests the successful password reset process.
     */
    @Test
    void testResetByEmailSuccessfulReset() {
        // Arrange
        String email = "user@example.com";
        String token = "randomToken";
        String contextPath = "http://example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn(token);

        // Act
        GenericResponse response = passwordResetService.resetByEmail(contextPath, email);

        // Assert
        assertNotNull(response);
        assertTrue(response.getMessage().contains("A password reset email has been sent"));
        verify(jwtService).generateToken(mockUser);
        verify(passwordTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendEmail(eq(email), eq("Password Reset"), anyString(), eq(true));
    }

    /**
     * Tests the password reset process when the user is not found.
     */
    @Test
    void testResetByEmailUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String contextPath = "http://example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            passwordResetService.resetByEmail(contextPath, email);
        }, "Expected exception for user not found");
    }

    /**
     * Tests the reset password process with an invalid token.
     */
    @Test
    void testResetPasswordWithInvalidTokenReturnsErrorMessage() {
        // Arrange
        String token = "invalidToken";
        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setToken(token);
        passwordDto.setNewPassword("newPassword");
        when(passwordTokenRepository.findByToken(token)).thenReturn(null);

        // Act
        GenericResponse response = passwordResetService.resetPassword(passwordDto);

        // Assert
        assertEquals("invalidToken", response.getMessage());
    }
}
