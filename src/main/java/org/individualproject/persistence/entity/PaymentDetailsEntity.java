package org.individualproject.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PaymentDetailsEntity {
    @Id
    private Long id;
}
