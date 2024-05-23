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
import java.util.stream.Collectors;

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
        try {
            Optional<ReviewEntity> reviewEntity = reviewRepository.findById(id);
            if(reviewEntity.isPresent()){

                ReviewEntity review = reviewEntity.get();
                reviewRepository.deleteById(id);
               return true;


            }else{
                throw new NotFoundException("Review not found.");
            }

        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public List<Review> getReviewsByUser(User user) {
        if (!accessToken.hasRole(UserRole.ADMIN.name())) {
            if (accessToken.getUserID() != user.getId()) {
                throw new UnauthorizedDataAccessException("USER_ID_NOT_FROM_LOGGED_IN_USER");
            }
        }
        UserEntity userEntity = UserConverter.convertToEntity(user);
        List<ReviewEntity> reviewEntities = reviewRepository.findByUserWriter(userEntity);
        return reviewEntities.stream().map(ReviewConverter::mapToDomain).collect(Collectors.toList());
    }

}
