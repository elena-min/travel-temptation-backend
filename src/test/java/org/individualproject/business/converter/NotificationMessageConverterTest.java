package org.individualproject.business.converter;

import org.individualproject.domain.NotificationMessage;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.entity.NotificationMessageEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMessageConverterTest {

    @Test
    void mapToDomain() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        LocalDateTime timestamp = LocalDateTime.now();

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .username("nickJonas")
                .hashedPassword("asdfgh")
                .gender(Gender.MALE)
                .build();

        UserEntity userEntity2 = UserEntity.builder()
                .id(2L)
                .firstName("Joe")
                .lastName("Jonas")
                .birthDate(date)
                .email("joeJonas@gmail.com")
                .username("joejonas123")
                .hashedPassword("lkjhgf")
                .gender(Gender.MALE)
                .build();

        NotificationMessageEntity notificationMessageEntity = NotificationMessageEntity.builder()
                .id(1L)
                .message("Hello!")
                .timestamp(timestamp)
                .read(false)
                .toUser(userEntity)
                .fromUser(userEntity2)
                .build();

        NotificationMessage notificationMessage = NotificationMessageConverter.mapToDomain(notificationMessageEntity);

        assertEquals(notificationMessageEntity.getId(), notificationMessage.getId());
        assertEquals(notificationMessageEntity.getMessage(), notificationMessage.getMessage());
        assertEquals(notificationMessageEntity.getFromUser().getUsername(), notificationMessage.getFrom());
        assertEquals(notificationMessageEntity.getToUser().getUsername(), notificationMessage.getTo());
        assertEquals(notificationMessageEntity.getTimestamp(), notificationMessage.getTimestamp());
    }
}