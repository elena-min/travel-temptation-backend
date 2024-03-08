package org.individualproject.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private String title;
    private String description;
    private Excursion excursion;
    private Date dateOfPosting;
    private String travelAgency;
}
