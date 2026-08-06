package com.furreverhome.Furrever_Home.unittests.services.authenticationServices;

import com.furreverhome.Furrever_Home.dto.auth.JwtAuthenticationResponse;
import com.furreverhome.Furrever_Home.dto.auth.SigninRequest;
import com.furreverhome.Furrever_Home.repository.PetAdopterRepository;
import com.furreverhome.Furrever_Home.repository.ShelterRepository;
import com.furreverhome.Furrever_Home.services.authenticationServices.AuthenticationServiceImpl;
import com.furreverhome.Furrever_Home.services.authenticationServices.PasswordResetService;
import com.furreverhome.Furrever_Home.services.authenticationServices.UserRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.jwtservices.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Covers only signin and e-mail verification, which is all that remains in
 * {@link AuthenticationServiceImpl} after extracting registration to
 * {@link UserRegistrationService}, password recovery to
 * {@link PasswordResetService}, and admin bootstrap to
 * {@code AdminBootstrapService}. See {@code UserRegistrationServiceImplTest},
 * {@code PasswordResetServiceImplTest}, and {@code AdminBootstrapServiceTest}
 * for the tests that used to live here.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ShelterRepository shelterRepository;
    @Mock
    private PetAdopterRepository petAdopterRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private PasswordResetService passwordResetService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

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
     * Tests the successful sign-in process for a pet adopter.
     * It verifies that the authentication service correctly generates JWT and refresh tokens.
     */
    @Test
    void testSigninSuccessForPetAdopter() {
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("user@example.com");
        signinRequest.setPassword("password");
        when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(shelterRepository.findByUserId(mockUser.getId())).thenReturn(Optional.ofNullable(null));
        when(petAdopterRepository.findByUserId(mockUser.getId())).thenReturn(Optional.ofNullable(mock(PetAdopter.class)));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
        when(jwtService.generateToken(any(User.class))).thenReturn("mockJwtToken");
        when(jwtService.generateRefreshToken(any(), any(User.class))).thenReturn("mockRefreshToken");

        // Act
        JwtAuthenticationResponse response = authenticationService.signin(signinRequest);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals("mockJwtToken", response.getToken(), "JWT token does not match expected value");
        assertEquals("mockRefreshToken", response.getRefreshToken(), "Refresh token does not match expected value");

        // Verify
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(signinRequest.getEmail());
        verify(shelterRepository, times(2)).findByUserId(mockUser.getId());
        verify(petAdopterRepository).findByUserId(mockUser.getId());
        verify(jwtService).generateToken(mockUser);
    }

    @Test
    void testSigninWithBadCredentials() {
        // Arrange
        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("user@example.com");
        signinRequest.setPassword("password");
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(new BadCredentialsException("Incorrect username or password"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authenticationService.signin(signinRequest), "Expected BadCredentialsException to be thrown");

        // Verify
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtService);
    }

    /**
     * Tests sign-in with an unverified user.
     * It verifies that the authentication service correctly handles unverified users.
     */
    @Test
    void signinWithUnverifiedUser() {
        mockUser.setVerified(false);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        SigninRequest signinRequest = new SigninRequest();
        signinRequest.setEmail("user@example.com");
        signinRequest.setPassword("password");

        JwtAuthenticationResponse response = authenticationService.signin(signinRequest);

        assertFalse(response.getVerified());
    }

    /**
     * Tests verifying a user by email when the user exists and is verified.
     * It verifies that the authentication service correctly updates the user's verification status.
     */
    @Test
    void testVerifyByEmailUserExistsUserVerified() {
        // Arrange
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setVerified(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        boolean result = authenticationService.verifyByEmail(email);

        // Assert
        assertTrue(result);
        assertTrue(user.getVerified());
        verify(userRepository).save(user);
    }

    /**
     * Tests the verification process for a non-existing user.
     */
    @Test
    void testVerifyByEmailUserDoesNotExistReturnFalse() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean result = authenticationService.verifyByEmail(email);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
}
