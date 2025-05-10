package org.individualproject.business;

import org.individualproject.business.converter.NotificationMessageConverter;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.NotificationMessage;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.NotificationsRepository;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.NotificationMessageEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private AccessToken accessToken;

    @Mock
    private NotificationsRepository notificationsRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void saveNotification() {
        UserEntity fromUser = UserEntity.builder().id(1L).username("user1").build();
        UserEntity toUser = UserEntity.builder().id(2L).username("user2").build();

        NotificationMessageEntity notificationMessageEntity = new NotificationMessageEntity();
        notificationMessageEntity.setId(1L);
        notificationMessageEntity.setFromUser(fromUser);
        notificationMessageEntity.setToUser(toUser);
        notificationMessageEntity.setMessage("Hello!");

        NotificationMessage notificationMessage = NotificationMessageConverter.mapToDomain(notificationMessageEntity);


        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(fromUser));
        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(toUser));

        // Act
        chatService.saveNotification(notificationMessage);

        // Assert
        verify(userRepository).findByUsername("user1");
        verify(userRepository).findByUsername("user2");
        verify(notificationsRepository).save(any(NotificationMessageEntity.class));
    }

    @Test
    void saveNotification_userNotFound() {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setId(1L);
        notificationMessage.setFrom("user1");
        notificationMessage.setTo("user2");
        notificationMessage.setMessage("Hello!");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chatService.saveNotification(notificationMessage));

        verify(userRepository).findByUsername("user1");
        verify(userRepository, never()).findByUsername("user2");
        verify(notificationsRepository, never()).save(any(NotificationMessageEntity.class));
    }

    @Test
    void getChatsForUser() {
        Long userIdReceiver = 1L;
        when(accessToken.getUserID()).thenReturn(userIdReceiver);
        UserEntity fromUser = UserEntity.builder().id(1L).username("user1").build();
        UserEntity toUser = UserEntity.builder().id(2L).username("user2").build();


        List<NotificationMessageEntity> mockEntities = Arrays.asList(
                new NotificationMessageEntity(1L, toUser, fromUser, "Message 1", LocalDateTime.now(), false ),
                new NotificationMessageEntity(2L, toUser, fromUser, "Message 2",  LocalDateTime.now(), false )
        );

        when(notificationsRepository.findAllUniqueChatsForUser(userIdReceiver)).thenReturn(mockEntities);

        // Act
        List<NotificationMessage> result = chatService.getChatsForUser(userIdReceiver);

        // Assert
        assertEquals(2, result.size());
        List<String> expectedMessages = mockEntities.stream()
                .map(NotificationMessageEntity::getMessage)
                .collect(Collectors.toList());
        List<String> actualMessages = result.stream()
                .map(NotificationMessage::getMessage)
                .collect(Collectors.toList());
        assertEquals(expectedMessages, actualMessages);

        // Verify interactions
        verify(accessToken).getUserID();
        verify(notificationsRepository).findAllUniqueChatsForUser(userIdReceiver);
    }

    @Test
    void getChatsForUser_unAuthorizedUser() {
        Long userIdReceiver = 1L;
        when(accessToken.getUserID()).thenReturn(2L);
        assertThrows(UnauthorizedDataAccessException.class, () -> chatService.getChatsForUser(userIdReceiver));

        verify(notificationsRepository, never()).findAllUniqueChatsForUser(anyLong());
    }

    @Test
    void getChatMessages() {
        // Arrange
        Long userIdReceiver = 1L;
        Long userIdSender = 2L;

        UserEntity receiver = UserEntity.builder().id(userIdReceiver).build();
        UserEntity sender = UserEntity.builder().id(userIdSender).build();

        NotificationMessageEntity message1 = new NotificationMessageEntity(1L, receiver, sender,"hello!", LocalDateTime.of(2023, 1, 1, 10, 0), true);
        NotificationMessageEntity message2 = new NotificationMessageEntity(2L, sender, receiver,"How are you?", LocalDateTime.of(2023, 1, 1, 11, 0), true);

        // Mock behavior of findByToUserAndFromUser
        when(notificationsRepository.findByToUserAndFromUser(receiver, sender))
                .thenReturn(Arrays.asList(message1));
        when(notificationsRepository.findByToUserAndFromUser(sender, receiver))
                .thenReturn(Arrays.asList(message2));

        // Act
        List<NotificationMessage> result = chatService.getChatMessages(userIdReceiver, userIdSender);

        // Assert
        verify(notificationsRepository).findByToUserAndFromUser(receiver, sender);
        verify(notificationsRepository).findByToUserAndFromUser(sender, receiver);

        List<NotificationMessage> expectedMessages = Arrays.asList(
                NotificationMessageConverter.mapToDomain(message1),
                NotificationMessageConverter.mapToDomain(message2)
        );
        expectedMessages.sort(Comparator.comparing(NotificationMessage::getTimestamp));

        assertEquals(expectedMessages, result);
    }

    @Test
    void getChatMessages_MessagesNotFound() {
        Long userIdReceiver = 1L;
        Long userIdSender = 2L;

        when(notificationsRepository.findByToUserAndFromUser(any(), any()))
                .thenReturn(Collections.emptyList());

        List<NotificationMessage> result = chatService.getChatMessages(userIdReceiver, userIdSender);

        verify(notificationsRepository).findByToUserAndFromUser(
                UserEntity.builder().id(userIdReceiver).build(),
                UserEntity.builder().id(userIdSender).build()
        );
        verify(notificationsRepository).findByToUserAndFromUser(
                UserEntity.builder().id(userIdSender).build(),
                UserEntity.builder().id(userIdReceiver).build()
        );

        assertEquals(0, result.size());
    }




}