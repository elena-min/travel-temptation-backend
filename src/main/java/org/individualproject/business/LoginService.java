package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.exception.InvalidCredentialsException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.configuration.security.token.AccessTokenEncoder;
import org.individualproject.domain.LoginRequest;
import org.individualproject.domain.LoginRegisterResponse;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@AllArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenEncoder accessTokenEncoder;
    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public LoginRegisterResponse login(LoginRequest loginRequest){
        try {
            UserEntity userEntity = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(InvalidCredentialsException::new);

            if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.getHashedPassword())) {
                throw new InvalidCredentialsException();
            }

            String accessToken = generateAccessToken(userEntity);
            return LoginRegisterResponse.builder().accessToken(accessToken).build();

        } catch (InvalidCredentialsException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during login: {}", ex.getMessage(), ex);
            throw new RuntimeException("An unexpected error occurred during login. Please try again later.");
        }

    }

    private String generateAccessToken(UserEntity user){
        Long userID = user.getId();
        List<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().toString())
                .toList();

        return accessTokenEncoder.encode(new AccessToken(user.getUsername(), userID, roles));
    }
}
