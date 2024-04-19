package org.individualproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.individualproject.domain.enums.Gender;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Length(min = 2, max = 50)
    @Column(name = "firstName")
    private String firstName;

    @NotBlank
    @Length(min = 2, max = 50)
    @Column(name = "lastName")
    private String lastName;

    @Temporal(TemporalType.DATE)
    @Column(name = "birthDate")
    private LocalDate birthDate;

    @NotBlank
    @Column(name = "email")
    private String email;

    @NotBlank
    @Length(min = 6)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Column(name = "hashedPassword")
    private String hashedPassword;

    @NotBlank
    @Column(name = "salt")
    private String salt;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;
}
