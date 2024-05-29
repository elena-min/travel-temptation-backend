package org.individualproject.business.converter;

import org.individualproject.domain.User;
import org.individualproject.persistence.entity.UserEntity;
import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {

    public static User mapToDomain(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .birthDate(userEntity.getBirthDate())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .hashedPassword(userEntity.getHashedPassword())
                .gender(userEntity.getGender())
                .build();
    }
    public static List<User> mapToDomainList(List<UserEntity> userEntities) {
        return userEntities.stream()
                .map(UserConverter::mapToDomain)
                .collect(Collectors.toList());
    }

    public static UserEntity convertToEntity(User user){
        return UserEntity.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .username(user.getUsername())
                .hashedPassword(user.getHashedPassword())
                .gender(user.getGender())
                .build();
    }

    private UserConverter(){}
}
