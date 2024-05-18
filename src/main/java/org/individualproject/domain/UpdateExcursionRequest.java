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

    @NotBlank
    private String name;

    @NotEmpty
    private List<String> destinations;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @NotBlank
    private User travelAgency;

    @Min(0)
    private double price;

    @Min(0)
    private int numberOfAvaliableSpaces;

}
