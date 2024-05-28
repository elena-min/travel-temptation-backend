package org.individualproject.business;

import org.individualproject.domain.Booking;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.BookingStatus;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.ReviewRepository;
import org.individualproject.persistence.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class TrendingServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ExcursionRepository excursionRepository;

    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private TrendingService trendingService;

    @Test
    void getTrendingExcursion() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        //LocalDateTime bookingDateTime = LocalDateTime.of(2024, Month.MAY, 19, 12, 0);
        Date startDate = new Date(2024, 6, 16);
        Date endDate = new Date(2024, 6, 24);
        //UserEntity user = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("JohnyDOe").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        UserEntity travelAgency = UserEntity.builder().id(1L).firstName("Travel").lastName("Global").birthDate(date).email("travel@example.com").username("traveling").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();
        List<ExcursionEntity> excursionEntities = Arrays.asList(
                new ExcursionEntity(1L, "Excursion 1", "Paris, Bora Bora", startDate, endDate, travelAgency, 100.0, 50, 50),
                new ExcursionEntity(2L, "Excursion 2", "London, Amsterdam", startDate, endDate, travelAgency, 200.0, 40, 40)
        );
        when(excursionRepository.findAll()).thenReturn(excursionEntities);



//        List<ReviewEntity> reviewEntities = Arrays.asList(
//                new ReviewEntity(2L,user, travelAgency, new Date(), 5, "title1", "Description1"),
//                new ReviewEntity(1L,user, travelAgency, new Date(), 5, "title2", "Description2")
//        );
//
//        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
//                .id(1L)
//                .user(user)
//                .cvv("234")
//                .cardHolderName("Nick Jonas")
//                .cardNumber("1234567890123456")
//                .expirationDate(date)
//                .build();
//
//        List<BookingEntity> bookingEntities = Arrays.asList(
//                new BookingEntity(1L, user, excursionEntities.get(0), bookingDateTime, BookingStatus.PENDING, paymentDetailsEntity, 4),
//                new BookingEntity(2L, user, excursionEntities.get(0), bookingDateTime, BookingStatus.PENDING, paymentDetailsEntity, 10)
//        );

        List<Excursion> trendingExcursions = trendingService.getTrendingExcursion(5);
        verify(excursionRepository, times(1)).findAll();
        assertNotNull(trendingExcursions);

       // assertEquals(2, trendingExcursions.size());
    }
}