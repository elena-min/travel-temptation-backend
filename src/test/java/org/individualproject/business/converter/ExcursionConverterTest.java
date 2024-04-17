package org.individualproject.business.converter;

import org.individualproject.domain.Excursion;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcursionConverterTest {

    @Test
    void mapToDomain() {
        ExcursionEntity excursionEntity = ExcursionEntity.builder()
                .id(1L)
                .name("City Tour")
                .destinations("Rome,Florance")
                .startDate(new Date(124, 4, 16))
                .endDate(new Date(124, 4, 26))
                .travelAgency("Agency 1")
                .price(100.00)
                .numberOfAvaliableSpaces(30)
                .build();

        Excursion excursion = ExcursionConverter.mapToDomain(excursionEntity);
        List<String> destinationsList = Arrays.asList(excursionEntity.getDestinations().split(","));

        //Assert

        assertEquals(excursionEntity.getId(), excursion.getId());
        assertEquals(excursionEntity.getName(), excursion.getName());
        assertEquals(destinationsList, excursion.getDestinations());
        assertEquals(excursionEntity.getStartDate(), excursion.getStartDate());
        assertEquals(excursionEntity.getEndDate(), excursion.getEndDate());
        assertEquals(excursionEntity.getTravelAgency(), excursion.getTravelAgency());
        assertEquals(excursionEntity.getPrice(), excursion.getPrice());
        assertEquals(excursionEntity.getNumberOfAvaliableSpaces(), excursion.getNumberOfAvaliableSpaces());
    }

    @Test
    void mapToDomainList() {
        List<ExcursionEntity> excursionEntityList = new ArrayList<>();
        ExcursionEntity excursionEntity1 = ExcursionEntity.builder()
                .id(1L)
                .name("City Tour")
                .destinations("Rome,Florance")
                .startDate(new Date(124, 4, 16))
                .endDate(new Date(124, 4, 26))
                .travelAgency("Agency 1")
                .price(100.00)
                .numberOfAvaliableSpaces(30)
                .build();

        ExcursionEntity excursionEntity2 = ExcursionEntity.builder()
                .id(2L)
                .name("Bahamas Bliss")
                .destinations("Bora Bora,Bahamas")
                .startDate(new Date(124, 9, 16))
                .endDate(new Date(124, 9, 26))
                .travelAgency("Agency 2")
                .price(700.00)
                .numberOfAvaliableSpaces(20)
                .build();
        excursionEntityList.add(excursionEntity1);
        excursionEntityList.add(excursionEntity2);

        //Act
        List<Excursion> excursions = ExcursionConverter.mapToDomainList(excursionEntityList);

        //Assert
        assertEquals(2, excursions.size());
        assertEquals(excursionEntity1.getId(), excursions.get(0).getId());
        assertEquals(excursionEntity1.getName(), excursions.get(0).getName());
        assertEquals(Arrays.asList("Rome", "Florance"), excursions.get(0).getDestinations()); // Convert destinations string to list
        assertEquals(excursionEntity1.getStartDate(), excursions.get(0).getStartDate());
        assertEquals(excursionEntity1.getEndDate(), excursions.get(0).getEndDate());
        assertEquals(excursionEntity1.getTravelAgency(), excursions.get(0).getTravelAgency());
        assertEquals(excursionEntity1.getPrice(), excursions.get(0).getPrice());
        assertEquals(excursionEntity1.getNumberOfAvaliableSpaces(), excursions.get(0).getNumberOfAvaliableSpaces());

        assertEquals(excursionEntity2.getId(), excursions.get(1).getId());
        assertEquals(excursionEntity2.getName(), excursions.get(1).getName());
        assertEquals(Arrays.asList("Bora Bora", "Bahamas"), excursions.get(1).getDestinations()); // Convert destinations string to list
        assertEquals(excursionEntity2.getStartDate(), excursions.get(1).getStartDate());
        assertEquals(excursionEntity2.getEndDate(), excursions.get(1).getEndDate());
        assertEquals(excursionEntity2.getTravelAgency(), excursions.get(1).getTravelAgency());
        assertEquals(excursionEntity2.getPrice(), excursions.get(1).getPrice());
        assertEquals(excursionEntity2.getNumberOfAvaliableSpaces(), excursions.get(1).getNumberOfAvaliableSpaces());
    }

    @Test
    void convertToEntity() {
        List<String> destinations = new ArrayList<>();
        destinations.add("Rome");
        destinations.add("Florance");
        Excursion excursion = Excursion.builder()
                .id(1L)
                .name("City Tour")
                .destinations(destinations)
                .startDate(new Date(124, 4, 16))
                .endDate(new Date(124, 4, 26))
                .travelAgency("Agency 1")
                .price(100.00)
                .numberOfAvaliableSpaces(30)
                .build();

        ExcursionEntity excursionEntity = ExcursionConverter.convertToEntity(excursion);
        String destinationsAsString = String.join(",", destinations);

        //Assert
        assertEquals(excursion.getId(), excursionEntity.getId());
        assertEquals(excursion.getName(), excursionEntity.getName());
        assertEquals(destinationsAsString, excursionEntity.getDestinations());
        assertEquals(excursion.getStartDate(), excursionEntity.getStartDate());
        assertEquals(excursion.getEndDate(), excursionEntity.getEndDate());
        assertEquals(excursion.getTravelAgency(), excursionEntity.getTravelAgency());
        assertEquals(excursion.getPrice(), excursionEntity.getPrice());
        assertEquals(excursion.getNumberOfAvaliableSpaces(), excursionEntity.getNumberOfAvaliableSpaces());
    }
}