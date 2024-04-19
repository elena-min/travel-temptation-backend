package org.individualproject.domain;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Excursion {
    private Long id;
    private String name;
    private List<String> destinations;
    private Date startDate;
    private Date endDate;
    private String travelAgency;
    private double price;
    private int numberOfAvaliableSpaces;

}
