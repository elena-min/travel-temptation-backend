package org.individualproject.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.individualproject.TravelApplication;
import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.CreateExcursionRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateExcursionRequest;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.UserRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(classes = TravelApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExcursionsControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExcursionRepository excursionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccessToken requestAccessToken;
    private User travelAgency;

    @BeforeEach
    void setUp() {
        excursionRepository.deleteAll();
        userRepository.deleteAll();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john.doe@example.com");
        user.setHashedPassword("hashedPassword");
        user.setGender(Gender.MALE);
        user.setUsername("johnDoe");
        UserEntity userEntity = UserConverter.convertToEntity(user);
        userEntity = userRepository.save(userEntity);
        User savedtravelAgency = UserConverter.mapToDomain(userEntity);
        travelAgency = savedtravelAgency;

        when(requestAccessToken.getUserID()).thenReturn(userEntity.getId());

    }

    @Test
    void getExcursion_ShouldReturnExcursion_WhenExcursionExists() throws Exception {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");
        //User mockuser = User.builder().firstName("John").lastName("Doe").birthDate(date).username("username").email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        //UserEntity userEntity = UserConverter.convertToEntity(mockuser);
        //userRepository.save(userEntity);

        Excursion mockExcursion = Excursion.builder()
                .name("Mountain Hike")
                .destinations(destinations)
                .description("description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(travelAgency)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(mockExcursion);
        //excursionEntity.setTravelAgency(userEntity);
        excursionEntity = excursionRepository.save(excursionEntity);

        // Use the actual ID of the saved excursionEntity
        Long savedExcursionId = excursionEntity.getId();

        //when(excursionService.getExcursion(excursionId)).thenReturn(Optional.of(mockExcursion));
        mockMvc.perform(get("/excursions/{id}", savedExcursionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedExcursionId));

      //  verify(excursionService).getExcursion(excursionId);

    }

    @Test
    void getExcursion_ShouldReturnNotFound_WhenExcursionDoesNotExist() throws Exception {
        Long excursionId = 1L;

        //when(excursionService.getExcursion(excursionId)).thenReturn(Optional.empty());
        mockMvc.perform(get("/excursions/{id}", excursionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        //verify(excursionService).getExcursion(excursionId);

    }

    @Test
    void getExcursions_ShouldReturnListOfExcursions() throws Exception {
      //  LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

       // User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        Excursion mockExcursion1 = Excursion.builder().id(1L).name("Excursion 1").description("Descrpition2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build();
        Excursion mockExcursion2 = Excursion.builder().id(2L).name("Excursion 2").description("Descrpition1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build();
        ExcursionEntity excursionEntity1 = ExcursionConverter.convertToEntity(mockExcursion1);
        ExcursionEntity excursionEntity2 = ExcursionConverter.convertToEntity(mockExcursion2);
        ExcursionEntity savedEXcursion1 = excursionRepository.save(excursionEntity1);
        ExcursionEntity savedEXcursion2 = excursionRepository.save(excursionEntity2);

        //when(excursionService.getExcursions()).thenReturn(mockExcursions);
        mockMvc.perform(get("/excursions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(savedEXcursion1.getId()))
                .andExpect(jsonPath("$[0].name").value(savedEXcursion1.getName()))
                .andExpect(jsonPath("$[1].id").value(savedEXcursion2.getId()))
                .andExpect(jsonPath("$[1].name").value(savedEXcursion2.getName()));
       // verify(excursionService).getExcursions();
    }

    @Test
    void getExcursions_shouldReturnEmptyListWithNoExcursions() throws Exception {
       // when(excursionService.getExcursions()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/excursions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
       // verify(excursionService).getExcursions();
    }

    @Test
    void createExcursion_ShouldReturnCreatedExcursion_WhenValidRequest() throws Exception {
        when(requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);
        // LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = Arrays.asList("Rome", "Florance");

        Excursion mockExcursion = Excursion.builder()
                .id(1L)
                .name("Mountain Hike")
                .destinations(destinations)
                .description("Description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(travelAgency)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();

        CreateExcursionRequest request = CreateExcursionRequest.builder()
                .name("Mountain Hike")
                .destinations(Arrays.asList("Paris", "London"))
                .description("Description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(travelAgency)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .build();


        //when(excursionService.createExcursion(request)).thenReturn(mockExcursion);
        mockMvc.perform(post("/excursions")
                        .with(user("username").roles("TRAVELAGENCY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Mountain Hike"))
                .andExpect(jsonPath("$.destinations[0]").value("Paris"))
                .andExpect(jsonPath("$.destinations[1]").value("London"))
                .andExpect(jsonPath("$.price").value(1500.0))
                .andExpect(jsonPath("$.numberOfAvaliableSpaces").value(58));
       // verify(excursionService).createExcursion(request);
    }

    @Test
    void createExcursion_ShouldReturnForbidden_WhenUserDoesNotHaveRole() throws Exception {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        Excursion mockExcursion = Excursion.builder()
                .id(1L)
                .name("Mountain Hike")
                .destinations(destinations)
                .description("Description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(mockUser)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();

        CreateExcursionRequest request = CreateExcursionRequest.builder()
                .name("Mountain Hike")
                .destinations(Arrays.asList("Paris", "London"))
                .description("Description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(mockUser)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .build();

        //when(excursionService.createExcursion(request)).thenReturn(mockExcursion);
        mockMvc.perform(post("/excursions")
                        .with(user("username").roles("USER")))
                .andExpect(status().isForbidden());

        //verify(excursionService, never()).createExcursion(any());
    }
    @Test
    void createExcursion_shouldNotCreateExcursion_InvalidRequest() throws Exception {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        Excursion mockExcursion = Excursion.builder()
                .id(1L)
                .name("Mountain Hike")
                .destinations(destinations)
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(mockUser)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();

        CreateExcursionRequest createRequest = CreateExcursionRequest.builder()
                .name("Mountain Hike")
                .destinations(Arrays.asList("Paris", "London"))
                .startDate(startDate)
                .endDate(null)
                .travelAgency(mockUser)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .build();

        //when(excursionService.createExcursion(createRequest)).thenReturn(mockExcursion);
        mockMvc.perform(post("/excursions")
                        .with(user("username").roles("TRAVELAGENCY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

       // verify(excursionService, never()).createExcursion(any());
    }

    @Test
    void deleteExcursion_Success() throws Exception{
        when(requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);

        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");
        Excursion mockExcursion = Excursion.builder()
                .name("Mountain Hike")
                .destinations(destinations)
                .description("description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(travelAgency)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(mockExcursion);
        excursionEntity = excursionRepository.save(excursionEntity);

        mockMvc.perform(delete("/excursions/{id}", excursionEntity.getId())
                        .with(user("username").roles("TRAVELAGENCY")))
                .andExpect(status().isOk());

       // verify(excursionService, times(1)).deleteExcursion(anyLong());
    }

    @Test
    void deleteExcursion_NotFound() throws Exception {
       //when(excursionService.deleteExcursion(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/excursions/{id}", 1)
                        .with(user("username").roles("TRAVELAGENCY")))
                .andExpect(status().isNotFound());

       // verify(excursionService, times(1)).deleteExcursion(anyLong());
    }

    @Test
    void deleteExcursion_UnauthorizedAccess() throws Exception {
        mockMvc.perform(delete("/excursions/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateExcursion_ShouldReturnNoContent_WhenExcursionUpdatedSuccessfully() throws Exception{
        when(requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);

        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        Excursion mockExcursion = Excursion.builder()
                .name("Mountain Hike")
                .destinations(destinations)
                .description("description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(travelAgency)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(mockExcursion);
        excursionEntity = excursionRepository.save(excursionEntity);

        UpdateExcursionRequest request = UpdateExcursionRequest.builder()
                .name("Sweet romance")
                .destinations(Arrays.asList("Paris", "London"))
                .description("Description")
                .startDate(startDate)
                .endDate(endDate)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .build();

        //when(excursionService.updateExcursion(any())).thenReturn(true);

        // Perform the request and assert the response
        mockMvc.perform(put("/excursions/{id}", excursionEntity.getId())
                        .with(user("username").roles("TRAVELAGENCY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        Optional<ExcursionEntity> updatedExcursion = excursionRepository.findById(excursionEntity.getId());
        assertTrue(updatedExcursion.isPresent());
        assertEquals(request.getName(), updatedExcursion.get().getName());
        assertEquals(request.getDescription(), updatedExcursion.get().getDescription());
        assertEquals(request.getStartDate(), updatedExcursion.get().getStartDate());
        //verify(excursionService, times(1)).updateExcursion(any());
    }

    @Test
    void updateExcursion_ShouldReturnInvalidRequest() throws Exception {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);

        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        Excursion mockExcursion = Excursion.builder()
                .name("Mountain Hike")
                .destinations(destinations)
                .description("description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(travelAgency)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(mockExcursion);
        excursionEntity = excursionRepository.save(excursionEntity);

        UpdateExcursionRequest request = UpdateExcursionRequest.builder()
                .name("Mountain Hike")
                .destinations(Arrays.asList("Paris", "London"))
                .startDate(startDate)
                .endDate(null)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .build();

        //when(excursionService.updateExcursion(any())).thenReturn(true);

        mockMvc.perform(put("/excursions/{id}", excursionEntity.getId())
                        .with(user("username").roles("TRAVELAGENCY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Optional<ExcursionEntity> updatedExcursion = excursionRepository.findById(excursionEntity.getId());
        assertTrue(updatedExcursion.isPresent());
        assertEquals("Mountain Hike", updatedExcursion.get().getName());
        assertEquals("description", updatedExcursion.get().getDescription());
        //verify(excursionService, never()).updateExcursion(any());
    }

    @Test
    void updateExcursion_ShouldReturnForbidden_WhenUnauthorizedAccessExceptionThrown() throws Exception {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");


        Excursion mockExcursion = Excursion.builder()
                .name("Mountain Hike")
                .destinations(destinations)
                .description("description")
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(travelAgency)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(mockExcursion);
        excursionEntity = excursionRepository.save(excursionEntity);

        UpdateExcursionRequest request = UpdateExcursionRequest.builder()
                .name("Mountain Hike")
                .destinations(Arrays.asList("Paris", "London"))
                .startDate(startDate)
                .endDate(endDate)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .build();


        //when(excursionService.updateExcursion(any())).thenThrow(new UnauthorizedDataAccessException("Unauthorized"));

        mockMvc.perform(put("/excursions/{id}", excursionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

       // verify(excursionService, times(0)).updateExcursion(any());
    }

    @Test
    void getExcursionByName_ShouldReturnExcursion_WhenExcursionExists() throws Exception {
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");
        Excursion mockExcursion = Excursion.builder()
                .id(1L)
                .name("Mountain Hike")
                .description("Desc1")
                .destinations(destinations)
                .startDate(startDate)
                .endDate(endDate)
                .travelAgency(travelAgency)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();
        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(mockExcursion);
        excursionRepository.save(excursionEntity);

        //when(excursionService.getExcursionByName(excursionName)).thenReturn(Optional.of(mockExcursion));
        mockMvc.perform(get("/excursions/name/{name}", "Mountain Hike")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(mockExcursion.getName()));


        //verify(excursionService).getExcursionByName(excursionName);

    }

    @Test
    void getExcursionByName_ShouldReturnNotFound_WhenExcursionDoesNotExist() throws Exception {
        String excursionName = "Mountain Hike";

        //when(excursionService.getExcursionByName(excursionName)).thenReturn(Optional.empty());
        mockMvc.perform(get("/excursions/name/{name}", excursionName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        //verify(excursionService).getExcursionByName(excursionName);

    }

    @Test
    void getExcursionsByTravelAgency_Success() throws Exception {
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        Excursion excursion1 = Excursion.builder().id(1L).name("Excursion 1").description("Desc1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build();
        Excursion excursion2 = Excursion.builder().id(2L).name("Excursion 2").description("Desc2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build();
        ExcursionEntity excursionEntity1 = ExcursionConverter.convertToEntity(excursion1);
        ExcursionEntity excursionEntity2 = ExcursionConverter.convertToEntity(excursion2);
        excursionRepository.save(excursionEntity1);
        excursionRepository.save(excursionEntity2);
        // when(userService.getUser(travelAgencyId)).thenReturn(Optional.of(mockUser));
        //when(excursionService.getExcursionsByTravelAgency(mockUser)).thenReturn(mockExcursions);

        mockMvc.perform(get("/excursions/travel-agency/{travelAgencyID}", travelAgency.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        //verify(userService, times(1)).getUser(travelAgencyId);
       // verify(excursionService, times(1)).getExcursionsByTravelAgency(mockUser);
    }

    @Test
    void getExcursionsByTravelAgency_TravelAgencyNotFound() throws Exception {
        Long travelAgencyId = 1L;

       // when(userService.getUser(travelAgencyId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/excursions/travel-agency/{travelAgencyID}", travelAgencyId))
                .andExpect(status().isNotFound());

       //verify(userService, times(1)).getUser(travelAgencyId);
    }
    @Test
    void searchExcursionsByNameAndTravelAgency_WithSearchTerm() throws Exception {
        String searchTerm = "Mountain";
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        Excursion excursion1 = Excursion.builder().id(1L).name("Mountain 1").description("Desc1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build();
        Excursion excursion2 = Excursion.builder().id(2L).name("Mountains 2").description("Desc2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build();
        ExcursionEntity excursionEntity1 = ExcursionConverter.convertToEntity(excursion1);
        ExcursionEntity excursionEntity2 = ExcursionConverter.convertToEntity(excursion2);
        excursionRepository.save(excursionEntity1);
        excursionRepository.save(excursionEntity2);
       // when(excursionService.searchExcursionsByNameAndTravelAgency(searchTerm)).thenReturn(mockExcursions);

        mockMvc.perform(get("/excursions/search-name").param("searchTerm", searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        //verify(excursionService, times(1)).searchExcursionsByNameAndTravelAgency(searchTerm);
    }

    @Test
    void searchExcursionsByNameAndTravelAgency_WithoutSearchTerm() throws Exception {
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        Excursion excursion1 = Excursion.builder().id(1L).name("Mountain 1").description("Desc1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build();
        Excursion excursion2 = Excursion.builder().id(2L).name("Mountains 2").description("Desc2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build();
        ExcursionEntity excursionEntity1 = ExcursionConverter.convertToEntity(excursion1);
        ExcursionEntity excursionEntity2 = ExcursionConverter.convertToEntity(excursion2);
        excursionRepository.save(excursionEntity1);
        excursionRepository.save(excursionEntity2);
        //when(excursionService.getExcursions()).thenReturn(mockExcursions);

        mockMvc.perform(get("/excursions/search-name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        //verify(excursionService, times(1)).getExcursions();
    }

    @Test
    void searchExcursions_WithSearchTerm() throws Exception {
        String searchTerm = "Mountain";

        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        Excursion excursion1 = Excursion.builder().id(1L).name("Mountain 1").description("Desc1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build();
        Excursion excursion2 = Excursion.builder().id(2L).name("Mountains 2").description("Desc2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build();
        ExcursionEntity excursionEntity1 = ExcursionConverter.convertToEntity(excursion1);
        ExcursionEntity excursionEntity2 = ExcursionConverter.convertToEntity(excursion2);
        excursionRepository.save(excursionEntity1);
        excursionRepository.save(excursionEntity2);

        //when(excursionService.searchExcursions(searchTerm, 0.0, Double.MAX_VALUE)).thenReturn(mockExcursions);

        mockMvc.perform(get("/excursions/search-name-and-price").param("searchTerm", searchTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        //verify(excursionService, times(1)).searchExcursions(searchTerm, 0.0, Double.MAX_VALUE);
    }

    @Test
    void searchExcursions_WithMinAndMaxPrice() throws Exception {
        String minPrice = "200.0";
        String maxPrice = "500.0";

        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");

        Excursion excursion1 = Excursion.builder().id(1L).name("Mountain 1").description("Desc1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build();
        Excursion excursion2 = Excursion.builder().id(2L).name("Mountains 2").description("Desc2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(travelAgency).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build();
        ExcursionEntity excursionEntity1 = ExcursionConverter.convertToEntity(excursion1);
        ExcursionEntity excursionEntity2 = ExcursionConverter.convertToEntity(excursion2);
        excursionRepository.save(excursionEntity1);
        excursionRepository.save(excursionEntity2);

        //when(excursionService.searchExcursions(null, 200.0, 500.0)).thenReturn(mockExcursions);

        mockMvc.perform(get("/excursions/search-name-and-price")
                        .param("minPrice", minPrice)
                        .param("maxPrice", maxPrice))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        //verify(excursionService, times(1)).searchExcursions(null, 200.0, 500.0);
    }

}