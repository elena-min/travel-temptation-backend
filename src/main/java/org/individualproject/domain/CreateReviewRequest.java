package org.individualproject.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.individualproject.domain.enums.BookingStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewRequest {

    @NotNull
    private User travelAgency;

    @NotNull
    private User userWriter;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date reviewDate;

    @Min(1)
    private int numberOfStars;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;
}

