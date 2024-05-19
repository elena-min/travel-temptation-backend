package org.individualproject.business;

import org.individualproject.business.exception.InvalidCredentialsException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.configuration.security.token.impl.AccessTokenEncoderDecoderImpl;
import org.individualproject.domain.LoginRegisterResponse;
import org.individualproject.domain.LoginRequest;
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
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AccessTokenEncoderDecoderImpl accessTokenEncoderDecoder;
    @InjectMocks
    private LoginService loginService;
    @Test
    void login_withValidCredentials_shouldReturnAccessToken() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        LoginRequest loginRequest = new LoginRequest("username", "password");
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword").gender(Gender.MALE).userRoles(new HashSet<>()).build();

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(userEntity);
        when(passwordEncoder.matches(loginRequest.getPassword(), userEntity.getHashedPassword())).thenReturn(true);
        when(accessTokenEncoderDecoder.encode(any(AccessToken.class))).thenReturn("encodedAccessToken");

        LoginRegisterResponse response = loginService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertEquals("encodedAccessToken", response.getAccessToken());

        // Verify interactions
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), userEntity.getHashedPassword());
        verify(accessTokenEncoderDecoder, times(1)).encode(any(AccessToken.class));

    }

    @Test
    void login_withInvalidCredentials_shouldThrowInvalidCredentialsException() {

        LoginRequest loginRequest = new LoginRequest("invalidusername", "invalidpassword");

        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> loginService.login(loginRequest));


        // Verify interactions
        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(accessTokenEncoderDecoder, never()).encode(any(AccessToken.class));

    }
}