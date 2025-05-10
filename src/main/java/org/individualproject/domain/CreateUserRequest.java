package org.individualproject.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.individualproject.domain.enums.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "First name cannot be null")
    private String firstName;

    @NotBlank(message = "Last name cannot be null")
    private String lastName;

    @NotNull(message = "Birth date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;


    @NotBlank(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username cannot be null")
    @Length(min = 2, max = 20, message = "Username length must be between 2 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be null")
    @Size(min = 6, message = "Password length must be at least 6 characters")
    private String password;

    @NotNull(message = "Gender cannot be null")
    private Gender gender;
}
