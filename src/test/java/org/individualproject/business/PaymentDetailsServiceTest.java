package org.individualproject.business;

import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.PaymentDetailsConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.InvalidExcursionDataException;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.BookingRepository;
import org.individualproject.persistence.PaymentDetailsRepository;
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
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentDetailsServiceTest {
    @Mock
    private PaymentDetailsRepository paymentDetailsRepository;

    @InjectMocks
    private PaymentDetailsService paymentDetailsService;

    @Test
    void getAllPaymentDetails_returnsAllPaymentDetails() {
        // Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

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
        LocalDate expDate = LocalDate.of(2027, 9, 16);
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();

        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                .id(1L)
                .expirationDate(expDate)
                .cvv("123")
                .cardNumber("1234567890123456")
                .cardHolderName("Nick Jonas")
                .user(userEntity)
                .build();
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
        LocalDate expDate = LocalDate.of(2027, 9, 16);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);
        CreatePaymentDetailsRequest request = new CreatePaymentDetailsRequest();
        request.setUser(user);
        request.setExpirationDate(expDate);
        request.setCardNumber("1234567890123456");
        request.setCardHolderName("Nick Jonas");
        request.setCvv("123");

        // Mocking the repository behavior
        PaymentDetailsEntity savedPaymentDetailsEntity = PaymentDetailsEntity.builder()
                .id(1L)
                .expirationDate(request.getExpirationDate())
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .cvv(request.getCvv())
                .user(userEntity)
                .build();
        when(paymentDetailsRepository.save(any())).thenReturn(savedPaymentDetailsEntity);

        // Calling the method under test
        PaymentDetails result = paymentDetailsService.createPaymentDetails(request);

        // Verifying that the repository method was called with the correct argument
        verify(paymentDetailsRepository).save(any());

        // Asserting the result
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
        LocalDate expDate = LocalDate.of(2027, 9, 16);
        User validUser = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "hashedPassword1", Gender.MALE);

        return Stream.of(
                Arguments.of(new CreatePaymentDetailsRequest(null, "1234567890123456", "234", expDate, "Nick Jonas")),
                Arguments.of(new CreatePaymentDetailsRequest(validUser, null, "234", expDate, "Nick Jonas")),
                Arguments.of(new CreatePaymentDetailsRequest(validUser, "1234567890123456", "232344", null, "Nick Jonas")),
                Arguments.of(new CreatePaymentDetailsRequest(validUser, "1234567890123456", "234", expDate, null))
        );
    }

    @Test
    void deletePaymentDetails_shouldDeleteExistingPaymentDetails() {
        Long id = 1L;
        Mockito.doNothing().when(paymentDetailsRepository).deleteById(id);

        // Act
        boolean result = paymentDetailsService.deletePaymentDetails(id);

        // Assert
        assertTrue(result);
        verify(paymentDetailsRepository, times(1)).deleteById(id);
    }

    @Test
    void deletePaymentDetails_nonExistingPaymentDetails(){
        Long nonExistingId = 9987L;
        doThrow(EmptyResultDataAccessException.class).when(paymentDetailsRepository).deleteById(nonExistingId);

        // Act
        boolean result = paymentDetailsService.deletePaymentDetails(nonExistingId);

        // Assert
        assertFalse(result);
        verify(paymentDetailsRepository, times(1)).deleteById(nonExistingId);
    }

    @Test
    void updatePaymentDetails_ShouldReturnTrue_WhenPaymentDetailsExist() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

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

        PaymentDetailsService paymentDetailsService = new PaymentDetailsService(paymentDetailsRepository);
        boolean result = paymentDetailsService.updatePaymentDetails(request);

        assertTrue(result);
        verify(paymentDetailsRepository, times(1)).save(existingPaymentDetails);
    }

    @Test
    void updatePaymentDetails_shouldReturnFalseWhenDetailsDoNotExist(){
        LocalDate date = LocalDate.of(2014, 9, 16);
        LocalDate expDate = LocalDate.of(2027, 9, 16);

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

        // Act:
        boolean updateResult = paymentDetailsService.updatePaymentDetails(request);

        // Assert
        assertFalse(updateResult);
        verify(paymentDetailsRepository, never()).save(any(PaymentDetailsEntity.class));

    }

}