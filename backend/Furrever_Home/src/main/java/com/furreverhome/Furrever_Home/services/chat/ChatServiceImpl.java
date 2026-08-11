package com.furreverhome.Furrever_Home.unittests.services.chat;

import com.furreverhome.Furrever_Home.dto.chat.ChatCredentialsResponse;
import com.furreverhome.Furrever_Home.dto.chat.UserRoleEntities;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.entities.Shelter;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.enums.Role;
import com.furreverhome.Furrever_Home.services.chat.ChatProviderService;
import com.furreverhome.Furrever_Home.services.chat.ChatServiceImpl;
import com.furreverhome.Furrever_Home.services.chat.ChatUserResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Covers chat-channel orchestration and token generation only. Role/user
 * resolution (previously tested here indirectly through repository mocks)
 * now has its own dedicated coverage in {@link ChatUserResolverTest}, since
 * that logic moved to {@link ChatUserResolver}.
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private ChatProviderService chatProviderService;

    @Mock
    private ChatUserResolver chatUserResolver;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User petAdopterUser;
    private PetAdopter petAdopter;
    private User shelterUser;
    private Shelter shelter;

    @BeforeEach
    void setUp() {
        petAdopterUser = new User();
        petAdopterUser.setId(1L);
        petAdopterUser.setEmail("adopter@example.com");
        petAdopterUser.setRole(Role.PETADOPTER);

        petAdopter = new PetAdopter();
        petAdopter.setId(1L);
        petAdopter.setUser(petAdopterUser);
        petAdopter.setFirstname("John");

        shelterUser = new User();
        shelterUser.setId(2L);
        shelterUser.setEmail("shelter@example.com");
        shelterUser.setRole(Role.SHELTER);

        shelter = new Shelter();
        shelter.setId(1L);
        shelter.setUser(shelterUser);
        shelter.setName("Happy Paws");
    }

    /**
     * Test case for creating a chat session between a pet adopter and a shelter.
     * It verifies that a chat session is created successfully once the resolver
     * has identified both sides.
     */
    @Test
    void createChatSession_createsSessionSuccessfully() {
        when(chatUserResolver.validateUserExists(petAdopterUser.getId())).thenReturn(petAdopterUser);
        when(chatUserResolver.validateUserExists(shelterUser.getId())).thenReturn(shelterUser);
        when(chatUserResolver.resolveRoles(petAdopterUser, shelterUser))
                .thenReturn(new UserRoleEntities(shelter, petAdopter));

        doNothing().when(chatProviderService).createChatChannel(any(), any(), any());
        when(chatProviderService.getApiKey()).thenReturn("apiKey");
        when(chatProviderService.getToken(any(), any(), any())).thenReturn("token");

        ChatCredentialsResponse response = chatService.createChatSession(petAdopterUser.getId(), shelterUser.getId());

        assertNotNull(response);
        assertEquals("apiKey", response.apiKey());
        assertEquals("token", response.token());

        verify(chatUserResolver).resolveRoles(petAdopterUser, shelterUser);
        verify(chatProviderService).createChatChannel(any(PetAdopter.class), any(Shelter.class), anyString());
        verify(chatProviderService).getApiKey();
        verify(chatProviderService).getToken(anyString(), any(), any());
    }

    /**
     * Test case for getting the chat history for a pet adopter.
     * It verifies that the chat history is retrieved successfully and that
     * only the pet adopter is upserted (shelter side is null).
     */
    @Test
    void getChatHistory_forPetAdopter_returnsChatHistorySuccessfully() {
        when(chatUserResolver.validateUserExists(petAdopterUser.getId())).thenReturn(petAdopterUser);
        when(chatUserResolver.resolveRoles(petAdopterUser)).thenReturn(new UserRoleEntities(null, petAdopter));

        when(chatProviderService.getApiKey()).thenReturn("apiKey");
        when(chatProviderService.getToken(anyString(), any(), any())).thenReturn("token");

        ChatCredentialsResponse response = chatService.getChatHistory(petAdopterUser.getId());

        assertNotNull(response);
        assertEquals("apiKey", response.apiKey());
        assertNull(response.channelId());
        assertEquals("token", response.token());

        verify(chatProviderService).addUser(eq(petAdopter.getId().toString()), eq(petAdopter.getFirstname()), anyString());
        verify(chatProviderService, never()).createChatChannel(any(), any(), anyString());
    }

    /**
     * Test case for getting the chat history for a shelter.
     * It verifies that only the shelter side is upserted (pet adopter side is null).
     */
    @Test
    void getChatHistory_forShelter_returnsChatHistorySuccessfully() {
        when(chatUserResolver.validateUserExists(shelterUser.getId())).thenReturn(shelterUser);
        when(chatUserResolver.resolveRoles(shelterUser)).thenReturn(new UserRoleEntities(shelter, null));

        when(chatProviderService.getApiKey()).thenReturn("apiKey");
        when(chatProviderService.getToken(anyString(), any(), any())).thenReturn("token");

        ChatCredentialsResponse response = chatService.getChatHistory(shelterUser.getId());

        assertNotNull(response);
        assertEquals("apiKey", response.apiKey());
        assertNull(response.channelId());
        assertEquals("token", response.token());

        verify(chatProviderService).addUser(eq(shelter.getId().toString()), eq(shelter.getName()), anyString());
        verify(chatProviderService, never()).createChatChannel(any(), any(), anyString());
    }

    /**
     * Test case verifying that a {@link ResponseStatusException} raised by the
     * resolver (nonexistent user) propagates out of createChatSession.
     */
    @Test
    void createChatSession_whenResolverThrows_propagatesException() {
        when(chatUserResolver.validateUserExists(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        assertThrows(ResponseStatusException.class, () -> chatService.createChatSession(999L, shelterUser.getId()));
    }

    /**
     * Test case verifying that a {@link ResponseStatusException} raised by the
     * resolver (nonexistent user) propagates out of getChatHistory.
     */
    @Test
    void getChatHistory_whenResolverThrows_propagatesException() {
        when(chatUserResolver.validateUserExists(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        assertThrows(ResponseStatusException.class, () -> chatService.getChatHistory(999L));
    }
}
