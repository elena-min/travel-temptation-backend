package org.individualproject.business.converter;

import org.individualproject.domain.Booking;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.BookingEntity;
import java.util.List;

public class BookingConverter {

    public static Booking mapToDomain(BookingEntity bookingEntity) {
        User user = UserConverter.mapToDomain(bookingEntity.getUser());
        Excursion excursion = ExcursionConverter.mapToDomain(bookingEntity.getExcursion());
        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(bookingEntity.getBankingDetails());

        Booking booking = Booking.builder()
                .id(bookingEntity.getId())
                .user(user)
                .excursion(excursion)
                .bookingTime(bookingEntity.getBookingTime())
                .status(bookingEntity.getStatus())
                .bankingDetails(paymentDetails)
                .numberOfTravelers(bookingEntity.getNumberOfTravelers())
                .build();
        return booking;
    }
    public static List<Booking> mapToDomainList(List<BookingEntity> bookingEntities) {
        return bookingEntities.stream()
                .map(BookingConverter::mapToDomain)
                .toList();
    }

    private BookingConverter(){}
}
