package com.furreverhome.Furrever_Home.unittests.services.chat;

import com.furreverhome.Furrever_Home.dto.chat.UserRoleEntities;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.entities.Shelter;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.enums.Role;
import com.furreverhome.Furrever_Home.repository.PetAdopterRepository;
import com.furreverhome.Furrever_Home.repository.ShelterRepository;
import com.furreverhome.Furrever_Home.repository.UserRepository;
import com.furreverhome.Furrever_Home.services.chat.ChatUserResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatUserResolverTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PetAdopterRepository petAdopterRepository;
    @Mock
    private ShelterRepository shelterRepository;

    @InjectMocks
    private ChatUserResolver chatUserResolver;

    private User petAdopterUser;
    private PetAdopter petAdopter;
    private User shelterUser;
    private Shelter shelter;

    @BeforeEach
    void setUp() {
        petAdopterUser = new User();
        petAdopterUser.setId(1L);
        petAdopterUser.setRole(Role.PETADOPTER);

        petAdopter = new PetAdopter();
        petAdopter.setId(1L);
        petAdopter.setUser(petAdopterUser);

        shelterUser = new User();
        shelterUser.setId(2L);
        shelterUser.setRole(Role.SHELTER);

        shelter = new Shelter();
        shelter.setId(1L);
        shelter.setUser(shelterUser);
    }

    @Test
    void validateUserExists_whenFound_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(petAdopterUser));

        User result = chatUserResolver.validateUserExists(1L);

        assertEquals(petAdopterUser, result);
    }

    @Test
    void validateUserExists_whenNotFound_throwsResponseStatusException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> chatUserResolver.validateUserExists(999L));
    }

    @Test
    void resolveRoles_fromPetAdopterToShelter_resolvesBothSides() {
        when(petAdopterRepository.findByUserId(petAdopterUser.getId())).thenReturn(Optional.of(petAdopter));
        when(shelterRepository.findByUserId(shelterUser.getId())).thenReturn(Optional.of(shelter));

        UserRoleEntities result = chatUserResolver.resolveRoles(petAdopterUser, shelterUser);

        assertEquals(petAdopter, result.petAdopter());
        assertEquals(shelter, result.shelter());
    }

    @Test
    void resolveRoles_fromShelterToPetAdopter_resolvesBothSides() {
        when(petAdopterRepository.findByUserId(petAdopterUser.getId())).thenReturn(Optional.of(petAdopter));
        when(shelterRepository.findByUserId(shelterUser.getId())).thenReturn(Optional.of(shelter));

        UserRoleEntities result = chatUserResolver.resolveRoles(shelterUser, petAdopterUser);

        assertEquals(petAdopter, result.petAdopter());
        assertEquals(shelter, result.shelter());
    }

    @Test
    void resolveRoles_singleUser_petAdopter_leavesShelterNull() {
        when(petAdopterRepository.findByUserId(petAdopterUser.getId())).thenReturn(Optional.of(petAdopter));

        UserRoleEntities result = chatUserResolver.resolveRoles(petAdopterUser);

        assertEquals(petAdopter, result.petAdopter());
        assertNull(result.shelter());
    }

    @Test
    void resolveRoles_singleUser_shelter_leavesPetAdopterNull() {
        when(shelterRepository.findByUserId(shelterUser.getId())).thenReturn(Optional.of(shelter));

        UserRoleEntities result = chatUserResolver.resolveRoles(shelterUser);

        assertEquals(shelter, result.shelter());
        assertNull(result.petAdopter());
    }
}
