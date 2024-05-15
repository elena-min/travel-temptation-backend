package org.individualproject.business.converter;

import org.individualproject.domain.Booking;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.BookingStatus;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.entity.BookingEntity;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingConverterTest {

    @Test
    void mapToDomain() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        BookingEntity bookingEntity = mock(BookingEntity.class);
        when(bookingEntity.getBookingTime()).thenReturn(LocalDateTime.now());
        when(bookingEntity.getStatus()).thenReturn(BookingStatus.PENDING);
        when(bookingEntity.getNumberOfTravelers()).thenReturn(4);
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .hashedPassword("asdfgh")
                .salt("asdfghjkl")
                .gender(Gender.Male)
                .build();

        User user = UserConverter.mapToDomain(userEntity);
        when(bookingEntity.getUser()).thenReturn(userEntity);

        ExcursionEntity excursionEntity = ExcursionEntity.builder()
                .id(1L)
                .name("City Tour")
                .destinations("Rome.Florance")
                .startDate(new Date(124, 4, 16))
                .endDate(new Date(124, 4, 26))
                .travelAgency("Agency 1")
                .price(100.00)
                .numberOfAvaliableSpaces(30)
                .build();
        Excursion excursion = ExcursionConverter.mapToDomain(excursionEntity);
        when(bookingEntity.getExcursion()).thenReturn(excursionEntity);

        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                .user(userEntity)
                .cvv("123")
                .cardHolderName("Nick Jonas")
                .cardNumber("2345")
                .expirationDate(LocalDate.EPOCH)
                .build();
        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(paymentDetailsEntity);
        when(bookingEntity.getBankingDetails()).thenReturn(paymentDetailsEntity);

        //Act
        Booking booking = BookingConverter.mapToDomain(bookingEntity);

        //Assert
        assertEquals(user, booking.getUser());
        assertEquals(excursion, booking.getExcursion());
        assertEquals(paymentDetails, booking.getBankingDetails());
        assertEquals(bookingEntity.getBookingTime(), booking.getBookingTime());
        assertEquals(bookingEntity.getStatus(), booking.getStatus());
        assertEquals(bookingEntity.getId(), booking.getId());
        assertEquals(bookingEntity.getNumberOfTravelers(), booking.getNumberOfTravelers());

    }

    @Test
    void mapToDomainList() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        List<BookingEntity> bookingEntityList = new ArrayList<>();
        BookingEntity bookingEntity = mock(BookingEntity.class);
        when(bookingEntity.getBookingTime()).thenReturn(LocalDateTime.now());
        when(bookingEntity.getStatus()).thenReturn(BookingStatus.PENDING);
        when(bookingEntity.getNumberOfTravelers()).thenReturn(4);

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .hashedPassword("asdfgh")
                .salt("asdfghjkl")
                .gender(Gender.Male)
                .build();

        User user = UserConverter.mapToDomain(userEntity);
        when(bookingEntity.getUser()).thenReturn(userEntity);

        ExcursionEntity excursionEntity = ExcursionEntity.builder()
                .id(1L)
                .name("City Tour")
                .destinations("Rome.Florance")
                .startDate(new Date(124, 4, 16))
                .endDate(new Date(124, 4, 26))
                .travelAgency("Agency 1")
                .price(100.00)
                .numberOfAvaliableSpaces(30)
                .build();
        Excursion excursion = ExcursionConverter.mapToDomain(excursionEntity);
        when(bookingEntity.getExcursion()).thenReturn(excursionEntity);

        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                .user(userEntity)
                .cvv("123")
                .cardHolderName("Nick Jonas")
                .cardNumber("2345")
                .expirationDate(LocalDate.EPOCH)
                .build();
        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(paymentDetailsEntity);
        when(bookingEntity.getBankingDetails()).thenReturn(paymentDetailsEntity);

        bookingEntityList.add(bookingEntity);

        //Act
        List<Booking> bookings = BookingConverter.mapToDomainList(bookingEntityList);

        //Assert
        assertEquals(1, bookings.size());
        Booking booking = bookings.get(0);
        assertEquals(bookingEntity.getId(), booking.getId());
        assertEquals(excursion, booking.getExcursion());
        assertEquals(user, booking.getUser());
        assertEquals(paymentDetails, booking.getBankingDetails());
        assertEquals(bookingEntity.getBookingTime(), booking.getBookingTime());
        assertEquals(bookingEntity.getNumberOfTravelers(), booking.getNumberOfTravelers());
        assertEquals(bookingEntity.getStatus(), booking.getStatus());

    }
}