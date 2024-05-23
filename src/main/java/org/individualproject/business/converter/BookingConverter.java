package org.individualproject.business.converter;

import org.individualproject.domain.Booking;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.BookingEntity;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;

import java.util.List;

public class BookingConverter {

    public static Booking mapToDomain(BookingEntity bookingEntity) {
        User user = UserConverter.mapToDomain(bookingEntity.getUser());
        Excursion excursion = ExcursionConverter.mapToDomain(bookingEntity.getExcursion());
        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(bookingEntity.getBankingDetails());

        return Booking.builder()
                .id(bookingEntity.getId())
                .user(user)
                .excursion(excursion)
                .bookingTime(bookingEntity.getBookingTime())
                .status(bookingEntity.getStatus())
                .bankingDetails(paymentDetails)
                .numberOfTravelers(bookingEntity.getNumberOfTravelers())
                .build();
    }
    public static List<Booking> mapToDomainList(List<BookingEntity> bookingEntities) {
        return bookingEntities.stream()
                .map(BookingConverter::mapToDomain)
                .toList();
    }

    public static BookingEntity convertToEntity(Booking booking){
        UserEntity userEntity = UserConverter.convertToEntity(booking.getUser());
        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsConverter.convertToEntity(booking.getBankingDetails());
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(booking.getExcursion());
        return BookingEntity.builder()
                .id(booking.getId())
                .user(userEntity)
                .bankingDetails(paymentDetailsEntity)
                .bookingTime(booking.getBookingTime())
                .excursion(excursionEntity)
                .numberOfTravelers(booking.getNumberOfTravelers())
                .status(booking.getStatus())
                .build();
    }

    private BookingConverter(){}
}
