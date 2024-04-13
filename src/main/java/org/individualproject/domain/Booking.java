package org.individualproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.individualproject.domain.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    private Long id;
    private User user;
    private  Excursion excursion;
    private LocalDateTime bookingTime;
    private BookingStatus status;
    private PaymentDetails bankingDetails;
    private int numberOfTravelers;
}
