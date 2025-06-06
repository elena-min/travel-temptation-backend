package org.individualproject.business.converter;

import org.individualproject.domain.Excursion;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;

import java.util.Arrays;
import java.util.List;

public class ExcursionConverter {
    public static Excursion mapToDomain(ExcursionEntity excursionEntity) {
        List<String> destinations = Arrays.asList(excursionEntity.getDestinations().split(","));
        User travelAgency = UserConverter.mapToDomain(excursionEntity.getTravelAgency());
        return Excursion.builder()
                .id(excursionEntity.getId())
                .name(excursionEntity.getName())
                .destinations(destinations)
                .description(excursionEntity.getDescription())
                .startDate(excursionEntity.getStartDate())
                .endDate(excursionEntity.getEndDate())
                .travelAgency(travelAgency)
                .price(excursionEntity.getPrice())
                .numberOfAvaliableSpaces(excursionEntity.getNumberOfAvaliableSpaces())
                .numberOfSpacesLeft(excursionEntity.getNumberOfSpacesLeft())
                .fileName(excursionEntity.getFileName())
                .build();
    }
    public static List<Excursion> mapToDomainList(List<ExcursionEntity> excursionEntities) {
        return excursionEntities.stream()
                .map(ExcursionConverter::mapToDomain)
                .toList();
    }

    public static ExcursionEntity convertToEntity(Excursion excursion){
        String destinationsAsString = String.join(",", excursion.getDestinations());
        UserEntity travelAgency = UserConverter.convertToEntity(excursion.getTravelAgency());
        return ExcursionEntity.builder()
                .id(excursion.getId())
                .name(excursion.getName())
                .destinations(destinationsAsString)
                .description(excursion.getDescription())
                .startDate(excursion.getStartDate())
                .endDate(excursion.getEndDate())
                .travelAgency(travelAgency)
                .price(excursion.getPrice())
                .numberOfAvaliableSpaces(excursion.getNumberOfAvaliableSpaces())
                .numberOfSpacesLeft(excursion.getNumberOfSpacesLeft())
                .fileName(excursion.getFileName())
                .build();
    }

    private ExcursionConverter(){}
}
