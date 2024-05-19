package org.individualproject.business;

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
import org.individualproject.domain.enums.Gender;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.entity.BookingEntity;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private AccessToken accessToken;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ExcursionRepository excursionRepository;

    @InjectMocks
    private BookingService bookingService;

    @InjectMocks
    private ExcursionService excursionService;
    @Test
    void getBookings_shouldReturnBookings() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        List<BookingEntity> allBookingEntitities = Arrays.asList(
                BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build(),
                BookingEntity.builder().id(2L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build()
        );
        List<Booking> bookings = BookingConverter.mapToDomainList(allBookingEntitities);

        when(bookingRepository.findAll()).thenReturn(allBookingEntitities);

        // Act
        List<Booking> result = bookingService.getBookings();

        // Assert
        assertEquals(bookings, result);
        verify(bookingRepository, times(1)).findAll();
    }
    @Test
    void getBookings_shouldReturnEmptyListWithNoBookings(){
        // Arrange
        when(bookingRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        List<Booking> result = bookingService.getBookings();

        //Assert
        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).findAll();

    }
    @Test
    void getBooking_shouldReturnBooking() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();

        Booking expectedBooking= BookingConverter.mapToDomain(bookingEntity);
        Long id = 1L;
        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntity));

        // Act
        Optional<Booking> result = bookingService.getBooking(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedBooking, result.get());
        verify(bookingRepository, times(1)).findById(id);
    }

    @Test
    void getBooking_nonExistingBooking() {
        Long id = 234L;
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Booking> result = bookingService.getBooking(id);

        // Assert
        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).findById(id);
    }
    @Test
    void createBooking() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();
        User user = UserConverter.mapToDomain(fakeUserEntity);
        Excursion excursion = ExcursionConverter.mapToDomain(fakeExcursionEntity);
        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(fakePaymentDetailsEntity);


        CreateBookingRequest createRequest = CreateBookingRequest.builder()
                .user(user)
                .bankingDetails(paymentDetails)
                .bookingTime(LocalDateTime.now())
                .excursion(excursion)
                .numberOfTravelers(5)
                .status(BookingStatus.PENDING)
                .build();


        // Mock the excursionRepository
        when(bookingRepository.save(any(BookingEntity.class))).thenReturn(bookingEntity);

        Booking expectedExcursion = BookingConverter.mapToDomain(bookingEntity);
        // Act:

        Booking result = bookingService.createBooking(createRequest);

        // Assert
        assertEquals(expectedExcursion, result);
        verify(bookingRepository, times(1)).save(any(BookingEntity.class));
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsParams")
    void createBooking_shouldThrowExceptionForInvalidInput(CreateBookingRequest invalidRequest) {
        assertThrows(InvalidExcursionDataException.class, () -> bookingService.createBooking(invalidRequest));
        verify(bookingRepository, never()).save(any());
    }

    private static Stream<Arguments> provideStringsForIsParams() {
        Date startDate = new Date();
        Date endDate = new Date();
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        User validUser = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "hashedPassword1", Gender.MALE);
        Excursion excursion = Excursion.builder().id(1L).name("Mountain Hike").destinations(Arrays.asList("Paris", "London")).startDate(startDate).endDate(endDate).travelAgency(validUser).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetails paymentDetails = PaymentDetails.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(validUser).build();

        return Stream.of(
                Arguments.of(new CreateBookingRequest(null, excursion,LocalDateTime.now(), BookingStatus.PENDING, paymentDetails, 5)),
                Arguments.of(new CreateBookingRequest(validUser, null,LocalDateTime.now(), BookingStatus.PENDING, paymentDetails, 5)),
                Arguments.of(new CreateBookingRequest(validUser, excursion,null, BookingStatus.PENDING, paymentDetails, 5)),
                Arguments.of(new CreateBookingRequest(validUser, excursion,LocalDateTime.now(), null, paymentDetails, 5)),
                Arguments.of(new CreateBookingRequest(validUser, excursion,LocalDateTime.now(), BookingStatus.PENDING, null, 5)),
                Arguments.of(new CreateBookingRequest(validUser, excursion,LocalDateTime.now(), BookingStatus.PENDING, paymentDetails, -1))
        );
    }
    @Test
    void deleteBooking_shouldDeleteExistingBooking() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();
         when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingEntity));
        doNothing().when(bookingRepository).deleteById(1L);

        // Method call
        boolean result = bookingService.deleteBooking(1L);

        // Verification
        assertTrue(result);
    }

