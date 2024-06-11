package org.individualproject.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExcursionRequest {
    @NotBlank(message = "Excursion name cannot be blank")
    private String name;

    @NotEmpty(message = "Destinations list cannot be blank")
    private List<String> destinations;

    @NotBlank(message = "Description name cannot be blank")
    private String description;

    @NotNull(message = "Start date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "Start date must be in the future")
    private Date startDate;

    @NotNull(message = "End date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "End date must be in the future")
    private Date endDate;

    @NotNull(message = "Travel agency cannot be null")
    private User travelAgency;

    @Min(value = 0, message = "Price must be at least 0")
    private double price;

    @Min(value = 0, message = "Number of available spaces must be at least 0")
    private int numberOfAvaliableSpaces;

}
