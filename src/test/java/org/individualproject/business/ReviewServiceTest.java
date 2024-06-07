package org.individualproject.business;

import org.individualproject.business.converter.ReviewConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.InvalidExcursionDataException;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.Gender;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.ReviewRepository;
import org.individualproject.persistence.entity.ReviewEntity;
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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AccessToken accessToken;

    @InjectMocks
    private ReviewService reviewService;
    @Test
    void getReviews_shouldReturnListOfReviews() {
        // Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date date2 = new Date();
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("JohnJohn").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        UserEntity travelAgencyEntity = UserEntity.builder().id(2L).firstName("Global").lastName("Adventure").birthDate(date).email("global@example.com").username("global123").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();

        List<ReviewEntity> allReviewsEntities = Arrays.asList(
                ReviewEntity.builder().id(1L).reviewDate(date2).title("Title1").description("Desc123").numberOfStars(5).userWriter(userEntity).travelAgency(travelAgencyEntity).build(),
                ReviewEntity.builder().id(2L).reviewDate(date2).title("Title2").description("Desc456").numberOfStars(3).userWriter(userEntity).travelAgency(travelAgencyEntity).build()
        );

        List<Review> reviewList = ReviewConverter.mapToDomainList(allReviewsEntities);

        when(reviewRepository.findAll()).thenReturn(allReviewsEntities);

        // Act
        List<Review> result = reviewService.getReviews();

        // Assert
        assertEquals(reviewList, result);
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void getReviews_returnsEmptyList() {
        // Arrange
        when(reviewRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        List<Review> result = reviewService.getReviews();

        //Assert
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void getReview_shouldReturnReview() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date date2 = new Date();
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("JohnJohn").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        UserEntity travelAgencyEntity = UserEntity.builder().id(2L).firstName("Global").lastName("Adventure").birthDate(date).email("global@example.com").username("global123").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();

        ReviewEntity reviewEntity = ReviewEntity.builder()
                .id(1L)
                .reviewDate(date2)
                .title("Title1")
                .description("Desc123")
                .numberOfStars(5)
                .userWriter(userEntity)
                .travelAgency(travelAgencyEntity).build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewEntity));
        Optional<Review> result = reviewService.getReview(1L);

        verify(reviewRepository).findById(1L);
        assertEquals(reviewEntity.getId(), result.get().getId());
        assertEquals(reviewEntity.getReviewDate(), result.get().getReviewDate());
        assertEquals(reviewEntity.getTitle(), result.get().getTitle());
        assertEquals(reviewEntity.getDescription(), result.get().getDescription());
        assertEquals(reviewEntity.getNumberOfStars(), result.get().getNumberOfStars());

    }
    @Test
    void getReview_nonExistingReview() {
        Long id = 1L;
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Review> result = reviewService.getReview(1L);
        assertTrue(result.isEmpty());
        verify(reviewRepository, times(1)).findById(id);
    }
    @Test
    void createReview() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date date2 = new Date();
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("JohnJohn").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        UserEntity travelAgencyEntity = UserEntity.builder().id(2L).firstName("Global").lastName("Adventure").birthDate(date).email("global@example.com").username("global123").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();

        User user = UserConverter.mapToDomain(userEntity);
        User travelAgency = UserConverter.mapToDomain(travelAgencyEntity);

        CreateReviewRequest request = new CreateReviewRequest();
        request.setReviewDate(date2);
        request.setTitle("Title1");
        request.setDescription("Description");
        request.setNumberOfStars(4);
        request.setUserWriter(user);
        request.setTravelAgency(travelAgency);

        ReviewEntity savedReviewEntity = ReviewEntity.builder()
                .id(1L)
                .reviewDate(request.getReviewDate())
                .title(request.getTitle())
                .description(request.getDescription())
                .numberOfStars(request.getNumberOfStars())
                .userWriter(userEntity)
                .travelAgency(travelAgencyEntity).build();

        when(reviewRepository.save(any())).thenReturn(savedReviewEntity);

        Review result = reviewService.createReview(request);

        verify(reviewRepository).save(any());
        assertEquals(savedReviewEntity.getId(), result.getId());
        assertEquals(savedReviewEntity.getReviewDate(), result.getReviewDate());
        assertEquals(savedReviewEntity.getTitle(), result.getTitle());
        assertEquals(savedReviewEntity.getDescription(), result.getDescription());
        assertEquals(savedReviewEntity.getNumberOfStars(), result.getNumberOfStars());
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsParams")
    void createReview_shouldThrowExceptionForInvalidInput(CreateReviewRequest invalidRequest) {
        assertThrows(InvalidExcursionDataException.class, () -> reviewService.createReview(invalidRequest));
        verify(reviewRepository, never()).save(any());
    }

    private static Stream<Arguments> provideStringsForIsParams() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date date2 = new Date();
        User user = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").username("JohnJohn").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User travelAgency = User.builder().id(2L).firstName("Global").lastName("Adventure").birthDate(date).email("global@example.com").username("global123").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();

        return Stream.of(
                Arguments.of(new CreateReviewRequest(null,user, date2, 4, "Title", "Descirption")),
                Arguments.of(new CreateReviewRequest(travelAgency, null, date2, 4, "Title", "Descirption")),
                Arguments.of(new CreateReviewRequest(travelAgency, user,  null, 4, "Title", "Descirption")),
                Arguments.of(new CreateReviewRequest(travelAgency, user, date2, -1, "Title", "Descirption")),
                Arguments.of(new CreateReviewRequest(travelAgency, user, date2, 4, null, "Descirption")),
                Arguments.of(new CreateReviewRequest(travelAgency, user, date2, 4, "Title", null))

        );
    }
    @Test
    void deleteReview_shouldDeleteExistingReview() {
        Long id = 1L;
        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setId(id);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        reviewEntity.setUserWriter(userEntity);
        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewEntity));
        when(accessToken.getUserID()).thenReturn(1L);

        // Act
        boolean result = reviewService.deleteReview(id);

        // Assert
        assertTrue(result);
        verify(reviewRepository, times(1)).deleteById(id);
    }
    @Test
    void deleteReview_shouldThrowUnauthorizedAccessExceptionWhenRoleButNotOwner() {
        Long id = 1L;
        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setId(id);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L);
        reviewEntity.setUserWriter(userEntity);
        when(reviewRepository.findById(id)).thenReturn(Optional.of(reviewEntity));
        when(accessToken.getUserID()).thenReturn(3L);

        assertThrows(UnauthorizedDataAccessException.class, () -> reviewService.deleteReview(id));
        verify(reviewRepository, never()).deleteById(id);
    }
    @Test
    void deleteReview_shouldThrowNotFoundExceptionWhenReviewNotFound() {
        Long id = 1L;
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        boolean result = reviewService.deleteReview(id);

        // Assert
        assertFalse(result);
        verify(reviewRepository, never()).deleteById(id);
    }
    @Test
    void getReviewsByUser_shouldReturnReviewsForUser() {
        // Arrange
        Date date = new Date(2024, 9, 16);

        User user = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com", "johndoe", "hashedPassword", Gender.MALE);
        UserEntity userEntity = UserConverter.convertToEntity(user);


        User travelagency = new User(2L, "Global", "Trips", LocalDate.of(1990, 1, 1), "global@example.com", "globalTrips", "hashedPassword", Gender.MALE);
        UserEntity travelagencyEntity = UserConverter.convertToEntity(travelagency);

        List<ReviewEntity> reviewEntities = Arrays.asList(
                new ReviewEntity(1L, travelagencyEntity, userEntity, date, 4, "Wow","Great experience" ),
                new ReviewEntity(2L, travelagencyEntity, userEntity, date, 4, "Wow2","Some review")
        );

        when(reviewRepository.findByUserWriter(userEntity)).thenReturn(reviewEntities);

        // Act
        List<Review> result = reviewService.getReviewsByUser(user);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Great experience", result.get(0).getDescription());
        assertEquals("Some review", result.get(1).getDescription());
        assertEquals("Wow", result.get(0).getTitle());
        assertEquals("Wow2", result.get(1).getTitle());
        assertEquals(user, result.get(0).getUserWriter());
        assertEquals(user, result.get(1).getUserWriter());
        assertEquals(travelagency, result.get(0).getTravelAgency());
        assertEquals(travelagency, result.get(1).getTravelAgency());
        assertEquals(4, result.get(0).getNumberOfStars());
        assertEquals(4, result.get(1).getNumberOfStars());
    }

    @Test
    void getReviewsByTravelAgency_shouldReturnReviewsForTravelAgency() {
        // Arrange
        Date date = new Date(2024, 9, 16);

        User user = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john@example.com", "johndoe", "hashedPassword", Gender.MALE);
        UserEntity userEntity = UserConverter.convertToEntity(user);


        User travelagency = new User(2L, "Global", "Trips", LocalDate.of(1990, 1, 1), "global@example.com", "globalTrips", "hashedPassword", Gender.MALE);
        UserEntity travelagencyEntity = UserConverter.convertToEntity(travelagency);

        List<ReviewEntity> reviewEntities = Arrays.asList(
                new ReviewEntity(1L, travelagencyEntity, userEntity, date, 4, "Wow","Great experience" ),
                new ReviewEntity(2L, travelagencyEntity, userEntity, date, 4, "Wow2","Excellent service")
        );

        when(reviewRepository.findByTravelAgency(travelagencyEntity)).thenReturn(reviewEntities);

        // Act
        List<Review> result = reviewService.getReviewsByTravelAgency(travelagency);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Great experience", result.get(0).getDescription());
        assertEquals("Excellent service", result.get(1).getDescription());
        assertEquals("Wow", result.get(0).getTitle());
        assertEquals("Wow2", result.get(1).getTitle());
        assertEquals(user, result.get(0).getUserWriter());
        assertEquals(user, result.get(1).getUserWriter());
        assertEquals(travelagency, result.get(0).getTravelAgency());
        assertEquals(travelagency, result.get(1).getTravelAgency());
        assertEquals(4, result.get(0).getNumberOfStars());
        assertEquals(4, result.get(1).getNumberOfStars());
    }
}