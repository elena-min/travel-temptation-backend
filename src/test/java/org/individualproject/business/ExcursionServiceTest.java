package org.individualproject.business;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.InvalidExcursionDataException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.Gender;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
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
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

//This annotation says to use the MockitoExtension class, who is responsible for initializing the Mockito framework for the tests
@ExtendWith(MockitoExtension.class)
class ExcursionServiceTest {

    //Creates a Mock object of this class
    @Mock
    private ExcursionRepository excursionRepository;
    @Mock
    private AccessToken accessToken;
    //This object is going to be initialized using the Mock objects
    @InjectMocks
    private ExcursionService excursionService;
    @Test
    void getExcursions_shouldReturnExcursions() {
        // Arrange
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);


        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        List<ExcursionEntity> allExcursionEntities = Arrays.asList(
                ExcursionEntity.builder().id(1L).name("Excursion 1").destinations("Paris,London").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
                ExcursionEntity.builder().id(2L).name("Excursion 2").destinations("New York,Boston").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
        );

        List<Excursion> excursions = ExcursionConverter.mapToDomainList(allExcursionEntities);

        when(excursionRepository.findAll()).thenReturn(allExcursionEntities);

        // Act
        List<Excursion> result = excursionService.getExcursions();

        // Assert
        assertEquals(excursions, result);
        verify(excursionRepository, times(1)).findAll();
    }

    @Test
    void getExcursions_shouldReturnEmptyListWithNoExcursions(){
        // Arrange
        when(excursionRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        List<Excursion> result = excursionService.getExcursions();

        //Assert
        assertTrue(result.isEmpty());
        verify(excursionRepository, times(1)).findAll();

    }

    @Test
    void getExcursion_shouldReturnExcursion() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);


        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();

        ExcursionEntity fakeExcursion = ExcursionEntity.builder()
                .id(1L)
                .name("Mountain Hike")
                .destinations("Mount Everest Base Camp, Annapurna Circuit")
                .startDate(startDate) // replace with actual date
                .endDate(endDate) // replace with actual date
                .travelAgency(userEntity)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();

        Excursion expectedExcursion = ExcursionConverter.mapToDomain(fakeExcursion);
        Long id = 1L;
        when(excursionRepository.findById(id)).thenReturn(Optional.of(fakeExcursion));

        // Act
        Optional<Excursion> result = excursionService.getExcursion(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedExcursion, result.get());
        verify(excursionRepository, times(1)).findById(id);
    }

    @Test
    void getExcursion_nonExistingExcursion() {
        Long id = 1L;
        when(excursionRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Excursion> result = excursionService.getExcursion(id);

        // Assert
        assertTrue(result.isEmpty());
        verify(excursionRepository, times(1)).findById(id);
    }

    @Test
    void createExcursion_shouldCreateExcursion() {
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);


        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);
        ExcursionEntity newExcursionEntity = ExcursionEntity.builder()
                .id(1L)
                .name("Mountain Hike")
                .destinations("Mount Everest Base Camp, Annapurna Circuit")
                .startDate(startDate) // replace with actual date
                .endDate(endDate) // replace with actual date
                .travelAgency(userEntity)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();

        CreateExcursionRequest createRequest = CreateExcursionRequest.builder()
                .name("Mountain Hike")
                .destinations(Arrays.asList("Paris", "London"))
                .startDate(startDate) // replace with actual date
                .endDate(endDate) // replace with actual date
                .travelAgency(user)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .build();

        // Mock the excursionRepository
        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);
        when(excursionRepository.save(any(ExcursionEntity.class))).thenReturn(newExcursionEntity);

        Excursion expectedExcursion = ExcursionConverter.mapToDomain(newExcursionEntity);
        // Act:

        Excursion result = excursionService.createExcursion(createRequest);

        // Assert
        assertEquals(expectedExcursion, result);
        verify(excursionRepository, times(1)).save(any(ExcursionEntity.class));

    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsParams")
    void createExcursion_shouldThrowExceptionForInvalidInput(CreateExcursionRequest invalidRequest) {
        assertThrows(InvalidExcursionDataException.class, () -> excursionService.createExcursion(invalidRequest));
        verify(excursionRepository, never()).save(any());
    }

    private static Stream<Arguments> provideStringsForIsParams() {
        Date startDate = new Date();
        Date endDate = new Date();
        User validUser = new User(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com", "hashedPassword1", Gender.MALE);

        return Stream.of(
                Arguments.of(new CreateExcursionRequest(null, Arrays.asList("Paris", "London"), startDate, endDate, validUser, 100.0, 50)), // Invalid name
                Arguments.of(new CreateExcursionRequest("Excursion Name", null, startDate, endDate, validUser, 100.0, 50)), // Invalid destinations
                Arguments.of(new CreateExcursionRequest("Excursion Name", Arrays.asList("Paris", "London"), null, endDate, validUser, 100.0, 50)), // Invalid start date
                Arguments.of(new CreateExcursionRequest("Excursion Name", Arrays.asList("Paris", "London"), startDate, null, validUser, 100.0, 50)), // Invalid end date
                Arguments.of(new CreateExcursionRequest("Excursion Name", Arrays.asList("Paris", "London"), startDate, endDate, null, 100.0, 50)), // Invalid travel agency
                Arguments.of(new CreateExcursionRequest("Excursion Name", Arrays.asList("Paris", "London"), startDate, endDate, validUser, -1.0, 50)), // Invalid price
                Arguments.of(new CreateExcursionRequest("Excursion Name", Arrays.asList("Paris", "London"), startDate, endDate, validUser, 100.0, -1)) // Invalid number of available spaces
        );
    }

    @Test
    void updateExcursion_shouldUpdateExistingExcursionWithValidInput(){
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);

        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);
        ExcursionEntity existingExcursionEntity = ExcursionEntity.builder()
                .id(1L)
                .name("Mountain Hike")
                .destinations("Mount Everest Base Camp, Annapurna Circuit")
                .startDate(startDate) // replace with actual date
                .endDate(endDate) // replace with actual date
                .travelAgency(userEntity)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();

        UpdateExcursionRequest updateExcursionRequest = new UpdateExcursionRequest(
                1L,
                "Test Excursion",
                Arrays.asList("Paris", "London"),
                startDate,
                endDate,

                1000.0,
                23
                );

        when(excursionRepository.findById(1L)).thenReturn(Optional.of(existingExcursionEntity));

        boolean updateResult = excursionService.updateExcursion(updateExcursionRequest);

        // Assert
        assertTrue(updateResult);
        verify(excursionRepository).save(existingExcursionEntity);
        assertEquals("Test Excursion", existingExcursionEntity.getName());
        assertEquals("Paris,London", existingExcursionEntity.getDestinations());
        assertEquals(startDate, existingExcursionEntity.getStartDate());
        assertEquals(endDate, existingExcursionEntity.getEndDate());
        assertEquals(userEntity, existingExcursionEntity.getTravelAgency());
        assertEquals(1000.0, existingExcursionEntity.getPrice());
        assertEquals(23, existingExcursionEntity.getNumberOfAvaliableSpaces());

    }

    @Test
    void updateExcursion_shouldReturnFalseWhenExcursionDoesNotExist(){
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
        User user = UserConverter.mapToDomain(userEntity);

        UpdateExcursionRequest updateExcursionRequest = new UpdateExcursionRequest(
                1L,
                "Test Excursion",
                Arrays.asList("Paris", "London"),
                startDate,
                endDate,
                1000.0,
                23
        );
        when(excursionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act:
        boolean updateResult = excursionService.updateExcursion(updateExcursionRequest);

        // Assert
        assertFalse(updateResult);
        verify(excursionRepository, never()).save(any(ExcursionEntity.class));

    }

    @Test
    void deleteExcursion_shouldDeleteExistingExcursion(){
        Long id = 1L;
        Mockito.doNothing().when(excursionRepository).deleteById(id);

        // Act
        boolean result = excursionService.deleteExcursion(id);

        // Assert
        assertTrue(result);
        verify(excursionRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteExcursion_nonExistingExcursion(){
        Long nonExistingId = 9987L;
        doThrow(EmptyResultDataAccessException.class).when(excursionRepository).deleteById(nonExistingId);

        // Act
        boolean result = excursionService.deleteExcursion(nonExistingId);

        // Assert
        assertFalse(result);
        verify(excursionRepository, times(1)).deleteById(nonExistingId);
    }

    @Test
    void getExcursionByName_shouldReturnExistingExcursion(){
        String name = "Mountain Hike";
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);


        UserEntity userEntity = UserEntity.builder().id(1L).firstName("John").lastName("Doe").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();

        ExcursionEntity fakeExcursion = ExcursionEntity.builder()
                .id(1L)
                .name(name)
                .destinations("Mount Everest Base Camp, Annapurna Circuit")
                .startDate(startDate) // replace with actual date
                .endDate(endDate) // replace with actual date
                .travelAgency(userEntity)
                .price(1500.0)
                .numberOfAvaliableSpaces(58)
                .numberOfSpacesLeft(58)
                .build();

        Excursion expectedExcursion = ExcursionConverter.mapToDomain(fakeExcursion);
        when(excursionRepository.findByName(name)).thenReturn(Optional.of(fakeExcursion));

        // Act
        Optional<Excursion> result = excursionService.getExcursionByName(name);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedExcursion, result.get());
        verify(excursionRepository, times(1)).findByName(name);

    }

    @Test
    void getExcursionByName_nonExistingExcursion() {
        String name = "Mountain Hike";
        when(excursionRepository.findByName(name)).thenReturn(Optional.empty());

        // Act
        Optional<Excursion> result = excursionService.getExcursionByName(name);

        // Assert
        assertTrue(result.isEmpty());
        verify(excursionRepository, times(1)).findByName(name);
    }

    @Test
    void bookSpaces_shouldDecrementSpacesWhenEnoughAvailable(){
        // Arrange
        Long id = 1L;
        int availableSpaces = 10;
        int spacesBooked = 3;
        when(excursionRepository.decrementSpacesLeft(id, spacesBooked)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> excursionService.bookSpaces(id, spacesBooked));

        // Assert
        verify(excursionRepository).decrementSpacesLeft(id, spacesBooked);
    }

    @Test
    void bookSpaces_shouldThrowExceptionWhenNotEnoughAvailableSpaces() {
        // Arrange
        Long id = 1L;
        int availableSpaces = 2;
        int spacesBooked = 5;
        when(excursionRepository.decrementSpacesLeft(id, spacesBooked)).thenReturn(0);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> excursionService.bookSpaces(id, spacesBooked));
        assertEquals("Not enough spaces left for this excursion!", exception.getMessage());

        // Verify
        verify(excursionRepository).decrementSpacesLeft(id, spacesBooked);
    }

    @Test
    void getExcursionsByTravelAgency_shouldReturnExcursionsForAuthorizedTravelAgency(){
        LocalDate date = LocalDate.of(2014, 9, 16);
        Date startDate = new Date(2028, 9, 16);
        Date endDate = new Date(2028, 9, 24);
        User travelAgency = User.builder().id(1L).firstName("Travel").lastName("Agency").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();


        UserEntity userEntity = UserConverter.convertToEntity(travelAgency);
        List<ExcursionEntity> excursionEntities = Arrays.asList(
                ExcursionEntity.builder().id(1L).name("Excursion 1").destinations("Paris,London").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(100.0).numberOfAvaliableSpaces(50).numberOfSpacesLeft(50).build(),
                ExcursionEntity.builder().id(2L).name("Excursion 2").destinations("New York,Boston").startDate(startDate).endDate(endDate).travelAgency(userEntity).price(200.0).numberOfAvaliableSpaces(40).numberOfSpacesLeft(40).build()
        );
        when(excursionRepository.findByTravelAgency(userEntity)).thenReturn(excursionEntities);

        // Act
        List<Excursion> excursions = excursionService.getExcursionsByTravelAgency(travelAgency);

        // Assert
        assertEquals(2, excursions.size());
        assertEquals("Excursion 1", excursions.get(0).getName());
        assertEquals("Excursion 2", excursions.get(1).getName());

    }

