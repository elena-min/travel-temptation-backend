package org.individualproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String email;
    private String password;
    private Gender gender;
}
