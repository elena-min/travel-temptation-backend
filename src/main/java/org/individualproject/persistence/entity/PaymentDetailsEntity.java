package org.individualproject.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_details")
public class PaymentDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "cardNumber", length = 16)
    @NotNull
    private String cardNumber;

    @Column(name = "cvv", length = 3)
    @NotNull
    private String cvv;

    @Column(name = "expirationDate")
    @NotNull
    private YearMonth expirationDate;

    @Column(name = "cardHolderName")
    @NotNull
    private String cardHolderName;
}
