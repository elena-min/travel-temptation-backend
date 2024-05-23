package org.individualproject.business;

import org.individualproject.business.exception.UsernameAlreadyExistsException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.configuration.security.token.impl.AccessTokenEncoderDecoderImpl;
import org.individualproject.domain.LoginRegisterResponse;
import org.individualproject.domain.LoginRequest;
import org.individualproject.domain.RegisterRequest;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccessTokenEncoderDecoderImpl accessTokenEncoderDecoder;
    @InjectMocks
    private RegisterService registerService;
    @Test
    void registerUser_ValidData_ShouldReturnAccessToken() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        RegisterRequest registerRequest = new RegisterRequest("Nick", "Jonas", date, "nickJonas@gmail.com", "nickJonas23", "nick123",Gender.MALE);

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(encodedPassword);

        UserEntity savedUserEntity = mock(UserEntity.class);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        String accessToken = "generatedAccessToken";
        when(accessTokenEncoderDecoder.encode(any(AccessToken.class))).thenReturn(accessToken);

        LoginRegisterResponse response = registerService.registerUser(registerRequest);

        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());

        // Verify interactions
    }

    @Test
    void registerUser_ExistingUsername_ShouldThrowUsernameAlreadyExistsException() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        RegisterRequest registerRequest = new RegisterRequest("Nick", "Jonas", date, "nickJonas@gmail.com", "nickJonas23", "nick123",Gender.MALE);

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> registerService.registerUser(registerRequest));
    }

    @Test
    void registerTravelingAgency_ValidData_ShouldReturnAccessToken() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        RegisterRequest registerRequest = new RegisterRequest("Global", "Travels", date, "globalTravels@gmail.com", "global123", "global123",Gender.OTHER);

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(encodedPassword);

        UserEntity savedUserEntity = mock(UserEntity.class);
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        String accessToken = "generatedAccessToken";
        when(accessTokenEncoderDecoder.encode(any(AccessToken.class))).thenReturn(accessToken);

        LoginRegisterResponse response = registerService.registerTravelingAgency(registerRequest);

        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
    }

    @Test
    void registerTravelingAgency_ExistingUsername_ShouldThrowUsernameAlreadyExistsException() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        RegisterRequest registerRequest = new RegisterRequest("Nick", "Jonas", date, "nickJonas@gmail.com", "nickJonas23", "nick123",Gender.MALE);

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> registerService.registerTravelingAgency(registerRequest));
    }
}