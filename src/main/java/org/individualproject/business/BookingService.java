package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.converter.BookingConverter;
import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.PaymentDetailsConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.InvalidExcursionDataException;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.BookingStatus;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.entity.BookingEntity;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingService {
    private static final String UNAUTHORIZED_ACCESS = "UNAUTHORIZED_ACCESS";

    private BookingRepository bookingRepository;
    private ExcursionRepository excursionRepository;
    private AccessToken requestAccessToken;

    public List<Booking> getBookings() {

        List<BookingEntity> bookingEntities = bookingRepository.findAll();
        return BookingConverter.mapToDomainList(bookingEntities);
    }

    public Optional<Booking> getBooking(Long id) {

        Optional<BookingEntity> bookingEntityOptional = bookingRepository.findById(id);
        if (bookingEntityOptional.isPresent()) {
            BookingEntity bookingEntity = bookingEntityOptional.get();

            if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name()) &&
                    !requestAccessToken.getUserID().equals(bookingEntity.getUser().getId())) {
                throw new UnauthorizedDataAccessException(UNAUTHORIZED_ACCESS);
            }
            return Optional.of(BookingConverter.mapToDomain(bookingEntity));
        } else {
            return Optional.empty();
        }
    }

    public Booking createBooking(CreateBookingRequest createBookingRequest) {
        if (createBookingRequest.getNumberOfTravelers() < 0 || createBookingRequest.getBookingTime() == null || createBookingRequest.getBankingDetails() == null ||
                createBookingRequest.getStatus() == null || createBookingRequest.getUser() == null || createBookingRequest.getExcursion() == null) {
            throw new InvalidExcursionDataException("Invalid input data");
        }
        UserEntity userEntity = UserConverter.convertToEntity(createBookingRequest.getUser());
        ExcursionEntity excursion = ExcursionConverter.convertToEntity(createBookingRequest.getExcursion());
        PaymentDetailsEntity paymentDetails = PaymentDetailsConverter.convertToEntity(createBookingRequest.getBankingDetails());

        if(createBookingRequest.getNumberOfTravelers() > excursion.getNumberOfAvaliableSpaces()){
            throw new IllegalStateException("Not enough spaces avaliable on this excursion!");
        }

        excursion.setNumberOfSpacesLeft(excursion.getNumberOfSpacesLeft() - createBookingRequest.getNumberOfTravelers());
        excursionRepository.save(excursion);


        BookingEntity bookingEntity = BookingEntity.builder()
                .user(userEntity)
                .excursion(excursion)
                .bookingTime(createBookingRequest.getBookingTime())
                .status(createBookingRequest.getStatus())
                .bankingDetails(paymentDetails)
                .numberOfTravelers(createBookingRequest.getNumberOfTravelers())
                .build();

        BookingEntity bookingEntity1 = bookingRepository.save(bookingEntity);


        return BookingConverter.mapToDomain(bookingEntity1);
    }

    public boolean deleteBooking(Long id) {
        try {
            Optional<BookingEntity> bookingEntity = bookingRepository.findById(id);
            if(bookingEntity.isPresent()){

                BookingEntity booking = bookingEntity.get();

                if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name()) &&
                        !requestAccessToken.getUserID().equals(booking.getUser().getId())) {
                    throw new UnauthorizedDataAccessException(UNAUTHORIZED_ACCESS);
                }

                Date currentDate = new Date();
                Date tripStartDate = booking.getExcursion().getStartDate();
                long twoWeeksInMillis = 14 * 24 * 60 * 60 * 1000;
                long timeDiff = tripStartDate.getTime() - currentDate.getTime();

                if (timeDiff < twoWeeksInMillis) {
                    throw new IllegalStateException("Cannot cancel trip. Cancellation period has passed.");
                } else {
                    bookingRepository.deleteById(id);
                    return true;
                }

            }else{
                throw new NotFoundException("Booking not found.");
            }

        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean updateBooking(UpdateBookingRequest updateBookingRequest) {
        if (updateBookingRequest.getNumberOfTravelers() < 0 || updateBookingRequest.getBookingTime() == null || updateBookingRequest.getBankingDetails() == null ||
                updateBookingRequest.getStatus() == null || updateBookingRequest.getUser() == null || updateBookingRequest.getExcursion() == null) {
            throw new InvalidExcursionDataException("Invalid input data");
        }
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(updateBookingRequest.getExcursion());
        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsConverter.convertToEntity(updateBookingRequest.getBankingDetails());

        Optional<BookingEntity> optionalBooking = bookingRepository.findById(updateBookingRequest.getId());
        if (optionalBooking.isPresent()) {
            BookingEntity existingBooking = optionalBooking.get();

            if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name()) &&
                    !requestAccessToken.getUserID().equals(existingBooking.getUser().getId())) {
                throw new UnauthorizedDataAccessException(UNAUTHORIZED_ACCESS);
            }
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

    public List<Booking> getBookingsByUser(User user) {
        if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name()) &&
                !requestAccessToken.getUserID().equals(user.getId())) {
            throw new UnauthorizedDataAccessException(UNAUTHORIZED_ACCESS);
        }
        UserEntity userEntity = UserConverter.convertToEntity(user);
        List<BookingEntity> bookingEntities = bookingRepository.findByUser(userEntity);
        return bookingEntities.stream().map(BookingConverter::mapToDomain).toList();
    }

    public List<Booking> getPastBookingsByUser(User user) {
        if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name()) &&
                !requestAccessToken.getUserID().equals(user.getId())) {
            throw new UnauthorizedDataAccessException(UNAUTHORIZED_ACCESS);
        }
        UserEntity userEntity = UserConverter.convertToEntity(user);
        LocalDateTime currentDate = LocalDateTime.now();
        Date currentDateAsDate = Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant());
        List<BookingEntity> bookingEntities = bookingRepository.findByUserAndExcursion_StartDateBeforeOrExcursion_StartDateEquals(userEntity, currentDateAsDate);
        return bookingEntities.stream().map(BookingConverter::mapToDomain).toList();
    }
    public List<Booking> getFutureBookingsByUser(User user) {
        if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name()) &&
                !requestAccessToken.getUserID().equals(user.getId())) {
            throw new UnauthorizedDataAccessException(UNAUTHORIZED_ACCESS);
        }
        UserEntity userEntity = UserConverter.convertToEntity(user);
        LocalDateTime currentDate = LocalDateTime.now();
        Date currentDateAsDate = Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant());
        List<BookingEntity> bookingEntities = bookingRepository.findByUserAndExcursion_StartDateAfter(userEntity, currentDateAsDate);
        return bookingEntities.stream().map(BookingConverter::mapToDomain).toList();
    }
    public List<Booking> getBookingsByExcursion(Excursion excursion) {
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(excursion);
        List<BookingEntity> bookingEntities = bookingRepository.findByExcursion(excursionEntity);
        return bookingEntities.stream()
                .map(BookingConverter::mapToDomain)
                .toList();
    }

    public Double getTotalSalesInLastQuarter(LocalDateTime startDate, LocalDateTime endDate, BookingStatus status){
        return bookingRepository.getTotalSalesInLastQuarter(startDate, endDate, status);
    }

    public Double getTotalSalesInLastQuarterForExcursion(Long excursionID,LocalDateTime startDate, LocalDateTime endDate, BookingStatus status){
        return bookingRepository.getTotalSalesInLastQuarterForExcursion(excursionID, startDate, endDate, status);
    }

    public List<WeeklyStatisticsDTO> getWeeklyStatistics(Long excursionID, BookingStatus status){
        return bookingRepository.getWeeklyStatistics(excursionID, status);
    }

    public List<BookingDataDTO> getBookingDataByDateRangePerExcursion(Long excursionID, LocalDateTime startDate, LocalDateTime endDate){
        return bookingRepository.getBookingDataByDateRange(excursionID, startDate,endDate);
    }


}
