package com.furreverhome.Furrever_Home.services.authenticationServices.impl;

import com.furreverhome.Furrever_Home.dto.GenericResponse;
import com.furreverhome.Furrever_Home.dto.user.PasswordDto;
import com.furreverhome.Furrever_Home.entities.PasswordResetToken;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.repository.PasswordTokenRepository;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.authenticationServices.PasswordResetService;
import com.furreverhome.Furrever_Home.services.emailservice.EmailService;
import com.furreverhome.Furrever_Home.services.jwtservices.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordTokenRepository passwordTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Override
    public GenericResponse resetByEmail(final String contextPath, String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        User user = optionalUser.get();
        String token = jwtService.generateToken(user);

        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);

        String url = contextPath + "/api/auth/redirectChangePassword?token=" + token;
        String linkText = "Click here to reset your password";
        String message = "<p>Password reset successfully. Please use the link below to reset your password. Note that the link is valid for "
                + PasswordResetToken.EXPIRATION + " minutes.</p>"
                + "<a href=\"" + url + "\">" + linkText + "</a>";

        try {
            emailService.sendEmail(user.getEmail(), "Password Reset", message, true);
        } catch (MessagingException e) {
            emailService.sendEmail(user.getEmail(), "Password Reset", message);
        }

        return new GenericResponse(
                "A password reset email has been sent. Follow the instructions inside\n" + message
        );
    }

    @Override
    public GenericResponse resetPassword(PasswordDto passwordDto) {
        String result = validatePasswordResetToken(passwordDto.getToken());

        if (result != null) {
            return new GenericResponse(result);
        }

        Optional<User> user = getUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent()) {
            changeUserPassword(user.get(), passwordDto.getNewPassword());
            invalidateResetToken(passwordDto.getToken());
            return new GenericResponse("Password reset successfully");
        } else {
            return new GenericResponse(null, "This username is invalid, or does not exist");
        }
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    @Override
    public GenericResponse updateUserPassword(PasswordDto passwordDto) {
        final Optional<User> user = userRepository.findByEmail(passwordDto.getEmail());
        if (user.isPresent()) {
            if (!checkIfValidOldPassword(user.get(), passwordDto.getOldPassword())) {
                return new GenericResponse(null, "Invalid Old Password.");
            }

            changeUserPassword(user.get(), passwordDto.getNewPassword());
            return new GenericResponse("Password updated successfully");
        } else {
            return new GenericResponse(null, "Not a valid user.");
        }
    }

    private void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    private boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    private Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token).getUser());
    }

    private void invalidateResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
        if (passwordResetToken != null) {
            passwordResetToken.setToken(null);
            passwordTokenRepository.save(passwordResetToken);
        }
    }
}
