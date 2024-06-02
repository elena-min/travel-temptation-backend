package org.individualproject.business;

import org.individualproject.business.converter.PaymentDetailsConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.InvalidExcursionDataException;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.PaymentDetailsRepository;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentDetailsService {
    private PaymentDetailsRepository paymentDetailsRepository;
    private AccessToken requestAccessToken;
    @Autowired
    public PaymentDetailsService(PaymentDetailsRepository paymentDRepository){
        this.paymentDetailsRepository = paymentDRepository;
    }
    public List<PaymentDetails> getAllPaymentDetails() {
        List<PaymentDetailsEntity> paymentDetailsEntities = paymentDetailsRepository.findAll();
        return PaymentDetailsConverter.mapToDomainList(paymentDetailsEntities);
    }
    public Optional<PaymentDetails> getPaymentDetails(Long id) {

        Optional<PaymentDetailsEntity> paymentDetailsOptional = paymentDetailsRepository.findById(id);

        if (paymentDetailsOptional.isPresent()) {
            PaymentDetailsEntity paymentDetailsEntity = paymentDetailsOptional.get();

            if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
                if (!requestAccessToken.getUserID().equals(paymentDetailsEntity.getUser().getId())) {
                    throw new UnauthorizedDataAccessException("UNAUTHORIZED_ACCESS");
                }
            }

            return paymentDetailsOptional.map(PaymentDetailsConverter::mapToDomain);
        } else {
            throw new NotFoundException("Payment details not found.");
        }
    }

    public PaymentDetails createPaymentDetails(CreatePaymentDetailsRequest request){
        if (request.getCardHolderName() == null || request.getCardNumber() == null || request.getCvv() == null ||
                request.getUser() == null || request.getExpirationDate() == null) {
            throw new InvalidExcursionDataException("Invalid input data");
        }
        UserEntity userEntity = UserConverter.convertToEntity(request.getUser());
        PaymentDetailsEntity newPaymentDetails = PaymentDetailsEntity.builder()
                .user(userEntity)
                .expirationDate(request.getExpirationDate())
                .cardNumber(request.getCardNumber())
                .cardHolderName(request.getCardHolderName())
                .cvv(request.getCvv())
                .build();

        PaymentDetailsEntity paymentDetailsEntity = paymentDetailsRepository.save(newPaymentDetails);
        return PaymentDetailsConverter.mapToDomain(paymentDetailsEntity);
    }

    public boolean deletePaymentDetails(Long id) {
        Optional<PaymentDetailsEntity> paymentDetailsOptional = paymentDetailsRepository.findById(id);
        if (paymentDetailsOptional.isPresent()) {
            PaymentDetailsEntity paymentDetailsEntity = paymentDetailsOptional.get();

            if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
                if (!requestAccessToken.getUserID().equals(paymentDetailsEntity.getUser().getId())) {
                    throw new UnauthorizedDataAccessException("UNAUTHORIZED_ACCESS");
                }
            }

            paymentDetailsRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePaymentDetails(UpdatePaymentDetailsRequest request) {
        UserEntity userEntity = UserConverter.convertToEntity(request.getUser());
        Optional<PaymentDetailsEntity> optionalPaymentDetails = paymentDetailsRepository.findById(request.getId());
        if (optionalPaymentDetails.isPresent()) {
            PaymentDetailsEntity existingPaymentDetails = optionalPaymentDetails.get();

            if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
                if (!requestAccessToken.getUserID().equals(existingPaymentDetails.getUser().getId())) {
                    throw new UnauthorizedDataAccessException("USER_ID_NOT_FROM_LOGGED_IN_USER");
                }
            }
            existingPaymentDetails.setId(request.getId());
            existingPaymentDetails.setUser(userEntity);
            existingPaymentDetails.setCvv(request.getCvv());
            existingPaymentDetails.setExpirationDate(request.getExpirationDate());
            existingPaymentDetails.setCardHolderName(request.getCardHolderName());
            existingPaymentDetails.setCardNumber(request.getCardNumber());
            existingPaymentDetails.setId(request.getId());


            paymentDetailsRepository.save(existingPaymentDetails);
            return true;
        } else {
            return false;
        }
    }
}
