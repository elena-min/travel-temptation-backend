package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.exception.InvalidCredentialsException;
import org.individualproject.business.exception.UsernameAlreadyExistsException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.configuration.security.token.AccessTokenEncoder;
import org.individualproject.domain.LoginRegisterResponse;
import org.individualproject.domain.RegisterRequest;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.UserEntity;
import org.individualproject.persistence.entity.UserRoleEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenEncoder accessTokenEncoder;

    public LoginRegisterResponse registerUser(RegisterRequest registerRequest){

        if(userRepository.existsByUsername(registerRequest.getUsername())){
            throw new UsernameAlreadyExistsException();
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        UserEntity userEntity = UserEntity.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .hashedPassword(hashedPassword)
                .gender(registerRequest.getGender())
                .birthDate(registerRequest.getBirthDate())
                .build();

        UserRoleEntity userRoleEntity = UserRoleEntity.builder()
                .role(UserRole.USER)
                .user(userEntity)
                .build();
        userEntity.getUserRoles().add(userRoleEntity);

        UserEntity savedUserEntity = userRepository.save(userEntity);
        String accessToken = generateAccessToken(savedUserEntity);
        return LoginRegisterResponse.builder().accessToken(accessToken).build();

    }

    public LoginRegisterResponse registerTravelingAgency(RegisterRequest registerRequest){

        if(userRepository.existsByUsername(registerRequest.getUsername())){
            throw new UsernameAlreadyExistsException();
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        UserEntity userEntity = UserEntity.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .hashedPassword(hashedPassword)
                .gender(registerRequest.getGender())
                .birthDate(registerRequest.getBirthDate())
                .build();

        UserRoleEntity userRoleEntity = UserRoleEntity.builder()
                .role(UserRole.TRAVELINGAGENCY)
                .user(userEntity)
                .build();
        userEntity.getUserRoles().add(userRoleEntity);

        UserEntity savedUserEntity = userRepository.save(userEntity);
        String accessToken = generateAccessToken(savedUserEntity);
        return LoginRegisterResponse.builder().accessToken(accessToken).build();

    }

    private String generateAccessToken(UserEntity user){
        Long userID = user.getId();
        List<String> roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().toString())
                .toList();

        return accessTokenEncoder.encode(new AccessToken(user.getUsername(), userID, roles));
    }
}
