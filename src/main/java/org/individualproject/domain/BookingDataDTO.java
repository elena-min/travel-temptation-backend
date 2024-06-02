package org.individualproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDataDTO {

    private LocalDateTime date;
    private long numberOfBookings;
    private double totalRevenue;
}
