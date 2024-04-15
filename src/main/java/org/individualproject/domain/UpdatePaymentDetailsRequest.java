package org.individualproject.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePaymentDetailsRequest {
    private Long id;

    private User user;

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank
    @Size(min = 3, message = "CVV must be between 3 and 4 digits")
    private String cvv;

    @NotNull
    private LocalDate expirationDate;

    @NotBlank
    private String cardHolderName;
}
