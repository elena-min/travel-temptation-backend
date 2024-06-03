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
import java.util.List;

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

//    public List<NotificationMessage> getMessagesBetweenUsers(Long userIdReceiver){
//        Long loggedinUserId = accessToken.getUserID();
//        return notificationsRepository.getMessagesBetweenUsers(loggedinUserId, userIdReceiver)
//                .stream()
//                .map(NotificationMessageConverter::mapToDomain)
//                .toList();
//    }
}
