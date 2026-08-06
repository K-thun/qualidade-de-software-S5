package com.furreverhome.Furrever_Home.unittests.services.authenticationServices;

import com.furreverhome.Furrever_Home.dto.auth.PetAdopterSignupRequest;
import com.furreverhome.Furrever_Home.dto.auth.ShelterSignupRequest;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.entities.Shelter;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.repository.PetAdopterRepository;
import com.furreverhome.Furrever_Home.repository.ShelterRepository;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.authenticationServices.impl.UserRegistrationServiceImpl;
import com.furreverhome.Furrever_Home.services.emailservice.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ShelterRepository shelterRepository;
    @Mock
    private PetAdopterRepository petAdopterRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    /**
     * Tests the successful signup of a pet adopter.
     */
    @Test
    void testSuccessfulPetAdopterSignup() throws MessagingException {
        // Arrange
        PetAdopterSignupRequest signupRequest = new PetAdopterSignupRequest();

        signupRequest.setEmail("johndoe@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setRole("PET_ADOPTER");
        signupRequest.setCheckRole(1);
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPhone_number("1234567890");
        signupRequest.setAddress("1234 Maple Street");
        signupRequest.setCity("Springfield");
        signupRequest.setCountry("Neverland");
        signupRequest.setZipcode("98765");

        String encodedPassword = "encodedPassword";
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn(encodedPassword);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), eq(true));

        // Act
        boolean result = userRegistrationService.register("http://localhost:8080", signupRequest);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(petAdopterRepository, times(1)).save(any(PetAdopter.class));
        verify(emailService, times(1)).sendEmail(
                eq(signupRequest.getEmail()),
                anyString(),
                anyString(),
                eq(true));
    }

    /**
     * Tests the successful signup of a shelter.
     */
    @Test
    void testSuccessfulShelterSignup() throws Exception {
        // Arrange
        ShelterSignupRequest signupRequest = new ShelterSignupRequest();
        signupRequest.setEmail("shelter@example.com");
        signupRequest.setPassword("securePassword");
        signupRequest.setCheckRole(2);

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), eq(true));

        // Act
        boolean result = userRegistrationService.register("http://localhost:8080", signupRequest);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(shelterRepository, times(1)).save(any(Shelter.class));
        verify(emailService, times(1)).sendEmail(
                eq(signupRequest.getEmail()),
                anyString(),
                anyString(),
                eq(true));
    }

    /**
     * Tests signup with an existing email address.
     */
    @Test
    void testSignupWithExistingEmail() {
        // Arrange
        PetAdopterSignupRequest signupRequest = new PetAdopterSignupRequest();
        signupRequest.setEmail("existingemail@example.com");
        signupRequest.setPassword("password");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userRegistrationService.register("http://localhost:8080", signupRequest);
        });

        String expectedMessage = "User Already Exists";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(userRepository, never()).save(any(User.class));
    }
}
