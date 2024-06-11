package org.individualproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetails {
    private Long id;
    private User user;
    private String cardNumber;
    private String cvv;
    private YearMonth expirationDate;
    private String cardHolderName;
}
