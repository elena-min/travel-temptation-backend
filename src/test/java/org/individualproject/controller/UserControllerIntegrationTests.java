package org.individualproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.individualproject.TravelApplication;
import org.individualproject.business.converter.*;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.BookingStatus;
import org.individualproject.domain.enums.Gender;
import org.individualproject.persistence.*;
import org.individualproject.persistence.entity.*;
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
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = TravelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ExcursionRepository excursionRepository;

    @Autowired
    private PaymentDetailsRepository paymentDetailsRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccessToken requestAccessToken;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john.doe@example.com");
        user.setHashedPassword("hashedPassword");
        user.setGender(Gender.MALE);
        user.setUsername("johnDoe");
        UserEntity userEntity = UserConverter.convertToEntity(user);
        userEntity = userRepository.save(userEntity);

        reviewRepository.deleteAll();
        bookingRepository.deleteAll();
        excursionRepository.deleteAll();;
        paymentDetailsRepository.deleteAll();

        when(requestAccessToken.getUserID()).thenReturn(userEntity.getId());
    }

    @Test
    void createUser_shouldCreateAndReturn201_WhenRequestValid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setEmail("john.doe@example.com");
        request.setPassword("hashedPassword");
        request.setGender(Gender.MALE);
        request.setUsername("johnDoe2");

        //User expectedUser = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "johnDoe", "hashedPassword", Gender.MALE);
        //when(userService.createUser(any(CreateUserRequest.class))).thenReturn(expectedUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        Optional<UserEntity> userEntityOptional = userRepository.findByUsername("johnDoe2");
        assertTrue(userEntityOptional.isPresent());
        UserEntity userEntity = userEntityOptional.get();
        assertEquals("John", userEntity.getFirstName());
        assertEquals("Doe", userEntity.getLastName());
        assertEquals("john.doe@example.com", userEntity.getEmail());

       // verify(userService).createUser(any(CreateUserRequest.class));
    }
    @Test
    void createUser_shouldCreateAndReturn400_WhenRequestInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setFirstName("");
        request.setLastName("Doe");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setEmail("john.doe@example.com");
        request.setPassword("hashedPassword");
        request.setGender(Gender.MALE);
        request.setUsername("johnDoe");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void getUsers_shouldReturn200WithUsersList() throws Exception {
        LocalDate date = LocalDate.of(2014, 9, 16);

//        List<User> expected =  Arrays.asList(
//                new User(1L, "John", "Doe", date, "j.doe@example.com","johnDoe", "hashedPassword1", Gender.MALE ),
//                new User(2L, "Eve", "McDonalds", date, "e.mcdonalds@example.com","eveMcdon" , "hashedPassword2", Gender.FEMALE),
//                new User(3L, "Donald", "Duck", date, "d.duck@example.com", "DOnalds", "hashedPassword3",  Gender.MALE)
//        );
        //when(userService.getUsers()).thenReturn(expected);

        User user1 = new User(null, "John", "Doe", date, "j.doe@example.com", "johnDoe", "hashedPassword1", Gender.MALE);
        User user2 = new User(null, "Eve", "McDonalds", date, "e.mcdonalds@example.com", "eveMcdon", "hashedPassword2", Gender.FEMALE);
        User user3 = new User(null, "Donald", "Duck", date, "d.duck@example.com", "DOnalds", "hashedPassword3", Gender.MALE);

        UserEntity userEntity1 = UserConverter.convertToEntity(user1);
        UserEntity userEntity2 = UserConverter.convertToEntity(user2);
        UserEntity userEntity3 = UserConverter.convertToEntity(user3);

        userRepository.saveAll(Arrays.asList(userEntity1, userEntity2, userEntity3));


        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[1].firstName").value("John"))
                .andExpect(jsonPath("$[1].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].email").value("j.doe@example.com"))
                .andExpect(jsonPath("$[2].firstName").value("Eve"))
                .andExpect(jsonPath("$[2].lastName").value("McDonalds"))
                .andExpect(jsonPath("$[2].email").value("e.mcdonalds@example.com"))
                .andExpect(jsonPath("$[3].firstName").value("Donald"))
                .andExpect(jsonPath("$[3].lastName").value("Duck"))
                .andExpect(jsonPath("$[3].email").value("d.duck@example.com"));

       //verify(userService).getUsers();
    }

    @Test
    void getUser_shouldReturn200WithUser_whenUserFound() throws Exception {
        User user = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "johnDoe","hashedPassword", Gender.MALE);
        UserEntity userEntity = UserConverter.convertToEntity(user);
        userEntity = userRepository.save(userEntity);
       // when(userService.getUser(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", userEntity.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

       // verify(userService).getUser(1L);
        Optional<UserEntity> retrievedUser = userRepository.findById(userEntity.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals("John", retrievedUser.get().getFirstName());
        assertEquals("Doe", retrievedUser.get().getLastName());
        assertEquals("john.doe@example.com", retrievedUser.get().getEmail());
    }
    @Test
    void getUser_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByUsername_shouldReturn200WithUser_whenUserFound() throws Exception {
        //User user = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "johnDoe","hashedPassword", Gender.MALE);
       // UserEntity userEntity = UserConverter.convertToEntity(user);
        //userEntity = userRepository.save(userEntity);
        // when(userService.getUser(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/username/{username}", "johnDoe"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        // verify(userService).getUser(1L);
        Optional<UserEntity> retrievedUser = userRepository.findByUsername("johnDoe");
        assertTrue(retrievedUser.isPresent());
        assertEquals("John", retrievedUser.get().getFirstName());
        assertEquals("Doe", retrievedUser.get().getLastName());
        assertEquals("john.doe@example.com", retrievedUser.get().getEmail());
    }
    @Test
    void getUserByUsername_shouldReturn404_whenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/username/{username}" , "someUSername"))
                .andExpect(status().isNotFound());
    }
    @Test
    @Transactional
    void deleteUser_shouldReturnOk_whenUserExists() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john.doe@example.com");
        user.setHashedPassword("hashedPassword");
        user.setGender(Gender.MALE);
        user.setUsername("johnDoe");
        UserEntity saveduserEntity = userRepository.save(user);
        when(requestAccessToken.getUserID()).thenReturn(saveduserEntity.getId());


        mockMvc.perform(delete("/users/{id}", saveduserEntity.getId())
                        .with(user("username").roles("USER")))
                .andExpect(status().isOk());

        Optional<UserEntity> deletedUser = userRepository.findById(saveduserEntity.getId());
        assertFalse(deletedUser.isPresent());

    }

    @Test
    void updateUser_shouldReturn204() throws Exception {
        UserEntity user = new UserEntity();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john.doe@example.com");
        user.setHashedPassword("hashedPassword");
        user.setGender(Gender.MALE);
        user.setUsername("johnDoe2");
        UserEntity saveduserEntity = userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest(saveduserEntity.getId(), "John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE);
        when(requestAccessToken.getUserID()).thenReturn(saveduserEntity.getId());


        mockMvc.perform(put("/users/{id}", saveduserEntity.getId())
                        .with(user("username").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());


        Optional<UserEntity> updatedUser = userRepository.findById(saveduserEntity.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals(request.getFirstName(), updatedUser.get().getFirstName());
        assertEquals(request.getLastName(), updatedUser.get().getLastName());
        assertEquals(request.getBirthDate(), updatedUser.get().getBirthDate());
        assertEquals(request.getGender(), updatedUser.get().getGender());
        //verify(userService).updateUser(request);
    }

    @Test
    void updateUser_shouldReturnBadRequest_whenRequestIsInvalid() throws Exception {
        UserEntity user = new UserEntity();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john.doe@example.com");
        user.setHashedPassword("hashedPassword");
        user.setGender(Gender.MALE);
        user.setUsername("johnDoe2");
        UserEntity saveduserEntity =userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest(saveduserEntity.getId(), "", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE);
        when(requestAccessToken.getUserID()).thenReturn(saveduserEntity.getId());

        mockMvc.perform(put("/users/{id}", saveduserEntity.getId())
                        .with(user("johnDoe").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        //verify(userService, never()).updateUser(request);
        Optional<UserEntity> updatedUser = userRepository.findById(saveduserEntity.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("John", updatedUser.get().getFirstName());
        assertEquals("Doe", updatedUser.get().getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), updatedUser.get().getBirthDate());
        assertEquals(Gender.MALE, updatedUser.get().getGender());
    }

    @Test
    void getReviewsByUser_shouldReturn200WithReviews() throws Exception{
        LocalDate date = LocalDate.of(2014, 9, 16);
        User mockuser = User.builder().firstName("John").lastName("Doe").username("johnDOe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User mockTravelAgency = User.builder().firstName("Global").lastName("Adventures").username("global123").birthDate(date).email("global123@example.com").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();
        UserEntity mockuserEntity = UserConverter.convertToEntity(mockuser);
        UserEntity mockTravelAgencyEntity = UserConverter.convertToEntity(mockTravelAgency);
        UserEntity savedUserEtity = userRepository.save(mockuserEntity);
        User savedUser = UserConverter.mapToDomain(savedUserEtity);
        Long userId = savedUser.getId();
        UserEntity savedTravelAgencyEntity = userRepository.save(mockTravelAgencyEntity);
        User savedTravelAgency = UserConverter.mapToDomain(savedTravelAgencyEntity);

        Date date2 = new Date();

        Review review1  = Review.builder().id(1L).reviewDate(date2).title("Title1").description("Desc123").numberOfStars(5).userWriter(savedUser).travelAgency(savedTravelAgency).build();
        Review review2  =Review.builder().id(2L).reviewDate(date2).title("Title2").description("Desc456").numberOfStars(3).userWriter(savedUser).travelAgency(savedTravelAgency).build();
        ReviewEntity reviewEntity1 = ReviewConverter.convertToEntity(review1);
        ReviewEntity reviewEntity2 = ReviewConverter.convertToEntity(review2);
        reviewRepository.save(reviewEntity1);
        reviewRepository.save(reviewEntity2);
        when(requestAccessToken.getUserID()).thenReturn(userId);

        //when(userService.getUser(userId)).thenReturn(Optional.of(mockuser));
        //when(reviewService.getReviewsByUser(mockuser)).thenReturn(mockReviews);

        mockMvc.perform(get("/users/{userId}/reviews", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Title1"))
                .andExpect(jsonPath("$[1].title").value("Title2"));
    }

    @Test
    void getBookingsByUser_shouldReturn200WithBookings() throws Exception{
        LocalDate date = LocalDate.of(2014, 9, 16);
        User mockuser = User.builder().firstName("John").lastName("Doe").username("johnDOe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User mockTravelAgency = User.builder().firstName("Global").lastName("Adventures").username("global123").birthDate(date).email("global123@example.com").hashedPassword("hashedPassword2").gender(Gender.OTHER).build();

        UserEntity mockuserEntity = UserConverter.convertToEntity(mockuser);
        UserEntity mockTravelAgencyEntity = UserConverter.convertToEntity(mockTravelAgency);
        UserEntity savedUserEtity = userRepository.save(mockuserEntity);
        User savedUser = UserConverter.mapToDomain(savedUserEtity);
        Long userId = savedUser.getId();

        UserEntity savedTravelAgencyEntity = userRepository.save(mockTravelAgencyEntity);
        User savedTravelAgency = UserConverter.mapToDomain(savedTravelAgencyEntity);

        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");
        Excursion excursion = Excursion.builder().id(1L).name("Excursion 1").description("Descrpiton").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(savedTravelAgency).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build();
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(excursion);
        ExcursionEntity savedExcursionEntity = excursionRepository.save(excursionEntity);
        Excursion savedExcursion = ExcursionConverter.mapToDomain(savedExcursionEntity);

        YearMonth expDate = YearMonth.of(2027, 9);
        PaymentDetails fakePaymentDetails = PaymentDetails.builder().id(1L).expirationDate(expDate).cvv("123").cardNumber("1234567890123456").cardHolderName("Nick Jonas").user(savedUser).build();
        PaymentDetailsEntity paymentDetailsEntity = PaymentDetailsConverter.convertToEntity(fakePaymentDetails);
        paymentDetailsRepository.save(paymentDetailsEntity);

        Booking booking1 = Booking.builder().id(1L).bookingTime(LocalDateTime.now()).numberOfTravelers(4).user(savedUser).status(BookingStatus.CONFIRMED).excursion(savedExcursion).bankingDetails(fakePaymentDetails).build();
        Booking booking2 = Booking.builder().id(2L).bookingTime(LocalDateTime.now()).numberOfTravelers(7).user(savedUser).status(BookingStatus.CONFIRMED).excursion(savedExcursion).bankingDetails(fakePaymentDetails).build();
        BookingEntity bookingEntity1 = BookingConverter.convertToEntity(booking1);
        BookingEntity bookingEntity2 = BookingConverter.convertToEntity(booking2);
        bookingRepository.save(bookingEntity1);
        bookingRepository.save(bookingEntity2);

        when(requestAccessToken.getUserID()).thenReturn(userId);
        //when(userService.getUser(userId)).thenReturn(Optional.of(mockuser));
        //when(reviewService.getReviewsByUser(mockuser)).thenReturn(mockReviews);

        mockMvc.perform(get("/users/{userId}/bookings", userId)
                .with(user("username").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numberOfTravelers").value(4))
                .andExpect(jsonPath("$[1].numberOfTravelers").value(7));
    }


}
