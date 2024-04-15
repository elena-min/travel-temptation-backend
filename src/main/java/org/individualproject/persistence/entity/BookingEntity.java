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

//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//@Table(name = "booking")
//public class BookingEntity {

//    @Id
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private UserEntity user;
//
//    @ManyToOne
//    @Column(name = "excursion_id")
//    private ExcursionEntity excursion;
//
//    @Column(name = "bookingTime")
//    private LocalDateTime bookingTime;
//
//    @Column(name = "status")
//    private BookingStatus status;
//
//    @ManyToOne
//    @Column(name = "bankingDetails_id")
//    private PaymentDetailsEntity bankingDetails;
//
//    @Column(name = "numberOfTravelers")
//    private int numberOfTravelers;
//}
