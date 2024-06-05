package org.individualproject.domain;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExcursionRequest {
    private Long id;

    @NotBlank(message = "Excursion name cannot be blank")
    private String name;

    @NotEmpty(message = "Destinations list cannot be blank")
    private List<String> destinations;

    @NotNull(message = "Start date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @NotNull(message = "End date cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @Min(value = 0, message = "Price must be at least 0")
    private double price;

    @Min(value = 0, message = "Number of available spaces must be at least 0")
    private int numberOfAvaliableSpaces;

}
