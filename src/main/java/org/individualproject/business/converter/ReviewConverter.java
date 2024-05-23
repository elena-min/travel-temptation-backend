package org.individualproject.business.converter;

import org.individualproject.domain.PaymentDetails;
import org.individualproject.domain.Review;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.PaymentDetailsEntity;
import org.individualproject.persistence.entity.ReviewEntity;
import org.individualproject.persistence.entity.UserEntity;

import java.util.List;

public class ReviewConverter {
    public static Review mapToDomain(ReviewEntity reviewEntity) {
        UserEntity userEntity = reviewEntity.getUserWriter();
        UserEntity travelEntityEntity = reviewEntity.getTravelAgency();

        User user = UserConverter.mapToDomain(userEntity);
        User travelAgency = UserConverter.mapToDomain(travelEntityEntity);

        return Review.builder()
                .id(reviewEntity.getId())
                .userWriter(user)
                .reviewDate(reviewEntity.getReviewDate())
                .title(reviewEntity.getTitle())
                .description(reviewEntity.getDescription())
                .numberOfStars(reviewEntity.getNumberOfStars())
                .travelAgency(travelAgency)
                .build();
    }
    public static List<Review> mapToDomainList(List<ReviewEntity> reviewEntities) {
        return reviewEntities.stream()
                .map(ReviewConverter::mapToDomain)
                .toList();
    }
    public static ReviewEntity convertToEntity(Review review){
        UserEntity userEntity = UserConverter.convertToEntity(review.getUserWriter());
        UserEntity travelAgencyEntity = UserConverter.convertToEntity(review.getTravelAgency());
        return ReviewEntity.builder()
                .id(review.getId())
                .title(review.getTitle())
                .description(review.getDescription())
                .reviewDate(review.getReviewDate())
                .numberOfStars(review.getNumberOfStars())
                .travelAgency(travelAgencyEntity)
                .userWriter(userEntity)
                .build();
    }

    private ReviewConverter(){}

}
