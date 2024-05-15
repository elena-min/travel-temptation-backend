package org.individualproject.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.BookingStatus;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "excursion_id")
    private ExcursionEntity excursion;

    @Column(name = "bookingTime")
    private LocalDateTime bookingTime;

    @Column(name = "status")
    private BookingStatus status;

    @ManyToOne
    @JoinColumn(name = "bankingDetails_id")
    private PaymentDetailsEntity bankingDetails;

    @Column(name = "numberOfTravelers")
    private int numberOfTravelers;
}