//    @Test
//    void deleteBooking_shouldThrowExceptionForPassedCancelationPeriod() {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2024, 4, 21);
//        Date endDate = new Date(2024, 5, 4);
//        LocalDate expDate = LocalDate.of(2027, 9, 16);
//
//        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
//        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();
//
//        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();
//        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingEntity));
//
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> bookingService.deleteBooking(1L));
//
//        // Assert the exception message
//        assertEquals("Cannot cancel trip. Cancellation period has passed.", exception.getMessage());
//    }
    @Test
    void deleteBooking_shouldThrowExceptionForBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.deleteBooking(1L));
    }
    @Test
    void updateBooking_shouldUpdateExistingBookingWithValidInput(){
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();
        User user = UserConverter.mapToDomain(fakeUserEntity);
        Excursion excursion = ExcursionConverter.mapToDomain(fakeExcursionEntity);
        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(fakePaymentDetailsEntity);


        UpdateBookingRequest updateBookingRequest = UpdateBookingRequest.builder()
                .id(1L)
                .user(user)
                .bankingDetails(paymentDetails)
                .bookingTime(LocalDateTime.now())
                .excursion(excursion)
                .numberOfTravelers(7)
                .status(BookingStatus.COMPLETED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingEntity));

        boolean updateResult = bookingService.updateBooking(updateBookingRequest);

        // Assert
        assertTrue(updateResult);
        verify(bookingRepository, times(1)).save(bookingEntity);

    }

    @Test
    void updateBooking_shouldReturnFalseWhenBookingDoNotExist(){

        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        User user = UserConverter.mapToDomain(fakeUserEntity);
        Excursion excursion = ExcursionConverter.mapToDomain(fakeExcursionEntity);
        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(fakePaymentDetailsEntity);


        UpdateBookingRequest updateBookingRequest = UpdateBookingRequest.builder()
                .id(1L)
                .user(user)
                .bankingDetails(paymentDetails)
                .bookingTime(LocalDateTime.now())
                .excursion(excursion)
                .numberOfTravelers(7)
                .status(BookingStatus.COMPLETED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act:
        boolean updateResult = bookingService.updateBooking(updateBookingRequest);

        // Assert
        assertFalse(updateResult);
        verify(bookingRepository, never()).save(any(BookingEntity.class));

    }

    @Test
    void getBookingsByUser_shouldReturnBookingsForAuthorizedUser() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        User travelAgency = User.builder().id(1L).firstName("Travel").lastName("Agency").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        UserEntity userEntity = UserConverter.convertToEntity(travelAgency);
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(userEntity).build();

        //User travelAgency = new User(1L, "Travel Agency", "Agency", null, null, null, null);
        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
        when(accessToken.getUserID()).thenReturn(travelAgency.getId());

        List<BookingEntity> allBookingEntitities = Arrays.asList(
                BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(userEntity).build(),
                BookingEntity.builder().id(2L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(userEntity).build()
        );
        when(bookingRepository.findByUser(userEntity)).thenReturn(allBookingEntitities);

        // Act
        List<Booking> bookings = bookingService.getBookingsByUser(travelAgency);

        // Assert
        assertEquals(2, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(2L, bookings.get(1).getId());
    }

//    @Test
//    void getBookingsByUser_shouldThrowExceptionForNonexistentUser() {
//        // Simulate a non-existent user
//        User nonExistentUser = User.builder().id(999L).firstName("Nonexistent").lastName("User").build();
//
//        // Configure mock behavior
//        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
//        when(accessToken.getUserID()).thenReturn(999L); // Set user ID to simulate a different user
//
//        // Mock behavior for findByUser when the user does not exist
//        when(bookingRepository.findByUser(any(UserEntity.class))).thenReturn(Collections.emptyList());
//
//        // Call the method and assert that it throws an exception
//        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByUser(nonExistentUser));
//    }

//    @Test
//    void getBookingsByUser_shouldThrowExceptionForUnauthorizedUser() {
//        // Simulate a different user ID or role
//        User travelAgency2 = User.builder().id(2L).firstName("Travel2").lastName("Agency2").build();
//
//        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
//        when(accessToken.getUserID()).thenReturn(2L); // Use a different user ID
//
//        User travelAgency = User.builder().id(1L).firstName("Travel").lastName("Agency").build();
//
//        // Ensure userEntity is not null when calling getBookingsByUser
//        List<BookingEntity> bookingEntities = Collections.singletonList(new BookingEntity());
//        when(bookingRepository.findByUser(any())).thenReturn(bookingEntities);
//
//        // Call the method with a non-null user object
//        assertThrows(UnauthorizedDataAccessException.class, () -> bookingService.getBookingsByUser(travelAgency2)); // Use travelAgency2
//    }
//

}