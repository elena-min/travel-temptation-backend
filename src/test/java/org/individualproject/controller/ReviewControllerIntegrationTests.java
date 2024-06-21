package org.individualproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.individualproject.TravelApplication;
import org.individualproject.business.converter.ReviewConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.CreateReviewRequest;
import org.individualproject.domain.Review;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.ReviewRepository;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.ReviewEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TravelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ReviewControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccessToken requestAccessToken;

    private User travelAgency;

    private User user;
    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        userRepository.deleteAll();
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setBirthDate(LocalDate.of(1990, 1, 1));
        user1.setEmail("john.doe@example.com");
        user1.setHashedPassword("hashedPassword");
        user1.setGender(Gender.MALE);
        user1.setUsername("johnDoe");
        UserEntity userEntity = UserConverter.convertToEntity(user1);
        userEntity = userRepository.save(userEntity);
        User savedUser= UserConverter.mapToDomain(userEntity);
        user = savedUser;

        User travelAgency1 = new User();
        travelAgency1.setFirstName("Global");
        travelAgency1.setLastName("Adventures");
        travelAgency1.setBirthDate(LocalDate.of(2010, 1, 1));
        travelAgency1.setEmail("global.adv@example.com");
        travelAgency1.setHashedPassword("hashedPassword2");
        travelAgency1.setGender(Gender.MALE);
        travelAgency1.setUsername("global123");
        UserEntity travelAegncyEntity = UserConverter.convertToEntity(travelAgency1);
        travelAegncyEntity = userRepository.save(travelAegncyEntity);
        User savedTravelAgency= UserConverter.mapToDomain(travelAegncyEntity);
        travelAgency = savedTravelAgency;

        when(requestAccessToken.getUserID()).thenReturn(userEntity.getId());

    }
    @Test
    void getReview_ShouldReturnExcursion_WhenReviewExists() throws  Exception{
        Review mockreview = Review.builder()
                .id(1L)
                .reviewDate(new Date())
                .title("Review title")
                .userWriter(user)
                .travelAgency(travelAgency)
                .numberOfStars(3)
                .description("Somethingekjf")
                .build();
        ReviewEntity reviewEntity = ReviewConverter.convertToEntity(mockreview);
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);

       // when(reviewService.getReview(reviewID)).thenReturn(Optional.of(mockreview));
        mockMvc.perform(get("/reviews/{id}", savedReview.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedReview.getId()));

        Optional<ReviewEntity> retrievedReview = reviewRepository.findById(savedReview.getId());
        assertTrue(retrievedReview.isPresent());
        assertEquals("Somethingekjf", retrievedReview.get().getDescription());
        assertEquals("Review title", retrievedReview.get().getTitle());
        assertEquals(3, retrievedReview.get().getNumberOfStars());
        //verify(reviewService).getReview(reviewID);
    }

    @Test
    void getReview_ShouldReturnExcursion_WhenReviewDoesNotExists() throws Exception {
        Long reviewID = 1L;

        //when(reviewService.getReview(reviewID)).thenReturn(Optional.empty());
        mockMvc.perform(get("/reviews/{id}", reviewID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        //verify(reviewService).getReview(reviewID);

    }

    @Test
    void geReviews_ShouldReturnListOfReview() throws Exception{
        Date date2 = new Date();

//        List<Review> mockReview = Arrays.asList(
//                Review.builder().id(1L).reviewDate(date2).title("Title1").description("Desc123").numberOfStars(5).userWriter(mockuser).travelAgency(mockTravelAgency).build(),
//                Review.builder().id(2L).reviewDate(date2).title("Title2").description("Desc456").numberOfStars(3).userWriter(mockuser).travelAgency(mockTravelAgency).build()
//        );

        Review review1 = Review.builder().id(1L).reviewDate(date2).title("Title1").description("Desc123").numberOfStars(5).userWriter(user).travelAgency(travelAgency).build();
        Review review2 = Review.builder().id(2L).reviewDate(date2).title("Title2").description("Desc456").numberOfStars(3).userWriter(user).travelAgency(travelAgency).build();

        ReviewEntity reviewEntity1 = ReviewConverter.convertToEntity(review1);
        ReviewEntity reviewEntity2 = ReviewConverter.convertToEntity(review2);
        reviewRepository.save(reviewEntity1);
        reviewRepository.save(reviewEntity2);
        // when(reviewService.getReviews()).thenReturn(mockReview);
        mockMvc.perform(get("/reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].description").value(reviewEntity1.getDescription()))
                .andExpect(jsonPath("$[0].title").value(reviewEntity1.getTitle()))
                .andExpect(jsonPath("$[1].description").value(reviewEntity2.getDescription()))
                .andExpect(jsonPath("$[1].title").value(reviewEntity2.getTitle()));
        //verify(reviewService).getReviews();
    }

    @Test
    void geReviews_ShouldReturnEmptyListWithNoReview() throws Exception{

        //when(reviewService.getReviews()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/reviews")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        //verify(reviewService).getReviews();
    }

    @Test
    void createReview_ShouldReturnCreatedReview_WhenValidRequest() throws Exception{
        when(requestAccessToken.hasRole(UserRole.USER.name())).thenReturn(true);

        Date date2 = new Date();
        CreateReviewRequest request = new CreateReviewRequest();
        request.setReviewDate(date2);
        request.setTitle("Title1");
        request.setDescription("Description");
        request.setNumberOfStars(4);
        request.setUserWriter(user);
        request.setTravelAgency(travelAgency);


        //when(reviewService.createReview(request)).thenReturn(mockreview);
        mockMvc.perform(post("/reviews")
                        .with(user("username").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Title1"));
        //verify(reviewService).createReview(request);

    }

    @Test
    void createReview_ShouldReturnForbidden_WhenUserDoesNotHaveRole() throws Exception{
        Date date2 = new Date();
        when(requestAccessToken.hasRole(UserRole.USER.name())).thenReturn(false);

        CreateReviewRequest request = new CreateReviewRequest();
        request.setReviewDate(date2);
        request.setTitle("Title1");
        request.setDescription("Description");
        request.setNumberOfStars(4);
        request.setUserWriter(user);
        request.setTravelAgency(travelAgency);

       //when(reviewService.createReview(request)).thenReturn(mockreview);
        mockMvc.perform(post("/reviews"))
                .andExpect(status().isUnauthorized());

       // verify(reviewService, never()).createReview(any());
    }


    @Test
    void deleteReview_Success() throws Exception{
        when(requestAccessToken.hasRole(UserRole.USER.name())).thenReturn(true);

        Review mockreview = Review.builder()
                .id(1L)
                .reviewDate(new Date())
                .title("Review title")
                .userWriter(user)
                .travelAgency(travelAgency)
                .numberOfStars(3)
                .description("Somethingekjf")
                .build();
        ReviewEntity reviewEntity = ReviewConverter.convertToEntity(mockreview);
        ReviewEntity savedReview = reviewRepository.save(reviewEntity);
        //when(reviewService.deleteReview(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/reviews/{id}", savedReview.getId())
                        .with(user("username").roles("USER")))
                .andExpect(status().isOk());

        Optional<ReviewEntity> deletedReview = reviewRepository.findById(savedReview.getId());
        assertFalse(deletedReview.isPresent());


        //verify(reviewService, times(1)).deleteReview(anyLong());
    }
    @Test
    void deleteReview_NotFound() throws Exception {
        //when(requestAccessToken.hasRole(UserRole.USER.name())).thenReturn(true);

        //when(reviewService.deleteReview(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/review/{id}", 1)
                        .with(user("username").roles("USER")))
                .andExpect(status().isNotFound());

        //verify(reviewService, times(0)).deleteReview(anyLong());
    }

    @Test
    void deleteReview_UnauthorizedAccess() throws Exception {
        mockMvc.perform(delete("/reviews/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getReviewsByTravelAgency() throws Exception {
        Date date2 = new Date();

        Review review1 = Review.builder().id(1L).reviewDate(date2).title("Title1").description("Desc123").numberOfStars(5).userWriter(user).travelAgency(travelAgency).build();
        Review review2 = Review.builder().id(2L).reviewDate(date2).title("Title2").description("Desc456").numberOfStars(3).userWriter(user).travelAgency(travelAgency).build();
        ReviewEntity reviewEntity1 = ReviewConverter.convertToEntity(review1);
        ReviewEntity reviewEntity2 = ReviewConverter.convertToEntity(review2);
        reviewRepository.save(reviewEntity1);
        reviewRepository.save(reviewEntity2);

        //when(userService.getUser(travelAgencyId)).thenReturn(Optional.of(mockTravelAgency));
        //when(reviewService.getReviewsByTravelAgency(mockTravelAgency)).thenReturn(mockReviews);

        mockMvc.perform(get("/reviews/travel-agency/{travelAgencyId}", travelAgency.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        //verify(userService, times(1)).getUser(2L);
        //verify(reviewService, times(1)).getReviewsByTravelAgency(mockTravelAgency);
    }


    @Test
    void getReviewsByTravelAgency_TravelAgencyNotFound() throws Exception {
        Long travelAgencyId = 1L;

      //  when(userService.getUser(travelAgencyId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/reviews/travel-agency/{travelAgencyId}", travelAgencyId))
                .andExpect(status().isNotFound());

       // verify(userService, times(1)).getUser(travelAgencyId);
    }

}