package com.furreverhome.Furrever_Home.services.chat;

import com.furreverhome.Furrever_Home.dto.chat.ChatCredentialsResponse;
import com.furreverhome.Furrever_Home.entities.PetAdopter;
import com.furreverhome.Furrever_Home.entities.Shelter;
import com.furreverhome.Furrever_Home.entities.User;
import com.furreverhome.Furrever_Home.enums.Role;
import com.furreverhome.Furrever_Home.repository.ChatService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static com.furreverhome.Furrever_Home.services.chat.ChatUtils.getAvatarUrl;

/**
 * Chat session orchestration: creates chat channels and generates
 * client-side tokens.
 * <p>
 * User/role lookup used to live here too (see {@link ChatUserResolver} for
 * that logic, extracted so this class only deals with channel/token
 * orchestration).
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatProviderService chatProviderService;
    private final ChatUserResolver chatUserResolver;

    private final String CHATPETUSERIDCONSTANT = "testpetuser1";
    private final String CHATSHELTERUSERIDCONSTANT = "testshelteruser1";

    /**
     * Creates a chat session between two users.
     *
     * @param fromUserId The ID of the user initiating the chat session.
     * @param toUserId The ID of the user receiving the chat session.
     * @return The credentials for the chat session.
     */
    @Override
    public ChatCredentialsResponse createChatSession(long fromUserId, long toUserId) {
        // Validate users
        var fromUser = chatUserResolver.validateUserExists(fromUserId);
        var toUser = chatUserResolver.validateUserExists(toUserId);

        // Determine roles and get entities
        var entities = chatUserResolver.resolveRoles(fromUser, toUser);

        // Generate the unique channel ID
        String channelId = generateChannelId(entities.petAdopter().getUser().getEmail(), entities.shelter().getUser().getEmail());

        // Create a channel with both users
        chatProviderService.createChatChannel(entities.petAdopter(), entities.shelter(), channelId);

        // Generate token for the 'from' user to connect to the client-side
        return generateTokenForUser(fromUser, entities.petAdopter(), entities.shelter(), channelId);
    }

    /**
     * Retrieves the chat history for a user.
     *
     * @param userId The ID of the user.
     * @return The credentials for accessing the chat history.
     */
    @Override
    public ChatCredentialsResponse getChatHistory(long userId) {
        var user = chatUserResolver.validateUserExists(userId);
        var entities = chatUserResolver.resolveRoles(user);

        // Upsert both users
        if (entities.petAdopter() != null) {
            chatProviderService.addUser(
                    entities.petAdopter().getId().toString(),
                    entities.petAdopter().getFirstname(),
                    getAvatarUrl(entities.petAdopter().getUser().getEmail())
            );
        }
        if (entities.shelter() != null) {
            chatProviderService.addUser(
                    entities.shelter().getId().toString(),
                    entities.shelter().getName(),
                    getAvatarUrl(entities.shelter().getUser().getEmail())
            );
        }

        return generateTokenForUser(user, entities.petAdopter(), entities.shelter(), null);
    }

    /**
     * Generates a unique channel ID for a chat session based on user emails.
     *
     * @param petAdopterEmail The email of the pet adopter.
     * @param shelterEmail The email of the shelter.
     * @return The generated channel ID.
     */
    private String generateChannelId(String petAdopterEmail, String shelterEmail) {
        int maxLength = 14;
        String rawId = DigestUtils.sha256Hex(petAdopterEmail + shelterEmail);
        return rawId.substring(0, Math.min(rawId.length(), maxLength));
    }

    /**
     * Generates a chat token for a user and retrieves the credentials for accessing the chat.
     *
     * @param fromUser The user for whom to generate the token.
     * @param petAdopter The pet adopter entity.
     * @param shelter The shelter entity.
     * @param channelId The ID of the chat channel.
     * @return The credentials for accessing the chat.
     */
    private ChatCredentialsResponse generateTokenForUser(User fromUser, PetAdopter petAdopter, Shelter shelter, String channelId) {
        var calendar = new GregorianCalendar();
        calendar.add(Calendar.HOUR, 1);

        String token;
        String userId;
        String avatarUrl;

        if (fromUser.getRole() == Role.PETADOPTER) {
            userId = CHATPETUSERIDCONSTANT;
            avatarUrl = getAvatarUrl(petAdopter.getUser().getEmail());
            token = chatProviderService.getToken(userId, calendar.getTime(), null);
        } else {
            userId = CHATSHELTERUSERIDCONSTANT;
            avatarUrl = getAvatarUrl(shelter.getUser().getEmail());

            token = chatProviderService.getToken(userId, calendar.getTime(), null);
        }

        return new ChatCredentialsResponse(token, chatProviderService.getApiKey(), channelId, userId, avatarUrl);
    }
}
