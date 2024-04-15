package org.individualproject.business;

import org.individualproject.business.converter.BookingConverter;
import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.domain.*;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.BookingEntity;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    @Autowired
    public BookingService(BookingRepository bRepository, UserRepository uRepository){
        this.bookingRepository = bRepository;
        this.userRepository = uRepository;
    }
    public List<Booking> getBookings() {
        List<BookingEntity> bookingEntities = bookingRepository.findAll();
        return BookingConverter.mapToDomainList(bookingEntities);
    }
    public Optional<Booking> getBooking(Long id) {
        Optional<BookingEntity> bookingEntity = bookingRepository.findById(id);
        return bookingEntity.map(BookingConverter::mapToDomain);
    }

    public Booking createBooking(CreateBookingRequest createBookingRequest){
        UserEntity userEntity = UserConverter.convertToEntity(createBookingRequest.getUser());
        ExcursionEntity excursion = ExcursionConverter.convertToEntity(createBookingRequest.getExcursion());
        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                .id(1L)
                .build();
        BookingEntity bookingEntity = BookingEntity.builder()
                .user(userEntity)
                .excursion(excursion)
                .bookingTime(createBookingRequest.getBookingTime())
                .status(createBookingRequest.getStatus())
                .bankingDetails(paymentDetailsEntity)
                .numberOfTravelers(createBookingRequest.getNumberOfTravelers())
                .build();

        BookingEntity excursionEntity = bookingRepository.save(bookingEntity);
        return BookingConverter.mapToDomain(excursionEntity);
    }

    public boolean deleteBooking(Long id) {
        try {
            bookingRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean updateBooking(UpdateBookingRequest updateBookingRequest) {
        UserEntity userEntity = UserConverter.convertToEntity(updateBookingRequest.getUser());
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(updateBookingRequest.getExcursion());
        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                .id(1L)
                .build();

        Optional<BookingEntity> optionalBooking = bookingRepository.findById(updateBookingRequest.getId());
        if (optionalBooking.isPresent()) {
            BookingEntity existingBooking = optionalBooking.get();
            existingBooking.setExcursion(excursionEntity);
            existingBooking.setBookingTime(updateBookingRequest.getBookingTime());
            existingBooking.setStatus(updateBookingRequest.getStatus());
            existingBooking.setBankingDetails(paymentDetailsEntity);
            existingBooking.setNumberOfTravelers(updateBookingRequest.getNumberOfTravelers());
            bookingRepository.save(existingBooking);
            return true;
        } else {
            return false;
        }
    }

}
