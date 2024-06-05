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

    @NotBlank(message = "Card number cannot be blank")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotBlank(message = "Cvv cannot be blank")
    @Size(min = 3, message = "CVV must be between 3 and 4 digits")
    private String cvv;

    @NotNull(message = "Expiration date cannot be null")
    private LocalDate expirationDate;

    @NotBlank(message = "Card Holder cannot be blank")
    private String cardHolderName;
}
