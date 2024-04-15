package org.individualproject.business.converter;

import org.individualproject.domain.Excursion;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {

    public static User mapToDomain(UserEntity userEntity) {
        User user = User.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .birthDate(userEntity.getBirthDate())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .hashedPassword(userEntity.getHashedPassword())
                .salt(userEntity.getSalt())
                .gender(userEntity.getGender())
                .build();
        return user;
    }
    public static List<User> mapToDomainList(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(UserConverter::mapToDomain)
                .collect(Collectors.toList());
    }
}
