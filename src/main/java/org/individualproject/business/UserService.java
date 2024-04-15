package org.individualproject.business;

import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.domain.*;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;
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
                .password(request.getPassword())
                .hashedPassword(request.getHashedPassword())
                .salt(request.getSalt())
                .gender(request.getGender())
                .build();

        UserEntity userEntity = userRepository.save(newUser);
        return UserConverter.mapToDomain(userEntity);
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
        Optional<UserEntity> optionalUser = userRepository.findById(request.getId());
        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            existingUser.setBirthDate(request.getBirthDate());
            existingUser.setGender(request.getGender());
            existingUser.setEmail(request.getEmail());
            existingUser.setPassword(request.getPassword());
            existingUser.setHashedPassword(request.getHashedPassword());
            existingUser.setSalt(request.getSalt());
            userRepository.save(existingUser);
            return true;
        } else {
            return false;
        }
    }

}
