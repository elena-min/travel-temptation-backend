package org.individualproject.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {

    @NotNull(message = "Travel agency cannot be null")
    private User travelAgency;

    @NotNull(message = "User writer cannot be null")
    private User userWriter;

    @NotNull(message = "Review date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date reviewDate;

    @Min(value = 1, message = "Number of stars must be at least 1")
    private int numberOfStars;

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @NotEmpty(message = "Description cannot be empty")
    private String description;
}

