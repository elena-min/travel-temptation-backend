package org.individualproject.business;

import org.individualproject.business.converter.BookingConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.domain.*;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
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
//    private BookingRepository bookingRepository;
//    private UserRepository userRepository;
//    @Autowired
//    public BookingService(BookingRepository bRepository, UserRepository uRepository){
//        this.bookingRepository = bRepository;
//        this.userRepository = uRepository;
//    }
//    public List<Booking> getBookings() {
//        List<BookingEntity> bookingEntities = bookingRepository.findAll();
//        return BookingConverter.mapToDomainList(bookingEntities);
//    }
//    public Optional<Booking> getBooking(Long id) {
//        Optional<BookingEntity> bookingEntity = bookingRepository.findById(id);
//        return bookingEntity.map(BookingConverter::mapToDomain);
//    }

//    public Booking createBooking(CreateBookingRequest createBookingRequest){
//        User user = createBookingRequest.getUser();
//        UserEntity userEntity = userRepository.findById(user.getId());
//
//        BookingEntity newBooking = BookingEntity.builder()
//                .user(createBookingRequest.getUser())
//                .excursion(createBookingRequest.getExcursion())
//                .bookingTime(createBookingRequest.getBookingTime())
//                .status(createBookingRequest.getStatus())
//                .bankingDetails(createBookingRequest.getBankingDetails())
//                .numberOfTravelers(createBookingRequest.getNumberOfTravelers())
//                .build();
//
//        BookingEntity excursionEntity = bookingRepository.save(newBooking);
//        return BookingConverter.mapToDomain(excursionEntity);
//    }
//
//    public boolean deleteBooking(Long id) {
//        try {
//            bookingRepository.deleteById(id);
//            return true;
//        } catch (EmptyResultDataAccessException e) {
//            return false;
//        }
//    }

//    public boolean updateBooking(UpdateBookingRequest updateBookingRequest) {
//        Optional<BookingEntity> optionalBooking = bookingRepository.findById(updateBookingRequest.getId());
//        if (optionalBooking.isPresent()) {
//            BookingEntity existingBooking = optionalBooking.get();
//            existingBooking.setExcursion(updateBookingRequest.getExcursion());
//            existingBooking.setBookingTime(updateBookingRequest.getBookingTime());
//            existingBooking.setStatus(updateBookingRequest.getStatus());
//            existingBooking.setBankingDetails(updateBookingRequest.getBankingDetails());
//            existingBooking.setNumberOfTravelers(updateBookingRequest.getNumberOfTravelers());
//            bookingRepository.save(existingBooking);
//            return true;
//        } else {
//            return false;
//        }
//    }

}
