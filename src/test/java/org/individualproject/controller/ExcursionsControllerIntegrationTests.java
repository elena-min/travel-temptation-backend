package org.individualproject.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.individualproject.TravelApplication;
import org.individualproject.business.ExcursionService;
import org.individualproject.business.UserService;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.domain.CreateExcursionRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateExcursionRequest;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.UserCredentialsDataSourceAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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
class ExcursionsControllerIntegrationTests {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ExcursionService excursionService;
//
//    @MockBean
//    private UserService userService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Test
//    void getExcursion_ShouldReturnExcursion_WhenExcursionExists() throws Exception {
//        Long excursionId = 1L;
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//        User mockuser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//
//        Excursion mockExcursion = Excursion.builder()
//                .id(1L)
//                .name("Mountain Hike")
//                .destinations(destinations)
//                .startDate(startDate)
//                .endDate(endDate)
//                .travelAgency(mockuser)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .numberOfSpacesLeft(58)
//                .build();
//
//        when(excursionService.getExcursion(excursionId)).thenReturn(Optional.of(mockExcursion));
//        mockMvc.perform(get("/excursions/{id}", excursionId)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(mockExcursion.getId()));
//
//        verify(excursionService).getExcursion(excursionId);
//
//    }
//
//    @Test
//    void getExcursion_ShouldReturnNotFound_WhenExcursionDoesNotExist() throws Exception {
//        Long excursionId = 1L;
//
//        when(excursionService.getExcursion(excursionId)).thenReturn(Optional.empty());
//        mockMvc.perform(get("/excursions/{id}", excursionId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//        verify(excursionService).getExcursion(excursionId);
//
//    }
//
//    @Test
//    void getExcursions_ShouldReturnListOfExcursions() throws Exception {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        List<Excursion> mockExcursions = Arrays.asList(
//                Excursion.builder().id(1L).name("Excursion 1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
//                Excursion.builder().id(2L).name("Excursion 2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
//        );
//
//        when(excursionService.getExcursions()).thenReturn(mockExcursions);
//        mockMvc.perform(get("/excursions")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(mockExcursions.size()))
//                .andExpect(jsonPath("$[0].id").value(mockExcursions.get(0).getId()))
//                .andExpect(jsonPath("$[0].name").value(mockExcursions.get(0).getName()))
//                .andExpect(jsonPath("$[1].id").value(mockExcursions.get(1).getId()))
//                .andExpect(jsonPath("$[1].name").value(mockExcursions.get(1).getName()));
//        verify(excursionService).getExcursions();
//    }
//
//    @Test
//    void getExcursions_shouldReturnEmptyListWithNoExcursions() throws Exception {
//        when(excursionService.getExcursions()).thenReturn(Collections.emptyList());
//        mockMvc.perform(get("/excursions")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$.length()").value(0));
//        verify(excursionService).getExcursions();
//    }
//
//    @Test
//    void createExcursion_ShouldReturnCreatedExcursion_WhenValidRequest() throws Exception {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = Arrays.asList("Rome", "Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        Excursion mockExcursion = Excursion.builder()
//                .id(1L)
//                .name("Mountain Hike")
//                .destinations(destinations)
//                .description("Description")
//                .startDate(startDate)
//                .endDate(endDate)
//                .travelAgency(mockUser)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .numberOfSpacesLeft(58)
//                .build();
//
//        CreateExcursionRequest request = CreateExcursionRequest.builder()
//                .name("Mountain Hike")
//                .destinations(Arrays.asList("Paris", "London"))
//                .description("Description")
//                .startDate(startDate)
//                .endDate(endDate)
//                .travelAgency(mockUser)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .build();
//
//        when(excursionService.createExcursion(request)).thenReturn(mockExcursion);
//        mockMvc.perform(post("/excursions")
//                        .with(user("username").roles("TRAVELAGENCY"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(mockExcursion.getId()));
//        verify(excursionService).createExcursion(request);
//    }
//
//    @Test
//    void createExcursion_ShouldReturnForbidden_WhenUserDoesNotHaveRole() throws Exception {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        Excursion mockExcursion = Excursion.builder()
//                .id(1L)
//                .name("Mountain Hike")
//                .destinations(destinations)
//                .startDate(startDate)
//                .endDate(endDate)
//                .travelAgency(mockUser)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .numberOfSpacesLeft(58)
//                .build();
//
//        CreateExcursionRequest request = CreateExcursionRequest.builder()
//                .name("Mountain Hike")
//                .destinations(Arrays.asList("Paris", "London"))
//                .startDate(startDate)
//                .endDate(endDate)
//                .travelAgency(mockUser)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .build();
//
//        when(excursionService.createExcursion(request)).thenReturn(mockExcursion);
//        mockMvc.perform(post("/excursions")
//                        .with(user("username").roles("USER")))
//                .andExpect(status().isForbidden());
//
//        verify(excursionService, never()).createExcursion(any());
//    }
//    @Test
//    void createExcursion() throws Exception {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        Excursion mockExcursion = Excursion.builder()
//                .id(1L)
//                .name("Mountain Hike")
//                .destinations(destinations)
//                .startDate(startDate)
//                .endDate(endDate)
//                .travelAgency(mockUser)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .numberOfSpacesLeft(58)
//                .build();
//
//        CreateExcursionRequest createRequest = CreateExcursionRequest.builder()
//                .name("Mountain Hike")
//                .destinations(Arrays.asList("Paris", "London"))
//                .startDate(startDate)
//                .endDate(null)
//                .travelAgency(mockUser)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .build();
//
//        when(excursionService.createExcursion(createRequest)).thenReturn(mockExcursion);
//        mockMvc.perform(post("/excursions")
//                        .with(user("username").roles("TRAVELAGENCY"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createRequest)))
//                .andExpect(status().isBadRequest());
//
//        // Verify interactions
//        verify(excursionService, never()).createExcursion(any());
//    }
//
//    @Test
//    void deleteExcursion_Success() throws Exception{
//        when(excursionService.deleteExcursion(anyLong())).thenReturn(true);
//
//        mockMvc.perform(delete("/excursions/{id}", 1)
//                        .with(user("username").roles("TRAVELAGENCY")))
//                .andExpect(status().isOk());
//
//        verify(excursionService, times(1)).deleteExcursion(anyLong());
//    }
//
//    @Test
//    public void deleteExcursion_NotFound() throws Exception {
//        when(excursionService.deleteExcursion(anyLong())).thenReturn(false);
//
//        mockMvc.perform(delete("/excursions/{id}", 1)
//                        .with(user("username").roles("TRAVELAGENCY")))
//                .andExpect(status().isNotFound());
//
//        verify(excursionService, times(1)).deleteExcursion(anyLong());
//    }
//
//    @Test
//    public void deleteExcursion_UnauthorizedAccess() throws Exception {
//        mockMvc.perform(delete("/excursions/{id}", 1))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void updateExcursion_ShouldReturnNoContent_WhenExcursionUpdatedSuccessfully() throws Exception{
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        UpdateExcursionRequest request = UpdateExcursionRequest.builder()
//                .name("Mountain Hike")
//                .destinations(Arrays.asList("Paris", "London"))
//                .description("Description")
//                .startDate(startDate)
//                .endDate(endDate)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .build();
//
//        when(excursionService.updateExcursion(any())).thenReturn(true);
//
//        // Perform the request and assert the response
//        mockMvc.perform(put("/excursions/{id}", 1)
//                        .with(user("username").roles("TRAVELAGENCY"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isNoContent());
//
//        // Verify that the service method was called with the correct parameters
//        verify(excursionService, times(1)).updateExcursion(any());
//    }
//
//    @Test
//    void updateExcursion_ShouldReturnInvalidRequest() throws Exception {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        UpdateExcursionRequest request = UpdateExcursionRequest.builder()
//                .name("Mountain Hike")
//                .destinations(Arrays.asList("Paris", "London"))
//                .startDate(startDate)
//                .endDate(null)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .build();
//
//        when(excursionService.updateExcursion(any())).thenReturn(true);
//
//        mockMvc.perform(put("/excursions/{id}", 1)
//                        .with(user("username").roles("TRAVELAGENCY"))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//
//        verify(excursionService, never()).updateExcursion(any());
//    }
//
//    @Test
//    void updateExcursion_ShouldReturnForbidden_WhenUnauthorizedAccessExceptionThrown() throws Exception {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        UpdateExcursionRequest request = UpdateExcursionRequest.builder()
//                .name("Mountain Hike")
//                .destinations(Arrays.asList("Paris", "London"))
//                .startDate(startDate)
//                .endDate(endDate)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .build();
//
//
//        when(excursionService.updateExcursion(any())).thenThrow(new UnauthorizedDataAccessException("Unauthorized"));
//
//        mockMvc.perform(put("/excursions/{id}", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isUnauthorized());
//
//        verify(excursionService, times(0)).updateExcursion(any());
//    }
//
//    @Test
//    void getExcursionByName_ShouldReturnExcursion_WhenExcursionExists() throws Exception {
//        String excursionName = "Mountain Hike";
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//        User mockuser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//
//        Excursion mockExcursion = Excursion.builder()
//                .id(1L)
//                .name("Mountain Hike")
//                .destinations(destinations)
//                .startDate(startDate)
//                .endDate(endDate)
//                .travelAgency(mockuser)
//                .price(1500.0)
//                .numberOfAvaliableSpaces(58)
//                .numberOfSpacesLeft(58)
//                .build();
//
//        when(excursionService.getExcursionByName(excursionName)).thenReturn(Optional.of(mockExcursion));
//        mockMvc.perform(get("/excursions/name/{name}", excursionName) // excursionName is a String
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value(mockExcursion.getName()));
//
//
//        verify(excursionService).getExcursionByName(excursionName);
//
//    }
//
//    @Test
//    void getExcursionByName_ShouldReturnNotFound_WhenExcursionDoesNotExist() throws Exception {
//        String excursionName = "Mountain Hike";
//
//        when(excursionService.getExcursionByName(excursionName)).thenReturn(Optional.empty());
//        mockMvc.perform(get("/excursions/name/{name}", excursionName)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//        verify(excursionService).getExcursionByName(excursionName);
//
//    }
//
//    @Test
//    public void getExcursionsByTravelAgency_Success() throws Exception {
//        Long travelAgencyId = 1L;
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        List<Excursion> mockExcursions = Arrays.asList(
//                Excursion.builder().id(1L).name("Excursion 1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
//                Excursion.builder().id(2L).name("Excursion 2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
//        );
//
//        when(userService.getUser(travelAgencyId)).thenReturn(Optional.of(mockUser));
//        when(excursionService.getExcursionsByTravelAgency(mockUser)).thenReturn(mockExcursions);
//
//        mockMvc.perform(get("/excursions/travelAgency/{travelAgencyID}", travelAgencyId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//
//        verify(userService, times(1)).getUser(travelAgencyId);
//        verify(excursionService, times(1)).getExcursionsByTravelAgency(mockUser);
//    }
//
//    @Test
//    public void getExcursionsByTravelAgency_TravelAgencyNotFound() throws Exception {
//        Long travelAgencyId = 1L;
//
//        when(userService.getUser(travelAgencyId)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/excursions/travelAgency/{travelAgencyID}", travelAgencyId))
//                .andExpect(status().isNotFound());
//
//        verify(userService, times(1)).getUser(travelAgencyId);
//    }
//    @Test
//    public void searchExcursionsByNameAndTravelAgency_WithSearchTerm() throws Exception {
//        String searchTerm = "Mountain";
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        List<Excursion> mockExcursions = Arrays.asList(
//                Excursion.builder().id(1L).name("Mountain 1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
//                Excursion.builder().id(2L).name("Mountains 2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
//        );
//        when(excursionService.searchExcursionsByNameAndTravelAgency(searchTerm)).thenReturn(mockExcursions);
//
//        mockMvc.perform(get("/excursions/searchName").param("searchTerm", searchTerm))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//
//        verify(excursionService, times(1)).searchExcursionsByNameAndTravelAgency(searchTerm);
//    }
//
//    @Test
//    public void searchExcursionsByNameAndTravelAgency_WithoutSearchTerm() throws Exception {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        List<Excursion> mockExcursions = Arrays.asList(
//                Excursion.builder().id(1L).name("Mountain 1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
//                Excursion.builder().id(2L).name("Mountains 2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
//        );
//        when(excursionService.getExcursions()).thenReturn(mockExcursions);
//
//        mockMvc.perform(get("/excursions/searchName"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//
//        verify(excursionService, times(1)).getExcursions();
//    }
//
//    @Test
//    public void searchExcursions_WithSearchTerm() throws Exception {
//        String searchTerm = "Mountain";
//
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        List<Excursion> mockExcursions = Arrays.asList(
//                Excursion.builder().id(1L).name("Mountain 1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
//                Excursion.builder().id(2L).name("Mountains 2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
//        );
//
//        when(excursionService.searchExcursions(searchTerm, 0.0, Double.MAX_VALUE)).thenReturn(mockExcursions);
//
//        mockMvc.perform(get("/excursions/searchNameAndPrice").param("searchTerm", searchTerm))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//        verify(excursionService, times(1)).searchExcursions(searchTerm, 0.0, Double.MAX_VALUE);
//    }
//
//    @Test
//    public void searchExcursions_WithMinAndMaxPrice() throws Exception {
//        String minPrice = "200.0";
//        String maxPrice = "500.0";
//
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        List<Excursion> mockExcursions = Arrays.asList(
//                Excursion.builder().id(1L).name("Mountain 1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(250.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
//                Excursion.builder().id(2L).name("Mountains 2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(490.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
//        );
//
//        when(excursionService.searchExcursions(null, 200.0, 500.0)).thenReturn(mockExcursions);
//
//        mockMvc.perform(get("/excursions/searchNameAndPrice")
//                        .param("minPrice", minPrice)
//                        .param("maxPrice", maxPrice))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//
//        verify(excursionService, times(1)).searchExcursions(null, 200.0, 500.0);
//    }
//
//    @Test
//    public void testSearchExcursions_AllParametersNull() throws Exception {
//        LocalDate date = LocalDate.of(2014, 9, 16);
//        Date startDate = new Date(2028, 9, 16);
//        Date endDate = new Date(2028, 9, 24);
//        List<String> destinations = new ArrayList<>();
//        destinations.add("Rome");
//        destinations.add("Florance");
//
//        User mockUser = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        List<Excursion> mockExcursions = Arrays.asList(
//                Excursion.builder().id(1L).name("Mountain 1").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(250.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
//                Excursion.builder().id(2L).name("Mountains 2").destinations(destinations).startDate(startDate).endDate(endDate).travelAgency(mockUser).price(490.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
//        );
//
//        when(excursionService.searchExcursions(null, 0.0, Double.MAX_VALUE)).thenReturn(mockExcursions);
//
//        mockMvc.perform(get("/excursions/searchNameAndPrice"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)));
//
//        verify(excursionService, times(1)).searchExcursions(null, 0.0, Double.MAX_VALUE);
//    }
}