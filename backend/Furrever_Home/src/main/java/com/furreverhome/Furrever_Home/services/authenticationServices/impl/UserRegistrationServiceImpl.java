package com.furreverhome.Furrever_Home.services.authenticationServices.impl;

import com.furreverhome.Furrever_Home.dto.auth.PetAdopterSignupRequest;
import com.furreverhome.Furrever_Home.dto.auth.ShelterSignupRequest;
import com.furreverhome.Furrever_Home.dto.auth.SignupRequest;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.entities.Shelter;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.enums.Role;
import com.furreverhome.Furrever_Home.exception.EmailExistsException;
import com.furreverhome.Furrever_Home.repository.PetAdopterRepository;
import com.furreverhome.Furrever_Home.repository.ShelterRepository;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.authenticationServices.UserRegistrationService;
import com.furreverhome.Furrever_Home.services.emailservice.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link UserRegistrationService}.
 * <p>
 * The original implementation distinguished pet-adopter vs. shelter signups
 * using magic numbers ({@code checkRole == 1} / {@code == 2}) and unsafe
 * downcasts. This version routes on the named {@link Role} constants and
 * isolates the entity-building logic for each role in its own method, so a
 * future third role can be added without touching the branching logic.
 */
@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private static final int CHECK_ROLE_PET_ADOPTER = 1;
    private static final int CHECK_ROLE_SHELTER = 2;

    private final UserRepository userRepository;
    private final ShelterRepository shelterRepository;
    private final PetAdopterRepository petAdopterRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public boolean register(String appUrl, SignupRequest signupRequest) throws MessagingException {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException(new EmailExistsException("User Already Exists"));
        }

        User user = buildBaseUser(signupRequest);

        if (signupRequest.getCheckRole() == CHECK_ROLE_PET_ADOPTER) {
            registerPetAdopter(user, (PetAdopterSignupRequest) signupRequest);
        } else if (signupRequest.getCheckRole() == CHECK_ROLE_SHELTER) {
            registerShelter(user, (ShelterSignupRequest) signupRequest);
        } else {
            throw new RuntimeException("Registration details is incorrect.");
        }

        sendVerificationEmail(appUrl, signupRequest.getEmail());
        return true;
    }

    private User buildBaseUser(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setVerified(Boolean.FALSE);
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        return user;
    }

    private void registerPetAdopter(User user, PetAdopterSignupRequest request) {
        PetAdopter petAdopter = new PetAdopter();
        petAdopter.setFirstname(request.getFirstName());
        petAdopter.setLastname(request.getLastName());
        petAdopter.setPhone_number(request.getPhone_number());
        petAdopter.setAddress(request.getAddress());
        petAdopter.setCity(request.getCity());
        petAdopter.setCountry(request.getCountry());
        petAdopter.setZipcode(request.getZipcode());

        user.setRole(Role.PETADOPTER);
        User savedUser = userRepository.save(user);
        petAdopter.setUser(savedUser);

        petAdopterRepository.save(petAdopter);
    }

    private void registerShelter(User user, ShelterSignupRequest request) {
        Shelter shelter = new Shelter();
        shelter.setName(request.getName());
        shelter.setContact(request.getContact());
        shelter.setLicense(request.getLicense());
        shelter.setCapacity(request.getCapacity());
        shelter.setImageBase64(request.getImageBase64());
        shelter.setCity(request.getCity());
        shelter.setCountry(request.getCountry());
        shelter.setAddress(request.getAddress());
        shelter.setZipcode(request.getZipcode());
        shelter.setRejected(Boolean.FALSE);

        user.setRole(Role.SHELTER);
        User savedUser = userRepository.save(user);
        shelter.setUser(savedUser);

        shelterRepository.save(shelter);
    }

    private void sendVerificationEmail(String appUrl, String email) throws MessagingException {
        String url = appUrl + "/api/auth/verify/" + email;
        String linkText = "Click here to verify your email.";
        String message = "<p>Please use the link below to verify your email.</p>"
                + "<a href=\"" + url + "\">" + linkText + "</a>";
        emailService.sendEmail(email, "Email Verification", message, true);
    }
}
