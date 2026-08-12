package com.furreverhome.Furrever_Home.unittests.services.impl;

import com.furreverhome.Furrever_Home.dto.auth.JwtAuthenticationResponse;
import com.furreverhome.Furrever_Home.dto.auth.RefreshTokenRequest;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.jwtservices.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class JwtServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private User testUser;
    private UserDetails userDetails;
    private String validToken;
    private String userName = "test@example.com";

    /**
     * Sets up the mock objects and initializes test data before each test method.
     * {@code secretKey} is set manually because this is a plain Mockito test
     * (no Spring context), so the real {@code @Value("${jwt.secret}")}
     * injection never runs — the field would otherwise be null and
     * {@code getSigningKey()} would throw.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "e2315034876692352de9a59ef03e9f39582606f2a1acf1f1a792e9d85a77eb90");

        testUser = new User();
        testUser.setEmail(userName);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername(userName)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        validToken = jwtService.generateToken(userDetails);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    }

    /**
     * Tests refreshing a token successfully.
     */
    @Test
    void testRefreshTokenSuccessful() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setToken(validToken);

        JwtAuthenticationResponse response = jwtService.refreshToken(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals(validToken, response.getRefreshToken());
    }

    /**
     * Tests refreshing a token when the user is not found.
     */
    @Test
    void testRefreshTokenUserNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setToken(validToken);

        // Configure the userRepository mock to return an empty Optional when findByEmail is called
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            jwtService.refreshToken(request);
        }, "Expected refreshToken to throw, but it didn't");

        // Verify the exception message or type as per your implementation
        assertTrue(exception instanceof RuntimeException); // Use the specific exception class you're throwing
    }

    /**
     * Tests generating a token successfully.
     */
    @Test
    void testGenerateTokenSuccessful() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    /**
     * Tests generating a refresh token successfully.
     */
    @Test
    void testGenerateRefreshTokenSuccessful() {
        String refreshToken = jwtService.generateRefreshToken(Collections.emptyMap(), userDetails);
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    /**
     * Tests extracting a username from a JWT token successfully.
     */
    @Test
    void testExtractUserNameSuccessful() {
        String extractedUserName = jwtService.extractUserName(validToken);
        assertEquals(userName, extractedUserName);
    }

    /**
     * Tests validating a token successfully.
     */
    @Test
    void testIsTokenValidSuccessful() {
        boolean isValid = jwtService.isTokenValid(validToken, userDetails);
        assertTrue(isValid);
    }
}
