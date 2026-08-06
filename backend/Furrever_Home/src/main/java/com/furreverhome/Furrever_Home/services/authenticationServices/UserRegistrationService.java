package com.furreverhome.Furrever_Home.services.authenticationServices;

import com.furreverhome.Furrever_Home.dto.auth.SignupRequest;
import jakarta.mail.MessagingException;

/**
 * Handles registration of new accounts (pet adopters and shelters), including
 * persistence of the role-specific profile and the verification e-mail.
 * Extracted from {@code AuthenticationServiceImpl} so that authentication
 * (signin/JWT) and registration are no longer a single class.
 */
public interface UserRegistrationService {
    boolean register(String appUrl, SignupRequest signupRequest) throws MessagingException;
}
