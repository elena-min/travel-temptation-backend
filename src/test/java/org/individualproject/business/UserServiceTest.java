package org.individualproject.business;

import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.CreateUserRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateUserRequest;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.*;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;


    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ExcursionRepository excursionRepository;

    @Mock
    private PaymentDetailsRepository paymentDetailsRepository;

    @Mock
    private AccessToken accessToken;

    @InjectMocks
    private UserService userService;
    @Test
    void getUsers_returnsAllUsersConverted() {
        //Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        List<User> expected =  Arrays.asList(
                new User(1L, "John", "Doe", date, "j.doe@example.com", "JohnDoe", "hashedPassword1", Gender.MALE ),
                new User(2L, "Eve", "McDonalds", date, "e.mcdonalds@example.com","EveMacd", "hashedPassword2", Gender.FEMALE),
                new User(3L, "Donald", "Duck", date, "d.duck@example.com", "Donalds","hashedPassword3",  Gender.MALE)
        );
        List<UserEntity> allUserEntities = Arrays.asList(
                UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("JohnDoe").hashedPassword("hashedPassword1").gender(Gender.MALE).build(),
                UserEntity.builder().id(2L).firstName("Eve").lastName("McDonalds").birthDate(date).email("e.mcdonalds@example.com").username("EveMacd").hashedPassword("hashedPassword2").gender(Gender.FEMALE).build(),
                UserEntity.builder().id(3L).firstName("Donald").lastName("Duck").birthDate(date).email("d.duck@example.com").username("Donalds").hashedPassword("hashedPassword3").gender(Gender.MALE).build()
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
        User expected = new User(1L, "John", "Doe", date, "j.doe@example.com", "JOhnDoe","hashedPassword1", Gender.MALE );

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("JOhnDoe").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
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
        Long id = 9923L; // Ensure the id matches everywhere
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class, () -> {
            // Act
            userService.getUser(id);
        });
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void getUserByUsername_shouldReturnUserConverted() {
        //Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        User expected = new User(1L, "John", "Doe", date, "j.doe@example.com", "JOhnDoe","hashedPassword1", Gender.MALE );

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("JOhnDoe").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        when(userRepository.findByUsername("JOhnDoe")).thenReturn(Optional.of(userEntity));
        Optional<User> expectedOptional = Optional.of(expected);

        // Act
        Optional<User> actual = userService.getUserByUsername("JOhnDoe");
        // Assert
        assertEquals(expectedOptional, actual);
        verify(userRepository, times(1)).findByUsername("JOhnDoe");
    }
    @Test
    void getUserByUsername_userNotFound() {
        //Arrange
        String username = "JOhnDoe";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<User> actual = userService.getUserByUsername(username);
        // Assert
        assertFalse(actual.isPresent());
        verify(userRepository, times(1)).findByUsername(username);
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
                "nickJonas",
                "passNIck",
                Gender.MALE
        );
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("Nick").lastName("JOnas").birthDate(date).email("nickJ@gmail.com").username("nickJonas").hashedPassword("hash").gender(Gender.MALE).build();
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
                "nickJonas",
                "123",
                Gender.MALE
        );

        UserEntity userEntity = UserEntity.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .birthDate(userRequest.getBirthDate())
                .email(userRequest.getEmail())
                .username(userRequest.getUsername())
                .hashedPassword(userRequest.getPassword())
                .gender(userRequest.getGender())
                .build();
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // Act
        User actual = userService.createUser(userRequest);

        verify(userRepository, times(1)).save(any());
        assertNull(actual);
    }

    @Test
    void deleteUser_existingUser() {
        Long id = 1L;
        LocalDate date = LocalDate.of(2014, 9, 16);
        CreateUserRequest userRequest = new CreateUserRequest(
                "Nick",
                "Jonas",
                date,
                "nickJonas",
                "nickJ@gmail.com",
                "passNIck",
                Gender.MALE
        );
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("Nick").lastName("JOnas").birthDate(date).username("nickJonas").email("nickJ@gmail.com").hashedPassword("hash").gender(Gender.MALE).build();
        when(accessToken.getUserID()).thenReturn(1L);

        // Mocking Repository Methods
        when(userRepository.findById(id)).thenReturn(Optional.of(userEntity));
        doNothing().when(reviewRepository).deleteByUserWriter(userEntity);
        doNothing().when(reviewRepository).deleteByTravelAgency(userEntity);
        doNothing().when(bookingRepository).deleteByUser(userEntity);
        doNothing().when(paymentDetailsRepository).deleteByUser(userEntity);
        doNothing().when(excursionRepository).deleteByTravelAgency(userEntity);
        doNothing().when(userRepository).deleteById(id); // This is already mocked

        // Act
        boolean result = userService.deleteUser(id);

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).deleteById(id);
        verify(reviewRepository, times(1)).deleteByUserWriter(userEntity);
        verify(reviewRepository, times(1)).deleteByTravelAgency(userEntity);
        verify(bookingRepository, times(1)).deleteByUser(userEntity);
        verify(paymentDetailsRepository, times(1)).deleteByUser(userEntity);
        verify(excursionRepository, times(1)).deleteByTravelAgency(userEntity);
    }
    @Test
    void deleteUser_nonExistingUser(){
        Long nonExistingUserId = 1L;
        when(accessToken.getUserID()).thenReturn(1L);

//        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(nonExistingUserId);

        // Act
        boolean result = userService.deleteUser(nonExistingUserId);

        // Assert
        assertFalse(result);
        verify(userRepository, times(0)).deleteById(nonExistingUserId);
    }

    @Test
    void deleteUser_unauthorizedAccessToken() {
        Long userId = 1L;
        Long differentUserId = 2L;

        when(accessToken.getUserID()).thenReturn(differentUserId);

        // Act & Assert
        UnauthorizedDataAccessException exception = assertThrows(UnauthorizedDataAccessException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals("USER_ID_NOT_FROM_LOGGED_IN_USER", exception.getReason());
        verify(userRepository, never()).deleteById(anyLong());
    }
    @Test
    void updateUser_existingUserWIthValidData() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1L, "JOe", "Smith", LocalDate.of(1990, 5, 15), Gender.MALE);
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("johnDoe").hashedPassword("hashedPassword1").gender(Gender.MALE).build();

        when(accessToken.getUserID()).thenReturn(1L);
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

        when(accessToken.getUserID()).thenReturn(1L);
        when(userRepository.findById(updateUserRequest.getId())).thenReturn(Optional.empty());

        boolean result = userService.updateUser(updateUserRequest);

        assertFalse(result);
        verify(userRepository, times(1)).findById(updateUserRequest.getId());
        verify(userRepository, never()).save(any(UserEntity.class));

    }

    @Test
    void updateUser_accessTokenIdDifferentFromRequestId() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(1L, "JOe", "Smith", LocalDate.of(1990, 5, 15), Gender.MALE);
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("johnDoe").hashedPassword("hashedPassword1").gender(Gender.MALE).build();

        when(accessToken.getUserID()).thenReturn(2L);
        assertThrows(UnauthorizedDataAccessException.class, () -> {
            userService.updateUser(updateUserRequest);
        });
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(UserEntity.class));

    }
}