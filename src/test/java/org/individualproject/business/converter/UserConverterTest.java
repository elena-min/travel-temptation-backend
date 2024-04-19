package org.individualproject.business.converter;

import org.individualproject.domain.Excursion;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserConverterTest {

    @Test
    void mapToDomain() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .password("NickBest")
                .hashedPassword("asdfgh")
                .salt("asdfghjkl")
                .gender(Gender.Male)
                .build();

        User user = UserConverter.mapToDomain(userEntity);
        //Assert

        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getFirstName(), user.getFirstName());
        assertEquals(userEntity.getLastName(), user.getLastName());
        assertEquals(userEntity.getBirthDate(), user.getBirthDate());
        assertEquals(userEntity.getEmail(), user.getEmail());
        assertEquals(userEntity.getPassword(), user.getPassword());
        assertEquals(userEntity.getHashedPassword(), user.getHashedPassword());
        assertEquals(userEntity.getSalt(), user.getSalt());
        assertEquals(userEntity.getGender(), user.getGender());

    }

    @Test
    void mapToDomainList() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        List<UserEntity> userEntities = new ArrayList<>();
        UserEntity userEntity1 = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .password("NickBest")
                .hashedPassword("asdfgh")
                .salt("asdfghjkl")
                .gender(Gender.Male)
                .build();
        UserEntity userEntity2 = UserEntity.builder()
                .id(2L)
                .firstName("Joe")
                .lastName("Jonas")
                .birthDate(date)
                .email("JOeJonas@gmail.com")
                .password("JOe123")
                .hashedPassword("zxcvbnm")
                .salt("sdfghjkl")
                .gender(Gender.Other)
                .build();

        userEntities.add(userEntity1);
        userEntities.add(userEntity2);
        List<User> users = UserConverter.mapToDomainList(userEntities);
        //Assert
        assertEquals(2, users.size());
        User user1 = users.get(0);
        User user2 = users.get(1);
        assertEquals(userEntity1.getId(), user1.getId());
        assertEquals(userEntity1.getFirstName(), user1.getFirstName());
        assertEquals(userEntity1.getLastName(), user1.getLastName());
        assertEquals(userEntity1.getBirthDate(), user1.getBirthDate());
        assertEquals(userEntity1.getEmail(), user1.getEmail());
        assertEquals(userEntity1.getPassword(), user1.getPassword());
        assertEquals(userEntity1.getHashedPassword(), user1.getHashedPassword());
        assertEquals(userEntity1.getSalt(), user1.getSalt());
        assertEquals(userEntity1.getGender(), user1.getGender());

        assertEquals(userEntity2.getId(), user2.getId());
        assertEquals(userEntity2.getFirstName(), user2.getFirstName());
        assertEquals(userEntity2.getLastName(), user2.getLastName());
        assertEquals(userEntity2.getBirthDate(), user2.getBirthDate());
        assertEquals(userEntity2.getEmail(), user2.getEmail());
        assertEquals(userEntity2.getPassword(), user2.getPassword());
        assertEquals(userEntity2.getHashedPassword(), user2.getHashedPassword());
        assertEquals(userEntity2.getSalt(), user2.getSalt());
        assertEquals(userEntity2.getGender(), user2.getGender());
    }

    @Test
    void convertToEntity() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        User user = User.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .password("NickBest")
                .hashedPassword("asdfgh")
                .salt("asdfghjkl")
                .gender(Gender.Male)
                .build();

        UserEntity userEntity = UserConverter.convertToEntity(user);

        //Assert
        assertEquals(user.getId(), userEntity.getId());
        assertEquals(user.getFirstName(), userEntity.getFirstName());
        assertEquals(user.getLastName(), userEntity.getLastName());
        assertEquals(user.getBirthDate(), userEntity.getBirthDate());
        assertEquals(user.getEmail(), userEntity.getEmail());
        assertEquals(user.getPassword(), userEntity.getPassword());
        assertEquals(user.getHashedPassword(), userEntity.getHashedPassword());
        assertEquals(user.getSalt(), userEntity.getSalt());
        assertEquals(user.getGender(), userEntity.getGender());
    }
}