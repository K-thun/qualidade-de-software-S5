package com.furreverhome.Furrever_Home.services.chat;

import com.furreverhome.Furrever_Home.dto.chat.UserRoleEntities;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.entities.Shelter;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.enums.Role;
import com.furreverhome.Furrever_Home.repository.PetAdopterRepository;
import com.furreverhome.Furrever_Home.repository.ShelterRepository;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Resolves which {@link User}, {@link PetAdopter}, and {@link Shelter}
 * entities are involved in a chat exchange.
 * <p>
 * Extracted from {@code ChatServiceImpl}, which previously mixed this
 * lookup/validation logic (two overloads of what was
 * {@code determineRolesAndGetEntities}, plus {@code validateUserExists})
 * with chat-channel orchestration and token generation. Isolating it here
 * makes the role-resolution rules independently testable and reusable by
 * any future chat-adjacent feature.
 */
@Component
@RequiredArgsConstructor
public class ChatUserResolver {

    private final UserRepository userRepository;
    private final PetAdopterRepository petAdopterRepository;
    private final ShelterRepository shelterRepository;

    /**
     * Validates the existence of a user with the given ID.
     *
     * @param userId The ID of the user to validate.
     * @return The user if found.
     * @throws ResponseStatusException if the user is not found.
     */
    public User validateUserExists(long userId) throws ResponseStatusException {
        return userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * Determines the roles of both users involved in a chat session and
     * retrieves their entities.
     *
     * @param fromUser The user initiating the chat session.
     * @param toUser   The user receiving the chat session.
     * @return The entities of the users involved.
     */
    public UserRoleEntities resolveRoles(User fromUser, User toUser) {
        Shelter shelter;
        PetAdopter petAdopter;

        if (fromUser.getRole() == Role.PETADOPTER) {
            petAdopter = petAdopterRepository.findByUserId(fromUser.getId()).get();
            shelter = shelterRepository.findByUserId(toUser.getId()).get();
        } else {
            petAdopter = petAdopterRepository.findByUserId(toUser.getId()).get();
            shelter = shelterRepository.findByUserId(fromUser.getId()).get();
        }

        return new UserRoleEntities(shelter, petAdopter);
    }

    /**
     * Determines the role of a single user and retrieves their entity (the
     * other side of {@link UserRoleEntities} is left {@code null}).
     *
     * @param user The user for whom to determine the role.
     * @return The entity of the user.
     */
    public UserRoleEntities resolveRoles(User user) {
        Shelter shelter;
        PetAdopter petAdopter;

        if (user.getRole() == Role.PETADOPTER) {
            petAdopter = petAdopterRepository.findByUserId(user.getId()).get();
            shelter = null;
        } else {
            petAdopter = null;
            shelter = shelterRepository.findByUserId(user.getId()).get();
        }

        return new UserRoleEntities(shelter, petAdopter);
    }
}