//    @Test
//    void getExcursionsByTravelAgency_shouldThrowExceptionForNonTravelAgencyUser() {
//        // Arrange
//        LocalDate date = LocalDate.of(2014, 9, 16);
//
//        User travelAgency = User.builder().id(1L).firstName("Travel").lastName("Agency").birthDate(date).email("j.doe@example.com").hashedPassword("hashedPassword1").gender(Gender.MALE).build();
//        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
//        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(false);
//        // Simulate a different user ID in the access token
//        when(accessToken.getUserID()).thenReturn(2L);
//
//        // Act & Assert
//        UnauthorizedDataAccessException exception = assertThrows(UnauthorizedDataAccessException.class,
//                () -> excursionService.getExcursionsByTravelAgency(travelAgency));
//        assertEquals( new UnauthorizedDataAccessException("USER_ID_NOT_FROM_LOGGED_IN_USER"), exception);
//    }
//
//    @Test
//    void getExcursionsByTravelAgency_shouldThrowExceptionForTravelAgencyRoleButNotOwnName() {
//        // Arrange
//        User travelAgency = User.builder().id(1L).firstName("Travel").lastName("Agency").build();
//        User travelAgency2 = User.builder().id(2L).firstName("Travel2").lastName("Agency2").build();
//
//        // Mock that the user does not have the travel agency role
//        when(accessToken.hasRole(UserRole.ADMIN.name())).thenReturn(false);
//        when(accessToken.hasRole(UserRole.TRAVELAGENCY.name())).thenReturn(true);
//
//        // Act & Assert
//        UnauthorizedDataAccessException exception = assertThrows(UnauthorizedDataAccessException.class,
//                () -> excursionService.getExcursionsByTravelAgency(travelAgency2));
//        assertEquals("Only travel agencies see own listings!", exception.getMessage());
//    }



}