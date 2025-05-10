package org.individualproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyStatisticsDTO {

    private int year;
    private int week;
    private long numberOfBookings;
    private double totalRevenue;
}
