package com.furreverhome.Furrever_Home.services.authenticationServices;


import com.furreverhome.Furrever_Home.dto.GenericResponse;
import com.furreverhome.Furrever_Home.dto.auth.JwtAuthenticationResponse;
import com.furreverhome.Furrever_Home.dto.auth.SigninRequest;
import com.furreverhome.Furrever_Home.dto.auth.SignupRequest;
import com.furreverhome.Furrever_Home.dto.user.PasswordDto;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.entities.Shelter;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.repository.PetAdopterRepository;
import com.furreverhome.Furrever_Home.repository.ShelterRepository;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.jwtservices.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

/**
 * Authentication entry point: signin (JWT issuance) and e-mail verification.
 * <p>
 * This class used to also own account registration, password-reset, and
 * admin-account bootstrap. Those concerns have been extracted to
 * {@link UserRegistrationService}, {@link PasswordResetService}, and
 * {@link AdminBootstrapService} respectively — each independently testable
 * and each with a single reason to change. This class now acts as a thin
 * facade over those collaborators so that {@code AuthenticationController}
 * and the {@link AuthenticationService} contract did not need to change.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final ShelterRepository shelterRepository;

    private final PetAdopterRepository petAdopterRepository;

    private final UserRegistrationService userRegistrationService;

    private final PasswordResetService passwordResetService;

    /**
     * Registers a new user (either a shelter or a pet adopter) and sends a verification email.
     * Delegates to {@link UserRegistrationService}.
     *
     * @param appUrl The base URL of the application.
     * @param signupRequest The request containing the signup details.
     * @return true if the signup process is successful, false otherwise.
     * @throws MessagingException if an error occurs while sending the email.
     */
    @Override
    public boolean signup(String appUrl, SignupRequest signupRequest) throws MessagingException {
        return userRegistrationService.register(appUrl, signupRequest);
    }

    /**
     * Authenticates a user based on the provided signin request.
     *
     * @param signinRequest The request containing the signin details.
     * @return The JWT authentication response if authentication is successful.
     * @throws BadCredentialsException if the provided credentials are invalid.
     * @throws DisabledException if the user account is disabled.
     * @throws UsernameNotFoundException if the username is not found.
     */
    @Override
    public JwtAuthenticationResponse signin(SigninRequest signinRequest) throws
            BadCredentialsException,
            DisabledException,
            UsernameNotFoundException {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),
                    signinRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        var user = userRepository.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email and password"));
        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        if(user.getVerified()) {
            Optional<Shelter> optionalShelter = shelterRepository.findByUserId(user.getId());
            Optional<PetAdopter> optionalPetAdopter = petAdopterRepository.findByUserId(user.getId());
            var jwt = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshToken);
            jwtAuthenticationResponse.setUserRole(user.getRole());
            jwtAuthenticationResponse.setUserId(user.getId());
            if(isShelterAccepted(user.getId())) {
                jwtAuthenticationResponse.setShelterId(optionalShelter.get().getId());
            }
            if(optionalPetAdopter.isPresent()) {
                jwtAuthenticationResponse.setPetAdopterId(optionalPetAdopter.get().getId());
            }
            jwtAuthenticationResponse.setVerified(user.getVerified());
            return jwtAuthenticationResponse;
        }

        jwtAuthenticationResponse.setVerified(user.getVerified());
        return jwtAuthenticationResponse;
    }

    /**
     * Checks if the shelter associated with the given user ID is accepted.
     *
     * @param userId The ID of the user.
     * @return true if the shelter is accepted, false otherwise.
     */
    public boolean isShelterAccepted (long userId) {
        Optional<Shelter> optionalShelter = shelterRepository.findByUserId(userId);
        if(optionalShelter.isPresent()) {
            Shelter shelter = optionalShelter.get();
            if(shelter.isAccepted()) {
                return true;
            } else {
                throw new RuntimeException("Admin approval pending..");
            }
        }
        return false;
    }

    /**
     * Verifies a user by email.
     *
     * @param email The email of the user to verify.
     * @return true if the user is successfully verified, false otherwise.
     */
    @Override
    public boolean verifyByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(Boolean.TRUE);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * Initiates the password reset process. Delegates to {@link PasswordResetService}.
     */
    @Override
    public GenericResponse resetByEmail(final String contextPath, String email) {
        return passwordResetService.resetByEmail(contextPath, email);
    }

    /**
     * Resets the user's password. Delegates to {@link PasswordResetService}.
     */
    @Override
    public GenericResponse resetPassword(PasswordDto passwordDto) {
        return passwordResetService.resetPassword(passwordDto);
    }

    /**
     * Validates a password reset token. Delegates to {@link PasswordResetService}.
     */
    @Override
    public String validatePasswordResetToken(String token) {
        return passwordResetService.validatePasswordResetToken(token);
    }

    /**
     * Updates the user's password. Delegates to {@link PasswordResetService}.
     */
    @Override
    public GenericResponse updateUserPassword(PasswordDto passwordDto) {
        return passwordResetService.updateUserPassword(passwordDto);
    }

    /**
     * Resolves the "change password" link redirect. Delegates to {@link PasswordResetService}.
     */
    @Override
    public String buildPasswordResetRedirectUrl(String token, String loginUrl, String updatePasswordUrl) {
        return passwordResetService.buildPasswordResetRedirectUrl(token, loginUrl, updatePasswordUrl);
    }
}
