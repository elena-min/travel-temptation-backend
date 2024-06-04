package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.converter.NotificationMessageConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.NotificationMessage;
import org.individualproject.domain.User;
import org.individualproject.persistence.NotificationsRepository;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.NotificationMessageEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ChatService {
    private NotificationsRepository notificationsRepository;
    private UserRepository userRepository;
    private AccessToken accessToken;

    public void saveNotification(NotificationMessage notificationMessage){
        UserEntity fromUser = userRepository.findByUsername(notificationMessage.getFrom());
                //.orElseThrow(() -> new RuntimeException("User not found: " + notificationMessage.getFrom()));
        UserEntity toUser = userRepository.findByUsername(notificationMessage.getTo());
                //.orElseThrow(() -> new RuntimeException("User not found: " + notificationMessage.getTo()));

        NotificationMessageEntity notificationMessageEntity = NotificationMessageEntity.builder()
                .id(notificationMessage.getId())
                .toUser(toUser)
                .fromUser(fromUser)
                .message(notificationMessage.getMessage())
                .read(false)
                .timestamp(LocalDateTime.now())
                .build();
        notificationsRepository.save(notificationMessageEntity);
    }



    public List<NotificationMessage> getChatsForUser(Long userIdReceiver){
        Long loggedinUserId = accessToken.getUserID();
        return notificationsRepository.findAllUniqueChatsForUser(loggedinUserId)
                .stream()
                .map(NotificationMessageConverter::mapToDomain)
                .toList();
    }

    public List<NotificationMessage> getChatMessages(Long userIdReceiver, Long userIdSender){
        List<NotificationMessageEntity> messages1 = notificationsRepository.findByToUserAndFromUser(UserEntity.builder().id(userIdReceiver).build(), UserEntity.builder().id(userIdSender).build());
        List<NotificationMessageEntity> messages2 = notificationsRepository.findByToUserAndFromUser(UserEntity.builder().id(userIdSender).build(), UserEntity.builder().id(userIdReceiver).build());

        List<NotificationMessage> allMessages = Stream.concat(
                        messages1.stream(), messages2.stream())
                .map(NotificationMessageConverter::mapToDomain)
                .sorted(Comparator.comparing(NotificationMessage::getTimestamp))
                .toList();


        return allMessages;

    }
}
