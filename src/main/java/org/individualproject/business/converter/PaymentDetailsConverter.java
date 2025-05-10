package org.individualproject.business.converter;

import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;

import java.util.List;

public class PaymentDetailsConverter {
    public static PaymentDetails mapToDomain(PaymentDetailsEntity paymentDetailsEntity) {
        UserEntity userEntity = paymentDetailsEntity.getUser();
        User user = UserConverter.mapToDomain(userEntity);
        return PaymentDetails.builder()
                .id(paymentDetailsEntity.getId())
                .user(user)
                .expirationDate(paymentDetailsEntity.getExpirationDate())
                .cardNumber(paymentDetailsEntity.getCardNumber())
                .cardHolderName(paymentDetailsEntity.getCardHolderName())
                .cvv(paymentDetailsEntity.getCvv())
                .build();
    }
    public static List<PaymentDetails> mapToDomainList(List<PaymentDetailsEntity> paymentDetailsEntities) {
        return paymentDetailsEntities.stream()
                .map(PaymentDetailsConverter::mapToDomain)
                .toList();
    }
    public static PaymentDetailsEntity convertToEntity(PaymentDetails paymentDetails){
        UserEntity userEntity = UserConverter.convertToEntity(paymentDetails.getUser());
        return PaymentDetailsEntity.builder()
                .id(paymentDetails.getId())
                .user(userEntity)
                .cardHolderName(paymentDetails.getCardHolderName())
                .cardNumber(paymentDetails.getCardNumber())
                .cvv(paymentDetails.getCvv())
                .expirationDate(paymentDetails.getExpirationDate())
                .build();
    }


    private PaymentDetailsConverter(){}
}
