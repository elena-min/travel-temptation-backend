package org.individualproject.business;

import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.PaymentDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentDetailsServiceTest {
    @Mock
    private PaymentDetailsRepository paymentDetailsRepository;

    @InjectMocks
    private PaymentDetailsService paymentDetailsService;

    @Test
    void getAllPaymentDetails() {
    }

    @Test
    void getPaymentDetails() {
    }

    @Test
    void createPaymentDetails() {
    }

    @Test
    void deletePaymentDetails() {
    }

    @Test
    void updatePaymentDetails() {
    }
}