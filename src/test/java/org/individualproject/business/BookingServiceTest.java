package org.individualproject.business;

import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.ExcursionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;
    @Test
    void getBookings() {
    }

    @Test
    void getBooking() {
    }

    @Test
    void createBooking() {
    }

    @Test
    void deleteBooking() {
    }

    @Test
    void updateBooking() {
    }
}