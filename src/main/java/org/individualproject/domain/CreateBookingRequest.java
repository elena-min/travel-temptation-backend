package org.individualproject.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.individualproject.domain.enums.BookingStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {
    @NotNull(message = "User cannot be null")
    private User user;

    @NotNull(message = "Excursion cannot be null")
    private  Excursion excursion;

    @NotNull(message = "Booking time cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime bookingTime;

    @NotNull(message = "Status cannot be null")
    private BookingStatus status;

    @NotNull(message = "Payment details cannot be null")
    private PaymentDetails bankingDetails;

    @Min(value = 0, message = "Number of travelers must be at least 0")
    private int numberOfTravelers;
}
