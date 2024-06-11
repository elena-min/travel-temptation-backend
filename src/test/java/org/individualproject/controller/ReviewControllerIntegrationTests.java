package org.individualproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.individualproject.TravelApplication;
import org.individualproject.business.ReviewService;
import org.individualproject.business.UserService;
import org.individualproject.domain.CreateReviewRequest;
import org.individualproject.domain.Review;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TravelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerIntegrationTests {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ReviewService reviewService;
//
//    @MockBean
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void getReview_ShouldReturnExcursion_WhenReviewExists() throws  Exception{
//        Long reviewID = 1L;
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        User mockuser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        User mockTravelAgency = User.builder().id(2L).firstName("Global").lastName("Adventures").birthDate(date).email("global123@example.com").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();
//
//        Review mockreview = Review.builder()
//                .id(1L)
//                .reviewDate(new Date())
//                .title("Review title")
//                .userWriter(mockuser)
//                .travelAgency(mockTravelAgency)
//                .numberOfStars(3)
//                .description("Somethingekjf")
//                .build();
//
//        when(reviewService.getReview(reviewID)).thenReturn(Optional.of(mockreview));
//        mockMvc.perform(get("/reviews/{id}", reviewID)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(mockreview.getId()));
//
//        verify(reviewService).getReview(reviewID);
//    }
//
//    @Test
//    void getReview_ShouldReturnExcursion_WhenReviewDoesNotExists() throws Exception {
//        Long reviewID = 1L;
//
//        when(reviewService.getReview(reviewID)).thenReturn(Optional.empty());
//        mockMvc.perform(get("/reviews/{id}", reviewID)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//        verify(reviewService).getReview(reviewID);
//
//    }
//
//    @Test
//    void geReviews_ShouldReturnListOfReview() throws Exception{
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        User mockuser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        User mockTravelAgency = User.builder().id(2L).firstName("Global").lastName("Adventures").birthDate(date).email("global123@example.com").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();
//        Date date2 = new Date();
//
//        List<Review> mockReview = Arrays.asList(
//                Review.builder().id(1L).reviewDate(date2).title("Title1").description("Desc123").numberOfStars(5).userWriter(mockuser).travelAgency(mockTravelAgency).build(),
//                Review.builder().id(2L).reviewDate(date2).title("Title2").description("Desc456").numberOfStars(3).userWriter(mockuser).travelAgency(mockTravelAgency).build()
//        );
//
//        when(reviewService.getReviews()).thenReturn(mockReview);
//        mockMvc.perform(get("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(mockReview.size()))
//                .andExpect(jsonPath("$[0].id").value(mockReview.get(0).getId()))
//                .andExpect(jsonPath("$[0].title").value(mockReview.get(0).getTitle()))
//                .andExpect(jsonPath("$[1].id").value(mockReview.get(1).getId()))
//                .andExpect(jsonPath("$[1].title").value(mockReview.get(1).getTitle()));
//        verify(reviewService).getReviews();
//    }
//
//    @Test
//    void geReviews_ShouldReturnEmptyListWithNoReview() throws Exception{
//
//        when(reviewService.getReviews()).thenReturn(Collections.emptyList());
//        mockMvc.perform(get("/reviews")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(0));
//        verify(reviewService).getReviews();
//    }
//
//    @Test
//    void createReview_ShouldReturnCreatedReview_WhenValidRequest() throws Exception{
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        User mockuser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        User mockTravelAgency = User.builder().id(2L).firstName("Global").lastName("Adventures").birthDate(date).email("global123@example.com").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();
//        Date date2 = new Date();
//
//        CreateReviewRequest request = new CreateReviewRequest();
//        request.setReviewDate(date2);
//        request.setTitle("Title1");
//        request.setDescription("Description");
//        request.setNumberOfStars(4);
//        request.setUserWriter(mockuser);
//        request.setTravelAgency(mockTravelAgency);
//
//        Review mockreview = Review.builder()
//                .id(1L)
//                .reviewDate(date2)
//                .title("Title1")
//                .userWriter(mockuser)
//                .travelAgency(mockTravelAgency)
//                .numberOfStars(4)
//                .description("Description")
//                .build();
//
//        when(reviewService.createReview(request)).thenReturn(mockreview);
//        mockMvc.perform(post("/reviews")
//                        .with(user("username").roles("USER"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(mockreview.getId()));
//        verify(reviewService).createReview(request);
//    }
//
//    @Test
//    void createReview_ShouldReturnForbidden_WhenUserDoesNotHaveRole() throws Exception{
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        User mockuser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        User mockTravelAgency = User.builder().id(2L).firstName("Global").lastName("Adventures").birthDate(date).email("global123@example.com").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();
//        Date date2 = new Date();
//
//        CreateReviewRequest request = new CreateReviewRequest();
//        request.setReviewDate(date2);
//        request.setTitle("Title1");
//        request.setDescription("Description");
//        request.setNumberOfStars(4);
//        request.setUserWriter(mockuser);
//        request.setTravelAgency(mockTravelAgency);
//
//        Review mockreview = Review.builder()
//                .id(1L)
//                .reviewDate(date2)
//                .title("Title1")
//                .userWriter(mockuser)
//                .travelAgency(mockTravelAgency)
//                .numberOfStars(4)
//                .description("Description")
//                .build();
//
//        when(reviewService.createReview(request)).thenReturn(mockreview);
//        mockMvc.perform(post("/reviews"))
//                .andExpect(status().isUnauthorized());
//
//        verify(reviewService, never()).createReview(any());
//    }
//
//
//    @Test
//    void deleteReview_Success() throws Exception{
//        when(reviewService.deleteReview(anyLong())).thenReturn(true);
//
//        mockMvc.perform(delete("/reviews/{id}", 1)
//                        .with(user("username").roles("USER")))
//                .andExpect(status().isOk());
//
//        verify(reviewService, times(1)).deleteReview(anyLong());
//    }
//    @Test
//    public void deleteReview_NotFound() throws Exception {
//        when(reviewService.deleteReview(anyLong())).thenReturn(false);
//
//        mockMvc.perform(delete("/review/{id}", 1)
//                        .with(user("username").roles("USER")))
//                .andExpect(status().isNotFound());
//
//        verify(reviewService, times(0)).deleteReview(anyLong());
//    }
//
//    @Test
//    public void deleteReview_UnauthorizedAccess() throws Exception {
//        mockMvc.perform(delete("/reviews/{id}", 1))
//                .andExpect(status().isUnauthorized());
//    }
//
//
//    @Test
//    void getReviewsByUser() throws Exception{
//        Long userId = 1L;
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        User mockuser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        User mockTravelAgency = User.builder().id(2L).firstName("Global").lastName("Adventures").birthDate(date).email("global123@example.com").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();
//        Date date2 = new Date();
//
//        List<Review> mockReviews = Arrays.asList(
//                Review.builder().id(1L).reviewDate(date2).title("Title1").description("Desc123").numberOfStars(5).userWriter(mockuser).travelAgency(mockTravelAgency).build(),
//                Review.builder().id(2L).reviewDate(date2).title("Title2").description("Desc456").numberOfStars(3).userWriter(mockuser).travelAgency(mockTravelAgency).build()
//        );
//
//        when(userService.getUser(userId)).thenReturn(Optional.of(mockuser));
//        when(reviewService.getReviewsByUser(mockuser)).thenReturn(mockReviews);
//
//        mockMvc.perform(get("/reviews/user/{userId}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//
//        verify(userService, times(1)).getUser(userId);
//        verify(reviewService, times(1)).getReviewsByUser(mockuser);
//    }
//
//    @Test
//    void getReviewsByUser_UserNotFound() throws Exception{
//        Long userId = 1L;
//        when(userService.getUser(userId)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/reviews/user/{userId}", userId))
//                .andExpect(status().isNotFound());
//
//        verify(userService, times(1)).getUser(userId);
//    }
//
//    @Test
//    void getReviewsByTravelAgency() throws Exception {
//        Long travelAgencyId = 2L;
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        User mockTravelAgency = User.builder().id(2L).firstName("Global").lastName("Adventures").birthDate(date).email("global123@example.com").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();
//        Date date2 = new Date();
//
//        List<Review> mockReviews = Arrays.asList(
//                Review.builder().id(1L).reviewDate(date2).title("Title1").description("Desc123").numberOfStars(5).userWriter(mockTravelAgency).travelAgency(mockTravelAgency).build(),
//                Review.builder().id(2L).reviewDate(date2).title("Title2").description("Desc456").numberOfStars(3).userWriter(mockTravelAgency).travelAgency(mockTravelAgency).build()
//        );
//
//        when(userService.getUser(travelAgencyId)).thenReturn(Optional.of(mockTravelAgency));
//        when(reviewService.getReviewsByTravelAgency(mockTravelAgency)).thenReturn(mockReviews);
//
//        mockMvc.perform(get("/reviews/travelagency/{travelAgencyId}", 2L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//
//        verify(userService, times(1)).getUser(2L);
//        verify(reviewService, times(1)).getReviewsByTravelAgency(mockTravelAgency);
//    }
//
//
//    @Test
//    public void getExcursionsByTravelAgency_TravelAgencyNotFound() throws Exception {
//        Long travelAgencyId = 1L;
//
//        when(userService.getUser(travelAgencyId)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/reviews/travelagency/{travelAgencyId}", travelAgencyId))
//                .andExpect(status().isNotFound());
//
//        verify(userService, times(1)).getUser(travelAgencyId);
//    }

}