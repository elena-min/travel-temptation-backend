package org.individualproject.business.converter;

import org.individualproject.domain.Excursion;
import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.PaymentDetailsEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentDetailsConverter {
    public static PaymentDetails mapToDomain(PaymentDetailsEntity paymentDetailsEntity) {
        User user = UserConverter.mapToDomain(paymentDetailsEntity.getUser());
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .id(paymentDetailsEntity.getId())
                .user(user)
                .expirationDate(paymentDetailsEntity.getExpirationDate())
                .cardNumber(paymentDetailsEntity.getCardNumber())
                .cardHolderName(paymentDetailsEntity.getCardHolderName())
                .cvv(paymentDetailsEntity.getCvv())
                .build();
        return paymentDetails;
    }
    public static List<PaymentDetails> mapToDomainList(List<PaymentDetailsEntity> paymentDetailsEntities) {
        return paymentDetailsEntities.stream()
                .map(PaymentDetailsConverter::mapToDomain)
                .toList();
    }

    private PaymentDetailsConverter(){}
}
