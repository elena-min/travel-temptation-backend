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
    @NotBlank
    private String name;

    @NotEmpty
    private List<String> destinations;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future
    private Date startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future
    private Date endDate;

    @NotBlank
    private String travelAgency;

    @Min(0)
    private double price;
}
