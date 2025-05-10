package org.individualproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Long id;
    private User travelAgency;
    private User userWriter;
    private Date reviewDate;
    private int numberOfStars;
    private String title;
    private String description;
}

