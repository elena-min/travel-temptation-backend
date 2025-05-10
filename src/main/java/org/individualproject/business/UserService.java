package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.persistence.*;
import org.individualproject.persistence.entity.BookingEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor

public class UserService {

    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private BookingRepository bookingRepository;
    private PaymentDetailsRepository paymentDetailsRepository;
    private ExcursionRepository excursionRepository;
    private NotificationsRepository notificationsRepository;
    private AccessToken requestAccessToken;
   public List<User> getUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return UserConverter.mapToDomainList(userEntities);
    }
    public Optional<User> getUser(Long id) {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if (userEntity.isPresent()) {
            return Optional.of(UserConverter.mapToDomain(userEntity.get()));
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public Optional<User> getUserByUsername(String username) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
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

    @Transactional
    public boolean deleteUser(Long id) {
        try {
            if (!Objects.equals(requestAccessToken.getUserID(), id)) {
                throw new UnauthorizedDataAccessException("USER_ID_NOT_FROM_LOGGED_IN_USER");
            }
            Optional<UserEntity> user = userRepository.findById(id);

            if(user.isPresent()){
                UserEntity userEntity = user.get();
                notificationsRepository.deleteByUserId(userEntity.getId());
                reviewRepository.deleteByUserWriter(userEntity);
                reviewRepository.deleteByTravelAgency(userEntity);
                bookingRepository.deleteByUser(userEntity);
                paymentDetailsRepository.deleteByUser(userEntity);

                excursionRepository.deleteByTravelAgency(userEntity);

                userRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean updateUser(UpdateUserRequest request) {
        if (!requestAccessToken.getUserID().equals(request.getId())) {
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
