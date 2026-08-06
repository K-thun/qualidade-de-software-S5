package com.furreverhome.Furrever_Home.services.authenticationServices;

import com.furreverhome.Furrever_Home.dto.GenericResponse;
import com.furreverhome.Furrever_Home.dto.user.PasswordDto;

/**
 * Handles the full password-reset lifecycle (request, token validation,
 * reset, and authenticated password change). Extracted from
 * {@code AuthenticationServiceImpl} so that authentication (signin/JWT) and
 * password recovery are no longer a single class.
 */
public interface PasswordResetService {

    GenericResponse resetByEmail(String contextPath, String email);

    GenericResponse resetPassword(PasswordDto passwordDto);

    String validatePasswordResetToken(String token);

    GenericResponse updateUserPassword(PasswordDto passwordDto);
}
