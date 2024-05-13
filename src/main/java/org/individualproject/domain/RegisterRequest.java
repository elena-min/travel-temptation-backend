package org.individualproject.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.individualproject.domain.enums.Gender;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Length(min = 2, max = 50)
    private String firstName;

    @NotBlank
    @Length(min = 2, max = 50)
    private String lastName;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 2, max = 20)
    private String username;

    @NotBlank
    @Length(min = 6)
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}

