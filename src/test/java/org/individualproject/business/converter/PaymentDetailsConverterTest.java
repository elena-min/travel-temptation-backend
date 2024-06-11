package org.individualproject.business.converter;

import org.individualproject.domain.Excursion;
import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDetailsConverterTest {

    @Test
    void mapToDomain() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .hashedPassword("asdfgh")
                .gender(Gender.MALE)
                .build();

        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsEntity.builder()
                .id(1L)
                .user(userEntity)
                .cvv("234")
                .cardHolderName("Nick Jonas")
                .cardNumber("1234567890123456")
                .expirationDate(expDate)
                .build();

        PaymentDetails paymentDetails = PaymentDetailsConverter.mapToDomain(paymentDetailsEntity);

        //Assert
        User expectedUser= UserConverter.mapToDomain(paymentDetailsEntity.getUser());
        assertEquals(paymentDetailsEntity.getId(), paymentDetails.getId());
        assertEquals(expectedUser, paymentDetails.getUser());
        assertEquals(paymentDetailsEntity.getCvv(), paymentDetails.getCvv());
        assertEquals(paymentDetailsEntity.getCardHolderName(), paymentDetails.getCardHolderName());
        assertEquals(paymentDetailsEntity.getCardNumber(), paymentDetails.getCardNumber());
        assertEquals(paymentDetailsEntity.getExpirationDate(), paymentDetails.getExpirationDate());
    }

    @Test
    void mapToDomainList() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .hashedPassword("asdfgh")
                .gender(Gender.MALE)
                .build();

        List<PaymentDetailsEntity> paymentDetailsEntityListEntityList = new ArrayList<>();

        PaymentDetailsEntity paymentDetailsEntity1 = PaymentDetailsEntity.builder()
                .id(1L)
                .user(userEntity)
                .cvv("234")
                .cardHolderName("Nick Jonas")
                .cardNumber("1234567890123456")
                .expirationDate(expDate)
                .build();

        PaymentDetailsEntity paymentDetailsEntity2 = PaymentDetailsEntity.builder()
                .id(2L)
                .user(userEntity)
                .cvv("986")
                .cardHolderName("Joe Jonas")
                .cardNumber("1234567890198456")
                .expirationDate(expDate)
                .build();

        paymentDetailsEntityListEntityList.add(paymentDetailsEntity1);
        paymentDetailsEntityListEntityList.add(paymentDetailsEntity2);

        //Act
        List<PaymentDetails> paymentDetails = PaymentDetailsConverter.mapToDomainList(paymentDetailsEntityListEntityList);

        //Assert
        User expectedUser= UserConverter.mapToDomain(paymentDetailsEntity1.getUser());

        assertEquals(paymentDetailsEntity1.getId(), paymentDetails.get(0).getId());
        assertEquals(expectedUser, paymentDetails.get(0).getUser());
        assertEquals(paymentDetailsEntity1.getCvv(), paymentDetails.get(0).getCvv());
        assertEquals(paymentDetailsEntity1.getCardHolderName(), paymentDetails.get(0).getCardHolderName());
        assertEquals(paymentDetailsEntity1.getCardNumber(), paymentDetails.get(0).getCardNumber());
        assertEquals(paymentDetailsEntity1.getExpirationDate(), paymentDetails.get(0).getExpirationDate());

        assertEquals(paymentDetailsEntity2.getId(), paymentDetails.get(1).getId());
        assertEquals(expectedUser, paymentDetails.get(1).getUser());
        assertEquals(paymentDetailsEntity2.getCvv(), paymentDetails.get(1).getCvv());
        assertEquals(paymentDetailsEntity2.getCardHolderName(), paymentDetails.get(1).getCardHolderName());
        assertEquals(paymentDetailsEntity2.getCardNumber(), paymentDetails.get(1).getCardNumber());
        assertEquals(paymentDetailsEntity2.getExpirationDate(), paymentDetails.get(1).getExpirationDate());
    }

    @Test
    void convertToEntity(){
        LocalDate date = LocalDate.of(2014, 9, 16);
        YearMonth expDate = YearMonth.of(2027, 9);
        User user = User.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .hashedPassword("asdfgh")
                .gender(Gender.MALE)
                .build();

        PaymentDetails paymentDetails = PaymentDetails.builder()
                .id(1L)
                .user(user)
                .cvv("234")
                .cardHolderName("Nick Jonas")
                .cardNumber("1234567890123456")
                .expirationDate(expDate)
                .build();

        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsConverter.convertToEntity(paymentDetails);
        UserEntity expectedUser = UserConverter.convertToEntity(paymentDetails.getUser());

        //Assert
        assertEquals(paymentDetails.getId(), paymentDetailsEntity.getId());
        assertEquals(paymentDetails.getCardHolderName(), paymentDetailsEntity.getCardHolderName());
        assertEquals(paymentDetails.getCvv(), paymentDetailsEntity.getCvv());
        assertEquals(paymentDetails.getExpirationDate(), paymentDetailsEntity.getExpirationDate());
        assertEquals(expectedUser, paymentDetailsEntity.getUser());
        assertEquals(paymentDetails.getCardNumber(), paymentDetailsEntity.getCardNumber());
    }
}