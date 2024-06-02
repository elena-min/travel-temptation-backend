package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.converter.*;
import org.individualproject.business.exception.InvalidExcursionDataException;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.entity.*;
import org.individualproject.persistence.ReviewRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReviewService {

    private ReviewRepository reviewRepository;
    private AccessToken accessToken;

    public List<Review> getReviews() {
        List<ReviewEntity> reviewEntities = reviewRepository.findAll();
        return ReviewConverter.mapToDomainList(reviewEntities);
    }

    public Optional<Review> getReview(Long id) {
        Optional<ReviewEntity> reviewEntity = reviewRepository.findById(id);
        return reviewEntity.map(ReviewConverter::mapToDomain);
    }

    public Review createReview(CreateReviewRequest createReviewRequest) {
        if (createReviewRequest.getNumberOfStars() < 0 || createReviewRequest.getReviewDate() == null || createReviewRequest.getTravelAgency() == null ||
                createReviewRequest.getUserWriter() == null || createReviewRequest.getDescription() == null || createReviewRequest.getTitle() == null) {
            throw new InvalidExcursionDataException("Invalid input data");
        }
        UserEntity userEntity = UserConverter.convertToEntity(createReviewRequest.getUserWriter());
        UserEntity travelAgencyEntity = UserConverter.convertToEntity(createReviewRequest.getTravelAgency());

        ReviewEntity reviewEntity = ReviewEntity.builder()
                .title(createReviewRequest.getTitle())
                .description(createReviewRequest.getDescription())
                .reviewDate(createReviewRequest.getReviewDate())
                .numberOfStars(createReviewRequest.getNumberOfStars())
                .travelAgency(travelAgencyEntity)
                .userWriter(userEntity)
                .build();

        ReviewEntity reviewEntity1 = reviewRepository.save(reviewEntity);


        return ReviewConverter.mapToDomain(reviewEntity1);
    }

    public boolean deleteReview(Long id) {

        Optional<ReviewEntity> reviewEntityOptional = reviewRepository.findById(id);
        if(reviewEntityOptional.isPresent()){

            ReviewEntity reviewEntity = reviewEntityOptional.get();

            if (!accessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
                if (!accessToken.getUserID().equals(reviewEntity.getUserWriter().getId())) {
                    throw new UnauthorizedDataAccessException("UNAUTHORIZED_ACCESS");
                }
            }
            reviewRepository.deleteById(id);
            return true;

        }else{
            return false;
        }

    }

    public List<Review> getReviewsByUser(User user) {
        UserEntity userEntity = UserConverter.convertToEntity(user);
        List<ReviewEntity> reviewEntities = reviewRepository.findByUserWriter(userEntity);
        return reviewEntities.stream().map(ReviewConverter::mapToDomain).toList();
    }

    public List<Review> getReviewsByTravelAgency(User travelAgency) {
        UserEntity userEntity = UserConverter.convertToEntity(travelAgency);
        List<ReviewEntity> reviewEntities = reviewRepository.findByTravelAgency(userEntity);
        return reviewEntities.stream().map(ReviewConverter::mapToDomain).toList();
    }

}
