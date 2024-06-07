package org.individualproject.business.converter;

import org.individualproject.domain.NotificationMessage;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.NotificationMessageEntity;

public class NotificationMessageConverter {
    public static NotificationMessage mapToDomain(NotificationMessageEntity notificationMessageEntity) {
        User toUser = UserConverter.mapToDomain(notificationMessageEntity.getToUser());
        User fromUser = UserConverter.mapToDomain(notificationMessageEntity.getFromUser());
        return NotificationMessage.builder()
                .id(notificationMessageEntity.getId())
                .from(fromUser.getUsername())
                .to(toUser.getUsername())
                .isRead(notificationMessageEntity.isRead())
                .message(notificationMessageEntity.getMessage())
                .timestamp(notificationMessageEntity.getTimestamp())
                .build();
    }

    private NotificationMessageConverter(){}

}
