package com.furreverhome.Furrever_Home.services.authenticationServices;

import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.enums.Role;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the initial admin account on application startup, if one does not
 * already exist. Extracted from {@code AuthenticationServiceImpl}, which
 * previously mixed this one-time bootstrap concern with signup/signin/reset
 * logic.
 * <p>
 * Credentials are sourced from configuration ({@code admin.email} /
 * {@code admin.password}), which resolve from the {@code ADMIN_EMAIL} /
 * {@code ADMIN_PASSWORD} environment variables. They must never be
 * hardcoded in source code.
 */
@Component
@RequiredArgsConstructor
public class AdminBootstrapService {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @PostConstruct
    public void createAdminAccountIfMissing() {
        User existingAdmin = userRepository.findByRole(Role.ADMIN);

        if (existingAdmin == null) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setRole(Role.ADMIN);
            admin.setVerified(Boolean.TRUE);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);
            logger.info("Admin account successfully created.");
        }
    }
}
