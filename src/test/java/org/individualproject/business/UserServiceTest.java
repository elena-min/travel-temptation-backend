package org.individualproject.business;

import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.domain.CreateUserRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    @Test
    void getUsers_returnsAllUsersConverted() {
        //Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        List<User> expected =  Arrays.asList(
                new User(1L, "John", "Doe", date, "j.doe@example.com", "password", "hashedPassword1", "salt1", Gender.Male ),
                new User(2L, "Eve", "McDonalds", date, "e.mcdonalds@example.com", "password", "hashedPassword2", "salt2", Gender.Female),
                new User(3L, "Donald", "Duck", date, "d.duck@example.com", "password", "hashedPassword3", "salt3", Gender.Male)
        );
        List<UserEntity> allUserEntities = Arrays.asList(
                UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").password("password").hashedPassword("hashedPassword1").salt("salt1").gender(Gender.Male).build(),
                UserEntity.builder().id(2L).firstName("Eve").lastName("McDonalds").birthDate(date).email("e.mcdonalds@example.com").password("password").hashedPassword("hashedPassword2").salt("salt2").gender(Gender.Female).build(),
                UserEntity.builder().id(3L).firstName("Donald").lastName("Duck").birthDate(date).email("d.duck@example.com").password("password").hashedPassword("hashedPassword3").salt("salt3").gender(Gender.Male).build()
        );
        when(userRepository.findAll()).thenReturn(allUserEntities);
        // Act
        List<User> actual = userService.getUsers();
        // Assert
        assertEquals(expected, actual);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUser_shouldReturnUserConverted() {
        //Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        User expected = new User(1L, "John", "Doe", date, "j.doe@example.com", "password", "hashedPassword1", "salt1", Gender.Male );

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").password("password").hashedPassword("hashedPassword1").salt("salt1").gender(Gender.Male).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Optional<User> expectedOptional = Optional.of(expected);

        // Act
        Optional<User> actual = userService.getUser(1L);
        // Assert
        assertEquals(expectedOptional, actual);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void createUser_shouldCreateUser() {
        // Act
        LocalDate date = LocalDate.of(2014, 9, 16);
        CreateUserRequest userRequest = new CreateUserRequest(
                "Nick",
                "Jonas",
                date,
                "nickJ@gmail.com",
                "passNIck",
                "hash",
                "salt",
                Gender.Male
        );
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("Nick").lastName("JOnas").birthDate(date).email("nickJ@gmail.com").password("passNick").hashedPassword("hash").salt("salt").gender(Gender.Male).build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        User actual = userService.createUser(userRequest);

        //Assert
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deleteUser() {
    }

    @Test
    void updateUser() {
    }
}