package org.individualproject.business;

import org.individualproject.business.converter.PaymentDetailsConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.InvalidExcursionDataException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.Gender;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.PaymentDetailsRepository;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentDetailsServiceTest {
    @Mock
    private PaymentDetailsRepository paymentDetailsRepository;

    @Mock
    private AccessToken accessToken;

    @InjectMocks
    private PaymentDetailsService paymentDetailsService;

    @Test
    void getAllPaymentDetails_returnsAllPaymentDetails() {
        // Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        List<PaymentDetailsEntity> allPaymentDetailsEntities = Arrays.asList(
                PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123455").cardHolderName("Nick Jonas").user(userEntity).build(),
                PaymentDetailsEntity.builder().id(1L).expirationDate(expDate).cvv("573").cardNumber("1257567890123455").cardHolderName("Joe Jonas").user(userEntity).build()
        );

        List<PaymentDetails> paymentDetailsList = PaymentDetailsConverter.mapToDomainList(allPaymentDetailsEntities);

        when(paymentDetailsRepository.findAll()).thenReturn(allPaymentDetailsEntities);

        // Act
        List<PaymentDetails> result = paymentDetailsService.getAllPaymentDetails();

        // Assert
        assertEquals(paymentDetailsList, result);
        verify(paymentDetailsRepository, times(1)).findAll();
    }

    @Test
    void getAllPaymentDetails_returnsEmptyList() {
        // Arrange
        when(paymentDetailsRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        List<PaymentDetails> result = paymentDetailsService.getAllPaymentDetails();

        //Assert
        assertTrue(result.isEmpty());
        verify(paymentDetailsRepository, times(1)).findAll();
    }

    @Test
    void getPaymentDetails_shouldReturnPaymentDetails() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();

        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                .id(1L)
                .expirationDate(expDate)
                .cvv("123")
                .cardNumber("1234567890123456")
                .cardHolderName("Nick Jonas")
                .user(userEntity)
                .build();
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);
        when(paymentDetailsRepository.findById(1L)).thenReturn(Optional.of(paymentDetailsEntity));
        Optional<PaymentDetails> result = paymentDetailsService.getPaymentDetails(1L);

        verify(paymentDetailsRepository).findById(1L);
        assertEquals(paymentDetailsEntity.getId(), result.get().getId());
        assertEquals(paymentDetailsEntity.getExpirationDate(), result.get().getExpirationDate());
        assertEquals(paymentDetailsEntity.getCvv(), result.get().getCvv());
        assertEquals(paymentDetailsEntity.getCardNumber(), result.get().getCardNumber());
        assertEquals(paymentDetailsEntity.getCardHolderName(), result.get().getCardHolderName());
    }
    @Test
    void getPaymentDetails_shouldThrowUnauthorizedAccessExceptionForUserId() {
        // Arrange
        Long detailsId = 1L;
        Long currentUserID = 1L;
        Long anotherUserID = 2L;
        UserEntity currentUserEntity = new UserEntity();
        currentUserEntity.setId(currentUserID);

        PaymentDetailsEntity paymentDetailsEntity = new PaymentDetailsEntity();
        paymentDetailsEntity.setId(detailsId);
        paymentDetailsEntity.setUser(currentUserEntity);

        when(accessToken.getUserID()).thenReturn(anotherUserID);
        when(paymentDetailsRepository.findById(detailsId)).thenReturn(Optional.of(paymentDetailsEntity));

        assertThrows(UnauthorizedDataAccessException.class, () -> paymentDetailsService.getPaymentDetails(detailsId));
        verify(paymentDetailsRepository, times(1)).findById(detailsId);
    }

    @Test
    void getPaymentDetails_nonExistingPaymentDetails() {
        Long id = 1L;
        when(paymentDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<PaymentDetails> result = paymentDetailsService.getPaymentDetails(1L);
        assertTrue(result.isEmpty());
        verify(paymentDetailsRepository, times(1)).findById(id);
    }

    @Test
    void createPaymentDetails_shouldSavePaymentDetails() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);
        CreatePaymentDetailsRequest request = new CreatePaymentDetailsRequest();
        request.setUser(user);
        request.setExpirationDate(expDate);
        request.setCardNumber("1234567890123456");
        request.setCardHolderName("Nick Jonas");
        request.setCvv("123");

        PaymentDetailsEntity savedPaymentDetailsEntity = PaymentDetailsEntity.builder()
                .id(1L)
                .expirationDate(request.getExpirationDate())
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .cvv(request.getCvv())
                .user(userEntity)
                .build();
        when(paymentDetailsRepository.save(any())).thenReturn(savedPaymentDetailsEntity);

        PaymentDetails result = paymentDetailsService.createPaymentDetails(request);

        verify(paymentDetailsRepository).save(any());
        assertEquals(savedPaymentDetailsEntity.getId(), result.getId());
        assertEquals(savedPaymentDetailsEntity.getExpirationDate(), result.getExpirationDate());
        assertEquals(savedPaymentDetailsEntity.getCardNumber(), result.getCardNumber());
        assertEquals(savedPaymentDetailsEntity.getCardHolderName(), result.getCardHolderName());
        assertEquals(savedPaymentDetailsEntity.getCvv(), result.getCvv());
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsParams")
    void createPaymentDetails_shouldThrowExceptionForInvalidInput(CreatePaymentDetailsRequest invalidRequest) {
        assertThrows(InvalidExcursionDataException.class, () -> paymentDetailsService.createPaymentDetails(invalidRequest));
        verify(paymentDetailsRepository, never()).save(any());
    }

    private static Stream<Arguments> provideStringsForIsParams() {
        YearMonth expDate = YearMonth.of(2027, 9);
        User validUser = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "johnDoe","hashedPassword1", Gender.MALE);

        return Stream.of(
                Arguments.of(new CreatePaymentDetailsRequest(null, "1234567890123456", "234", expDate, "Nick Jonas")),
                Arguments.of(new CreatePaymentDetailsRequest(validUser, null, "234", expDate, "Nick Jonas")),
                Arguments.of(new CreatePaymentDetailsRequest(validUser, "1234567890123456", "232344", null, "Nick Jonas")),
                Arguments.of(new CreatePaymentDetailsRequest(validUser, "1234567890123456", "234", expDate, null)),
                Arguments.of(new CreatePaymentDetailsRequest(validUser, "1234567890123456", null, expDate, "Nick Jonas"))

        );
    }

    @Test
    void deletePaymentDetails_shouldDeleteExistingPaymentDetails() {
        // Arrange
        Long detailsId = 1L;
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        PaymentDetailsEntity paymentDetailsEntity = new PaymentDetailsEntity();
        paymentDetailsEntity.setId(detailsId);
        paymentDetailsEntity.setUser(userEntity);

        when(accessToken.getUserID()).thenReturn(userId);
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(false);
        when(paymentDetailsRepository.findById(detailsId)).thenReturn(Optional.of(paymentDetailsEntity));

        // Act
        boolean result = paymentDetailsService.deletePaymentDetails(detailsId);

        // Assert
        assertTrue(result);
        verify(paymentDetailsRepository, times(1)).deleteById(detailsId);
    }

    @Test
    void deletePaymentDetails_shouldThrowUnauthorizedAccessExceptionForUnauthorizedUser() {
        // Arrange
        Long detailsId = 1L;
        Long userId = 1L;
        Long anotherUserId = 2L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        PaymentDetailsEntity paymentDetailsEntity = new PaymentDetailsEntity();
        paymentDetailsEntity.setId(detailsId);
        paymentDetailsEntity.setUser(userEntity);

        when(accessToken.getUserID()).thenReturn(anotherUserId);
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(false);
        when(paymentDetailsRepository.findById(detailsId)).thenReturn(Optional.of(paymentDetailsEntity));

        // Act & Assert
        assertThrows(UnauthorizedDataAccessException.class, () -> paymentDetailsService.deletePaymentDetails(detailsId));
        verify(paymentDetailsRepository, never()).deleteById(detailsId);
    }
    @Test
    void deletePaymentDetails_nonExistingPaymentDetails(){
        // Arrange
        Long detailsId = 1L;

        when(paymentDetailsRepository.findById(detailsId)).thenReturn(Optional.empty());

        // Act
        boolean result = paymentDetailsService.deletePaymentDetails(detailsId);

        // Assert
        assertFalse(result);
        verify(paymentDetailsRepository, never()).deleteById(detailsId);
    }

    @Test
    void updatePaymentDetails_ShouldReturnTrue_WhenPaymentDetailsExist() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);

        UpdatePaymentDetailsRequest request = new UpdatePaymentDetailsRequest();
        request.setId(1L);
        request.setUser(user);
        request.setCvv("123");
        request.setExpirationDate(expDate);
        request.setCardHolderName("Kevinn Jonas");
        request.setCardNumber("2345678998765427");

        PaymentDetailsEntity existingPaymentDetails = PaymentDetailsEntity.builder()
                .id(1L)
                .expirationDate(request.getExpirationDate())
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .cvv(request.getCvv())
                .user(userEntity)
                .build();
        when(paymentDetailsRepository.findById(1L)).thenReturn(Optional.of(existingPaymentDetails));
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);

        boolean result = paymentDetailsService.updatePaymentDetails(request);

        assertTrue(result);
        verify(paymentDetailsRepository, times(1)).save(existingPaymentDetails);
    }

    @Test
    void updatePaymentDetails_shouldReturnFalseWhenDetailsDoNotExist(){
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);

        UpdatePaymentDetailsRequest request = new UpdatePaymentDetailsRequest();
        request.setId(1L);
        request.setUser(user);
        request.setCvv("123");
        request.setExpirationDate(expDate);
        request.setCardHolderName("Kevinn Jonas");
        request.setCardNumber("2345678998765427");

        when(paymentDetailsRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean updateResult = paymentDetailsService.updatePaymentDetails(request);

        // Assert
        assertFalse(updateResult);
        verify(paymentDetailsRepository, never()).save(any(PaymentDetailsEntity.class));

    }

    @ParameterizedTest
    @MethodSource("provideInvalidUpdatePaymentDetailsRequests")
    void updatePaymentDetails_shouldThrowExceptionForInvalidInput(UpdatePaymentDetailsRequest invalidRequest) {

        LocalDate date = LocalDate.of(2014, 9, 16);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);

        assertThrows(InvalidExcursionDataException.class, () -> paymentDetailsService.updatePaymentDetails(invalidRequest));
        verify(paymentDetailsRepository, never()).save(any());
    }

    private static Stream<Arguments> provideInvalidUpdatePaymentDetailsRequests() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity fakeUserEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(fakeUserEntity);

        return Stream.of(
                Arguments.of(new UpdatePaymentDetailsRequest(1L, null, "2345678998765427", "123", expDate, "Kevinn Jonas")),
                Arguments.of(new UpdatePaymentDetailsRequest(1L, user, null, "123", expDate, "Kevinn Jonas")),
                Arguments.of(new UpdatePaymentDetailsRequest(1L, user, "2345678998765427", null, expDate, "Kevinn Jonas")),
                Arguments.of(new UpdatePaymentDetailsRequest(1L, user, "2345678998765427", "123", null, "Kevinn Jonas")),
                Arguments.of(new UpdatePaymentDetailsRequest(1L, user, "2345678998765427", "123", expDate, null))
        );
    }

    @Test
    void updatePaymentDetails_shouldThrowUnauthorizedAccessExceptionForInvalidUser() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);

        UpdatePaymentDetailsRequest request = new UpdatePaymentDetailsRequest();
        request.setId(1L);
        request.setUser(user);
        request.setCvv("123");
        request.setExpirationDate(expDate);
        request.setCardHolderName("Kevinn Jonas");
        request.setCardNumber("2345678998765427");

        PaymentDetailsEntity existingPaymentDetails = PaymentDetailsEntity.builder()
                .id(1L)
                .expirationDate(request.getExpirationDate())
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .cvv(request.getCvv())
                .user(userEntity)
                .build();
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(false);
        when(accessToken.getUserID()).thenReturn(100L);

        when(paymentDetailsRepository.findById(request.getId())).thenReturn(Optional.of(existingPaymentDetails));

        assertThrows(UnauthorizedDataAccessException.class, () -> paymentDetailsService.updatePaymentDetails(request));
        verify(paymentDetailsRepository, never()).save(any());
    }


}