package org.individualproject.business;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.individualproject.domain.CreateExcursionRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.implementation.FakePostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//This annotation says to use the MockitoExtension class, who is responsible for initializing the Mockito framework for the tests
@ExtendWith(MockitoExtension.class)
class ExcursionServiceTest {

    //Creates a Mock object of this class
    @Mock
    private ExcursionRepository excursionRepository;

    //This object is going to be initialized using the Mock objects
    @InjectMocks
    private ExcursionService excursionService;
    @Test
    void getExcursions() {
        // Arrange
        List<Excursion> fakeExcursions = Arrays.asList(
                new Excursion(
                        1L,
                        "Mountain Hike",
                        Arrays.asList("Mount Everest Base Camp", "Annapurna Circuit"),
                        new Date(), // replace with actual date
                        new Date(), // replace with actual date
                        "Adventure Tours",
                        1500.0
                ),

          new Excursion(
                2L,
                "City Tour",
                Arrays.asList("Paris", "Rome", "Barcelona"),
                new Date(), // replace with actual date
                new Date(), // replace with actual date
                "City Explorers",
                1200.0
        ),

        new Excursion(
                3L,
                "Beach Getaway",
                Arrays.asList("Maldives", "Phuket", "Bora Bora"),
                new Date(), // replace with actual date
                new Date(), // replace with actual date
                "Sunshine Travel",
                2000.0
        ));
        when(excursionRepository.getExcursions()).thenReturn(fakeExcursions);

        // Act
        List<Excursion> result = excursionService.getExcursions();

        // Assert
        assertEquals(fakeExcursions, result);
    }

    @Test
    void getExcursion() {
        Excursion fakeExcursion = new Excursion(
                        1L,
                        "Mountain Hike",
                        Arrays.asList("Mount Everest Base Camp", "Annapurna Circuit"),
                        new Date(), // replace with actual date
                        new Date(), // replace with actual date
                        "Adventure Tours",
                        1500.0
                );
        when(excursionRepository.getExcursion(1)).thenReturn(Optional.of(fakeExcursion));
        // Act
        Optional<Excursion> result = excursionService.getExcursion(1);

        // Assert
        assertEquals(Optional.of(fakeExcursion), result);
    }

    @Test
    void createExcursion() {
        CreateExcursionRequest createRequest = new CreateExcursionRequest();
        createRequest.setName("Test Excursion");
        createRequest.setDestinations(Arrays.asList("Destination1", "Destination2"));
        createRequest.setStartDate(new Date(System.currentTimeMillis() + 1000000));
        createRequest.setEndDate(new Date(System.currentTimeMillis() + 2000000));
        createRequest.setTravelAgency("Test Agency");
        createRequest.setPrice(1000.0);
        //Act
        excursionService.createExcursion(createRequest);

        //Assert
        //This verifies (using the Mockito method verify) that the method has been called only 1 time
        verify(excursionRepository, times(1)).createExcursion(any(Excursion.class));
    }
}