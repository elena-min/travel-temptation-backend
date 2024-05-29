package org.individualproject.business.converter;

import org.individualproject.domain.*;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewConverterTest {

    @Test
    void mapToDomain() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .hashedPassword("asdfgh")
                .gender(Gender.MALE)
                .build();

        UserEntity travelAgencyEntity = UserEntity.builder()
                .id(2L)
                .firstName("Global")
                .lastName("Adventurws")
                .birthDate(date)
                .email("global@gmail.com")
                .username("globalAdv")
                .hashedPassword("1234")
                .gender(Gender.OTHER)
                .build();

        ReviewEntity reviewEntity = ReviewEntity.builder()
                .id(1L)
                .reviewDate(new Date())
                .title("Review title")
                .userWriter(userEntity)
                .travelAgency(travelAgencyEntity)
                .numberOfStars(3)
                .description("Somethingekjf")
                .build();


        Review review = ReviewConverter.mapToDomain(reviewEntity);

        //Assert
        User expectedUser= UserConverter.mapToDomain(reviewEntity.getUserWriter());
        User expectedTravelAgency= UserConverter.mapToDomain(reviewEntity.getTravelAgency());
        assertEquals(reviewEntity.getId(), review.getId());
        assertEquals(expectedUser, review.getUserWriter());
        assertEquals(expectedTravelAgency, review.getTravelAgency());
        assertEquals(reviewEntity.getReviewDate(), review.getReviewDate());
        assertEquals(reviewEntity.getTitle(), review.getTitle());
        assertEquals(reviewEntity.getDescription(), review.getDescription());
        assertEquals(reviewEntity.getNumberOfStars(), review.getNumberOfStars());
    }

    @Test
    void mapToDomainList() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .hashedPassword("asdfgh")
                .gender(Gender.MALE)
                .build();

        UserEntity travelAgencyEntity = UserEntity.builder()
                .id(2L)
                .firstName("Global")
                .lastName("Adventurws")
                .birthDate(date)
                .email("global@gmail.com")
                .username("globalAdv")
                .hashedPassword("1234")
                .gender(Gender.OTHER)
                .build();

        ReviewEntity reviewEntity1 = ReviewEntity.builder()
                .id(1L)
                .reviewDate(new Date())
                .title("Review title")
                .userWriter(userEntity)
                .travelAgency(travelAgencyEntity)
                .numberOfStars(3)
                .description("Somethingekjf")
                .build();

        ReviewEntity reviewEntity2 = ReviewEntity.builder()
                .id(2L)
                .reviewDate(new Date())
                .title("Review title2")
                .userWriter(userEntity)
                .travelAgency(travelAgencyEntity)
                .numberOfStars(5)
                .description("Somethingekjf2")
                .build();

        List<ReviewEntity> reviewEntityArrayList = new ArrayList<>();
        reviewEntityArrayList.add(reviewEntity1);
        reviewEntityArrayList.add(reviewEntity2);

        //Act
        List<Review> reviewList = ReviewConverter.mapToDomainList(reviewEntityArrayList);

        //Assert
        User expectedUser= UserConverter.mapToDomain(reviewEntity1.getUserWriter());
        User expectedTravelAgency= UserConverter.mapToDomain(reviewEntity1.getTravelAgency());
        assertEquals(reviewEntity1.getId(), reviewList.get(0).getId());
        assertEquals(expectedUser, reviewList.get(0).getUserWriter());
        assertEquals(expectedTravelAgency, reviewList.get(0).getTravelAgency());
        assertEquals(reviewEntity1.getReviewDate(), reviewList.get(0).getReviewDate());
        assertEquals(reviewEntity1.getTitle(), reviewList.get(0).getTitle());
        assertEquals(reviewEntity1.getDescription(), reviewList.get(0).getDescription());
        assertEquals(reviewEntity1.getNumberOfStars(), reviewList.get(0).getNumberOfStars());

        assertEquals(reviewEntity2.getId(), reviewList.get(1).getId());
        assertEquals(expectedUser, reviewList.get(1).getUserWriter());
        assertEquals(expectedTravelAgency, reviewList.get(1).getTravelAgency());
        assertEquals(reviewEntity2.getReviewDate(), reviewList.get(1).getReviewDate());
        assertEquals(reviewEntity2.getTitle(), reviewList.get(1).getTitle());
        assertEquals(reviewEntity2.getDescription(), reviewList.get(1).getDescription());
        assertEquals(reviewEntity2.getNumberOfStars(), reviewList.get(1).getNumberOfStars());

    }

    @Test
    void convertToEntity() {
        LocalDate date = LocalDate.of(2014, 9, 16);

        User user = User.builder()
                .id(1L)
                .firstName("Nick")
                .lastName("Jonas")
                .birthDate(date)
                .email("nickJonas@gmail.com")
                .hashedPassword("asdfgh")
                .gender(Gender.MALE)
                .build();

        User travelAgency = User.builder()
                .id(2L)
                .firstName("Global")
                .lastName("Adventurws")
                .birthDate(date)
                .email("global@gmail.com")
                .username("globalAdv")
                .hashedPassword("1234")
                .gender(Gender.OTHER)
                .build();

        Review review = Review.builder()
                .id(1L)
                .reviewDate(new Date())
                .title("Review title")
                .userWriter(user)
                .travelAgency(travelAgency)
                .numberOfStars(3)
                .description("Somethingekjf")
                .build();

        ReviewEntity reviewEntity = ReviewConverter.convertToEntity(review);
        UserEntity expectedUser = UserConverter.convertToEntity(review.getUserWriter());
        UserEntity expectedTravelAgency = UserConverter.convertToEntity(review.getTravelAgency());

        //Assert
        assertEquals(review.getId(), reviewEntity.getId());
        assertEquals(expectedUser, reviewEntity.getUserWriter());
        assertEquals(expectedTravelAgency, reviewEntity.getTravelAgency());
        assertEquals(review.getReviewDate(), reviewEntity.getReviewDate());
        assertEquals(review.getTitle(), reviewEntity.getTitle());
        assertEquals(review.getDescription(), reviewEntity.getDescription());
        assertEquals(review.getNumberOfStars(), reviewEntity.getNumberOfStars());
    }
}