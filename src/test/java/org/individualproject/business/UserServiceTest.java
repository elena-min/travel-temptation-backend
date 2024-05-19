package org.individualproject.business;

import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.domain.CreateUserRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateUserRequest;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.*;

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
                new User(1L, "John", "Doe", date, "j.doe@example.com", "hashedPassword1", Gender.MALE ),
                new User(2L, "Eve", "McDonalds", date, "e.mcdonalds@example.com", "hashedPassword2", Gender.FEMALE),
                new User(3L, "Donald", "Duck", date, "d.duck@example.com", "hashedPassword3",  Gender.MALE)
        );
        List<UserEntity> allUserEntities = Arrays.asList(
                UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build(),
                UserEntity.builder().id(2L).firstName("Eve").lastName("McDonalds").birthDate(date).email("e.mcdonalds@example.com").hashedPassword("hashedPassword2").gender(Gender.FEMALE).build(),
                UserEntity.builder().id(3L).firstName("Donald").lastName("Duck").birthDate(date).email("d.duck@example.com").hashedPassword("hashedPassword3").gender(Gender.MALE).build()
        );
        when(userRepository.findAll()).thenReturn(allUserEntities);
        // Act
        List<User> actual = userService.getUsers();
        // Assert
        assertArrayEquals(expected.toArray(), actual.toArray());
        verify(userRepository, times(1)).findAll();
    }
    @Test
    void getUsers_returnsEmptyListWithNoUsers() {
        //Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        List<User> expected =  Collections.emptyList();
        List<UserEntity> allUserEntities = Collections.emptyList();

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
        User expected = new User(1L, "John", "Doe", date, "j.doe@example.com", "hashedPassword1", Gender.MALE );

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        Optional<User> expectedOptional = Optional.of(expected);

        // Act
        Optional<User> actual = userService.getUser(1L);
        // Assert
        assertEquals(expectedOptional, actual);
        verify(userRepository, times(1)).findById(1L);
    }
    @Test
    void getUser_userNotFound() {
        //Arrange
        Long id = 9923L;
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<User> actual = userService.getUser(1L);
        // Assert
        assertFalse(actual.isPresent());
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
                Gender.MALE
        );
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("Nick").lastName("JOnas").birthDate(date).email("nickJ@gmail.com").hashedPassword("hash").gender(Gender.MALE).build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        User actual = userService.createUser(userRequest);

        //Assert
        verify(userRepository, times(1)).save(any());
    }
    @Test
    void createUser_invalidData() {
        // Act
        LocalDate date = LocalDate.of(2014, 9, 16);
        CreateUserRequest userRequest = new CreateUserRequest(
                "joe",
                "Jonas",
                date,
                "nickJ@gmail.com",
                "123",
                Gender.MALE
        );
        when(userRepository.save(any(UserEntity.class))).thenReturn(null);

        // Act
        User actual = userService.createUser(userRequest);

        verify(userRepository, times(1)).save(any());
        assertNull(actual);
    }

    @Test
    void deleteUser_existingUser() {
//        Long id = 1L;
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        CreateUserRequest userRequest = new CreateUserRequest(
//                "Nick",
//                "Jonas",
//                date,
//                "nickJ@gmail.com",
//                "passNIck",
//                Gender.MALE
//        );
//        UserEntity userEntity = UserEntity.builder().id(1L).firstName("Nick").lastName("JOnas").birthDate(date).email("nickJ@gmail.com").hashedPassword("hash").gender(Gender.MALE).build();
//        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
//
//        User actual = userService.createUser(userRequest);
//
//        Mockito.doNothing().when(userRepository).deleteById(id);
//
//        boolean result = userService.deleteUser(id);
//
//        //Assert
//        assertTrue(result);
//        verify(userRepository, times(1)).deleteById(id);
        Long userId = 1L;
        Mockito.doNothing().when(userRepository).deleteById(userId);

        // Act
        boolean result = userService.deleteUser(userId);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).deleteById(userId);

    }
    @Test
    void deleteUser_nonExistingUser(){
        Long nonExistingUserId = 9987L;
        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(nonExistingUserId);

        // Act
        boolean result = userService.deleteUser(nonExistingUserId);

        // Assert
        assertFalse(result);
        verify(userRepository, times(1)).deleteById(nonExistingUserId);
    }

    @Test
    void updateUser_existingUserWIthValidData() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1L, "JOe", "Smith", LocalDate.of(1990, 5, 15), Gender.MALE);
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();

        when(userRepository.findById(updateUserRequest.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        boolean result = userService.updateUser(updateUserRequest);

        assertTrue(result);
        verify(userRepository, times(1)).findById(updateUserRequest.getId());
        verify(userRepository, times(1)).save(userEntity);
        assertEquals(updateUserRequest.getFirstName(), userEntity.getFirstName());
        assertEquals(updateUserRequest.getLastName(), userEntity.getLastName());
        assertEquals(updateUserRequest.getBirthDate(), userEntity.getBirthDate());
        assertEquals(updateUserRequest.getGender(), userEntity.getGender());

    }
    @Test
    void updateUser_nonExistingUser() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1L, "JOe", "Smith", LocalDate.of(1990, 5, 15), Gender.MALE);

        when(userRepository.findById(updateUserRequest.getId())).thenReturn(Optional.empty());

        boolean result = userService.updateUser(updateUserRequest);

        assertFalse(result);
        verify(userRepository, times(1)).findById(updateUserRequest.getId());
        verify(userRepository, never()).save(any(UserEntity.class));

    }
}