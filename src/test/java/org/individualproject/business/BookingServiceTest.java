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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
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
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
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
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();

        Booking expectedBooking= BookingConverter.mapToDomain(bookingEntity);
        Long id = 1L;
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);
        when(bookingRepository.findById(id)).thenReturn(Optional.of(bookingEntity));

        // Act
        Optional<Booking> result = bookingService.getBooking(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedBooking, result.get());
        verify(bookingRepository, times(1)).findById(id);
    }
    @Test
    void getBooking_shouldThrowUnauthorizedAccessExceptionForUserId() {
        // Arrange
        Long bookingId = 1L;
        Long currentUserID = 1L;
        Long anotherUserID = 2L;
        UserEntity currentUserEntity = new UserEntity();
        currentUserEntity.setId(currentUserID);

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(bookingId);
        bookingEntity.setUser(currentUserEntity);

        when(accessToken.getUserID()).thenReturn(anotherUserID);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingEntity));

        // Act & Assert
        assertThrows(UnauthorizedDataAccessException.class, () -> bookingService.getBooking(bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
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
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
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
        YearMonth expDate = YearMonth.of(2027, 9);

        User validUser = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "johnDoe", "hashedPassword1", Gender.MALE);
        Excursion excursion = Excursion.builder().id(1L).name("Mountain Hike").description("description").destinations(Arrays.asList("Paris", "London")).startDate(startDate).endDate(endDate).travelAgency(validUser).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
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
        // Arrange
        YearMonth expDate = YearMonth.of(2027, 9);
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        LocalDateTime bookingTime = LocalDateTime.of(2024, 6, 1, 10, 0);
        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).description("description").name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(startDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(bookingTime).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingEntity));
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);

        // Act
        boolean result = bookingService.deleteBooking(1L);

        // Assert
        assertTrue(result);
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteBooking_shouldThrowExceptionForPassedCancelationPeriod() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date currentDate = new Date();
        YearMonth expDate = YearMonth.of(2027, 9);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 12);
        Date startDate = calendar.getTime();
        LocalDateTime bookingTime = LocalDateTime.of(2024, 6, 1, 10, 0);
        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(startDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(bookingTime).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingEntity));
        when(accessToken.getUserID()).thenReturn(fakeUserEntity.getId());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> bookingService.deleteBooking(1L));
        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, never()).deleteById(1L);
    }
    @Test
    void deleteBooking_shouldThrowExceptionForBookingNotFound() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.deleteBooking(1L));
    }
    @Test
    void deleteBooking_shouldThrowUnauthorizedAccessExceptionForInvalidUser() {
        // Arrange
        Long bookingId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(bookingId);
        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);
        bookingEntity.setUser(anotherUser);

        when(accessToken.getUserID()).thenReturn(userEntity.getId());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingEntity));

        assertThrows(UnauthorizedDataAccessException.class, () -> bookingService.deleteBooking(bookingId));
        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).deleteById(bookingId);
    }

    @Test
    void updateBooking_shouldUpdateExistingBookingWithValidInput(){
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
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
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingEntity));
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);

        boolean updateResult = bookingService.updateBooking(updateBookingRequest);

        // Assert
        assertTrue(updateResult);
        verify(bookingRepository, times(1)).save(bookingEntity);

    }

    @ParameterizedTest
    @MethodSource("provideInvalidUpdateBookingRequests")
    void updateBooking_shouldThrowExceptionForInvalidInput(UpdateBookingRequest invalidRequest) {

        LocalDate date = LocalDate.of(2014, 9, 16);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);

        assertThrows(InvalidExcursionDataException.class, () -> bookingService.updateBooking(invalidRequest));
        verify(bookingRepository, never()).save(any());
    }

    private static Stream<Arguments> provideInvalidUpdateBookingRequests() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        YearMonth expDate = YearMonth.of(2027, 9);

        LocalDateTime bookingTime = LocalDateTime.now();
        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(fakeUserEntity).build();

        User user = UserConverter.mapToDomain(fakeUserEntity);
        Excursion excursion = ExcursionConverter.mapToDomain(fakeExcursionEntity);
        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(fakePaymentDetailsEntity);

        BookingEntity bookingEntity = BookingEntity.builder().id(1L).bookingTime(bookingTime).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(fakeUserEntity).build();

        return Stream.of(
                Arguments.of(new UpdateBookingRequest(1L, null, excursion, bookingTime, BookingStatus.CONFIRMED, paymentDetails, 7 )),
                Arguments.of(new UpdateBookingRequest(1L, user, null, bookingTime, BookingStatus.CONFIRMED, paymentDetails, 7 )),
                Arguments.of(new UpdateBookingRequest(1L, user, excursion, null, BookingStatus.CONFIRMED, paymentDetails, 7 )),
                Arguments.of(new UpdateBookingRequest(1L, user, excursion, bookingTime, null, paymentDetails, 7 )),
                Arguments.of(new UpdateBookingRequest(1L, user, excursion, bookingTime, BookingStatus.CONFIRMED, null, 7 )),
                Arguments.of(new UpdateBookingRequest(1L, user, excursion, bookingTime, BookingStatus.CONFIRMED, paymentDetails, -3 ))
                );
    }
    @Test
    void updateBooking_shouldReturnFalseWhenBookingDoNotExist(){

        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
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
                .status(BookingStatus.CONFIRMED)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        assertThrows(NotFoundException.class, () -> {
            // Act
            bookingService.updateBooking(updateBookingRequest);
        });
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateBooking_shouldThrowUnauthorizedAccessExceptionForInvalidUser() {
        // Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(fakeUserEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
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
                .status(BookingStatus.CONFIRMED)
                .build();
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(false);
        when(accessToken.getUserID()).thenReturn(100L);

        when(bookingRepository.findById(updateBookingRequest.getId())).thenReturn(Optional.of(bookingEntity));

        assertThrows(UnauthorizedDataAccessException.class, () -> bookingService.updateBooking(updateBookingRequest));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingsByUser_shouldReturnBookingsForAuthorizedUser() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        YearMonth expDate = YearMonth.of(2027, 9);

        User travelAgency = User.builder().id(1L).firstName("Travel").lastName("Agency").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        UserEntity userEntity = UserConverter.convertToEntity(travelAgency);
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(userEntity).build();

        //User travelAgency = new User(1L, "Travel Agency", "Agency", null, null, null, null);
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

    @Test
    void getBookingsByUser_shouldThrowExceptionForUnauthorizedUser() {
        // Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        User otherUser = User.builder().id(2L).firstName("Other").lastName("User").birthDate(date).email("other@example.com").hashedPassword("otherPassword").gender(Gender.FEMALE).build();

        when(accessToken.getUserID()).thenReturn(3L);

        // Act & Assert
        assertThrows(UnauthorizedDataAccessException.class, () -> bookingService.getBookingsByUser(otherUser));
        verify(bookingRepository, never()).findByUser(any());
    }

    @Test
    void getBookingsByUser_shouldReturnEmptyListForNonExistingUser() {
        // Arrange

        LocalDate date = LocalDate.of(2014, 9, 16);
        User nonExistingUser = User.builder().id(100L).firstName("Non").lastName("Existing").birthDate(date).email("nonexisting@example.com").hashedPassword("nonExistingPwd").gender(Gender.MALE).build();
        UserEntity userEntity = UserConverter.convertToEntity(nonExistingUser);

        when(accessToken.getUserID()).thenReturn(nonExistingUser.getId());
        when(bookingRepository.findByUser(userEntity)).thenReturn(Collections.emptyList());

        // Act
        List<Booking> bookings = bookingService.getBookingsByUser(nonExistingUser);

        // Assert
        assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByUser(userEntity);
    }
//    @Test
//    void getPastBookingsByUser_shouldReturnPastBookingsForAuthorizedUser() {
//        // Arrange
//        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
//        User user = User.builder()
//                .id(1L)
//                .firstName("John")
//                .lastName("Doe")
//                .birthDate(dateOfBirth)
//                .email("john.doe@example.com")
//                .hashedPassword("hashedPassword1")
//                .gender(Gender.MALE)
//                .build();
//        UserEntity userEntity = UserConverter.convertToEntity(user);
//        Date startDate = new Date(2022, 9, 16);
//        Date endDate = new Date(2022, 9, 24);
//
//        LocalDate expDate = LocalDate.of(2027, 9, 16);
//        User validUser = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "JohdnDoe", "hashedPassword1", Gender.MALE);
//        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(userEntity).build();
//        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder()
//                .id(1L)
//                .name("Mountain Hike")
//                .destinations("Paris,London")
//                .startDate(startDate)
//                .endDate(endDate)
//                .travelAgency(UserConverter.convertToEntity(validUser))
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .numberOfSpacesLeft(58)
//                .build();
//
//        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
//        when(accessToken.getUserID()).thenReturn(user.getId());
//
//        LocalDateTime currentDate = LocalDateTime.of(2024, 6, 6, 0, 0); // Assuming the test runs on June 6, 2024
//        Date currentDateAsUtilDate = Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant());
//
//        BookingEntity pastBooking1 = BookingEntity.builder()
//                .id(1L)
//                .bookingTime(LocalDateTime.of(2024, 5, 1, 10, 0)) // Past booking
//                .excursion(fakeExcursionEntity)
//                .status(BookingStatus.PENDING)
//                .numberOfTravelers(4)
//                .bankingDetails(fakePaymentDetailsEntity)
//                .user(userEntity)
//                .build();
//        BookingEntity pastBooking2 = BookingEntity.builder()
//                .id(2L)
//                .bookingTime(LocalDateTime.of(2024, 5, 15, 10, 0)) // Past booking
//                .excursion(fakeExcursionEntity)
//                .status(BookingStatus.PENDING)
//                .numberOfTravelers(4)
//                .bankingDetails(fakePaymentDetailsEntity)
//                .user(userEntity)
//                .build();
//        List<BookingEntity> pastBookingEntities = List.of(pastBooking1, pastBooking2);
//
//        // Adjust this line to use any() for the second argument
//        when(bookingRepository.findByUserAndExcursion_StartDateAfter(any(), eq(currentDateAsUtilDate)))
//                .thenReturn(pastBookingEntities);
//
//        // Act
//        List<Booking> pastBookings = bookingService.getPastBookingsByUser(user);
//
//        // Assert
//        assertEquals(2, pastBookings.size());
//        assertEquals(1L, pastBookings.get(0).getId());
//        assertEquals(2L, pastBookings.get(1).getId());
//        verify(bookingRepository, times(1)).findByUserAndExcursion_StartDateAfter(any(), eq(currentDateAsUtilDate));
//    }

    @Test
    void getPastBookingsByUser_shouldThrowExceptionForUnauthorizedUser() {
        // Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        User otherUser = User.builder().id(2L).firstName("Other").lastName("User").birthDate(date).email("other@example.com").hashedPassword("otherPassword").gender(Gender.FEMALE).build();

        when(accessToken.getUserID()).thenReturn(3L);

        // Act & Assert
        assertThrows(UnauthorizedDataAccessException.class, () -> bookingService.getPastBookingsByUser(otherUser));
        verify(bookingRepository, never()).findByUser(any());
    }

//    @Test
//    void getPastBookingsByUser_shouldReturnEmptyListForNonExistingUser() {
//        // Arrange
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        User nonExistingUser = User.builder().id(100L).firstName("Non").lastName("Existing").birthDate(date).email("nonexisting@example.com").hashedPassword("nonExistingPwd").gender(Gender.MALE).build();
//
//        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
//        when(accessToken.getUserID()).thenReturn(nonExistingUser.getId());
//        when(bookingRepository.findByUserAndExcursion_StartDateAfter(any(UserEntity.class), any(Date.class))).thenReturn(Collections.emptyList());
//
//        // Act
//        List<Booking> bookings = bookingService.getPastBookingsByUser(nonExistingUser);
//
//        // Assert
//        assertTrue(bookings.isEmpty());
//        verify(bookingRepository, times(1)).findByUserAndExcursion_StartDateBeforeOrExcursion_StartDateEquals(any(UserEntity.class), any(Date.class));
//    }
//
//    @Test
//    void getFutureBookingsByUser_shouldReturnFutureBookingsForAuthorizedUser() {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        LocalDateTime currentDate = LocalDateTime.now();
//        Date currentDateAsDate = Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant());
//        Date currentDateAsUtilDate = Date.from(currentDate.atZone(ZoneId.systemDefault()).toInstant());
//        Date startDate = new Date(2026, 9, 16);
//        Date endDate = new Date(2026, 9, 24);
//
//        User user = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        UserEntity userEntity = UserConverter.convertToEntity(user);
//
//        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
//        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(date).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(userEntity).build();
//
//        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
//        when(accessToken.getUserID()).thenReturn(user.getId());
//
//        List<BookingEntity> futureBookingEntities = Arrays.asList(
//                BookingEntity.builder().id(1L).bookingTime(currentDate).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(userEntity).build(),
//                BookingEntity.builder().id(2L).bookingTime(currentDate).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(userEntity).build()
//        );
//
//        when(bookingRepository.findByUserAndExcursion_StartDateBeforeOrExcursion_StartDateEquals(userEntity, currentDateAsDate))
//                .thenReturn(futureBookingEntities);
//
//        // Act
//        List<Booking> futureBookings = bookingService.getFutureBookingsByUser(user);
//
//        // Assert
//        assertEquals(2, futureBookings.size());
//        assertEquals(1L, futureBookings.get(0).getId());
//        assertEquals(2L, futureBookings.get(1).getId());
//    }
    @Test
    void getFutureBookingsByUser_shouldThrowExceptionForUnauthorizedUser() {
        // Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        User otherUser = User.builder().id(2L).firstName("Other").lastName("User").birthDate(date).email("other@example.com").hashedPassword("otherPassword").gender(Gender.FEMALE).build();

        when(accessToken.getUserID()).thenReturn(3L);

        // Act & Assert
        assertThrows(UnauthorizedDataAccessException.class, () -> bookingService.getFutureBookingsByUser(otherUser));
        verify(bookingRepository, never()).findByUser(any());
    }

//    @Test
//    void getFutureBookingsByUser_shouldReturnEmptyListForNonExistingUser() {
//        // Arrange
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        User nonExistingUser = User.builder().id(100L).firstName("Non").lastName("Existing").birthDate(date).email("nonexisting@example.com").hashedPassword("nonExistingPwd").gender(Gender.MALE).build();
//
//        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
//        when(accessToken.getUserID()).thenReturn(nonExistingUser.getId());
//        when(bookingRepository.findByUserAndExcursion_StartDateBeforeOrExcursion_StartDateEquals(any(UserEntity.class), any(Date.class))).thenReturn(Collections.emptyList());
//
//        // Act
//        List<Booking> bookings = bookingService.getFutureBookingsByUser(nonExistingUser);
//
//        // Assert
//        assertTrue(bookings.isEmpty());
//        verify(bookingRepository, times(1)).findByUserAndExcursion_StartDateAfter(any(UserEntity.class), any(Date.class));
//    }

    @Test
    void getBookingsByExcursion_shouldReturnBookings() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        YearMonth expDate = YearMonth.of(2027, 9);

        User travelAgency = User.builder().id(1L).firstName("Travel").lastName("Agency").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        UserEntity userEntity = UserConverter.convertToEntity(travelAgency);
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();
        PaymentDetailsEntity fakePaymentDetailsEntity = PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(userEntity).build();

        Excursion excursion = ExcursionConverter.mapToDomain(fakeExcursionEntity);

        List<BookingEntity> allBookingEntitities = Arrays.asList(
                BookingEntity.builder().id(1L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(userEntity).build(),
                BookingEntity.builder().id(2L).bookingTime(LocalDateTime.now()).excursion(fakeExcursionEntity).status(BookingStatus.PENDING).numberOfTravelers(4).bankingDetails(fakePaymentDetailsEntity).user(userEntity).build()
        );
        when(bookingRepository.findByExcursion(fakeExcursionEntity)).thenReturn(allBookingEntitities);

        // Act
        List<Booking> bookings = bookingService.getBookingsByExcursion(excursion);

        // Assert
        assertEquals(2, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(2L, bookings.get(1).getId());
    }

    @Test
    void getBookingsByExcursion_shouldReturnEmptyListForNonExistingExcursion() {
        // Arrange
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        LocalDate date = LocalDate.of(2014, 9, 16);
        User nonExistingUser = User.builder().id(100L).firstName("Non").lastName("Existing").birthDate(date).email("nonexisting@example.com").hashedPassword("nonExistingPwd").gender(Gender.MALE).build();
        UserEntity userEntity = UserConverter.convertToEntity(nonExistingUser);
        ExcursionEntity fakeExcursionEntity = ExcursionEntity.builder().id(1L).name("Mountain Hike").description("description").destinations("Mount Everest Base Camp, Annapurna Circuit").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(1500.0).numberOfAvaliableSpaces(58).numberOfSpacesLeft(58).build();

        Excursion excursion = ExcursionConverter.mapToDomain(fakeExcursionEntity);

        when(bookingRepository.findByExcursion(fakeExcursionEntity)).thenReturn(Collections.emptyList());

        // Act
        List<Booking> bookings = bookingService.getBookingsByExcursion(excursion);

        // Assert
        assertTrue(bookings.isEmpty());
        verify(bookingRepository, times(1)).findByExcursion(fakeExcursionEntity);
    }

}