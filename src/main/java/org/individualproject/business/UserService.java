package org.individualproject.business;

import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
    private AccessToken requestAccessToken;
    @Autowired
    public UserService(UserRepository uRepository){
        this.userRepository = uRepository;
    }
    public List<User> getUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return UserConverter.mapToDomainList(userEntities);
    }
    public Optional<User> getUser(Long id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        return userEntity.map(UserConverter::mapToDomain);
    }

    public User createUser(CreateUserRequest request){
        UserEntity newUser = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .username(request.getUsername())
                .hashedPassword(request.getPassword())
                .gender(request.getGender())
                .build();

        UserEntity userEntity = userRepository.save(newUser);
        if (userEntity.getId() != null) {
            return UserConverter.mapToDomain(userEntity);
        } else {
            return null;
        }
    }

    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean updateUser(UpdateUserRequest request) {
        if (requestAccessToken.getUserID() != request.getId()) {
            throw new UnauthorizedDataAccessException("USER_ID_NOT_FROM_LOGGED_IN_USER");
        }

        Optional<UserEntity> optionalUser = userRepository.findById(request.getId());
        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();

            if(existingUser.getFirstName()!= null){
                existingUser.setFirstName(request.getFirstName());
            }
            if(existingUser.getLastName()!= null){
                existingUser.setLastName(request.getLastName());
            }
            if(existingUser.getGender()!= null){
                existingUser.setGender(request.getGender());
            }
            if(existingUser.getBirthDate()!= null){
                existingUser.setBirthDate(request.getBirthDate());
            }
            userRepository.save(existingUser);
            return true;
        } else {
            return false;
        }
    }

}
