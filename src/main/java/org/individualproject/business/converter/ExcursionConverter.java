package org.individualproject.business.converter;

import org.individualproject.domain.Excursion;
import org.individualproject.domain.User;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExcursionConverter {
    public static Excursion mapToDomain(ExcursionEntity excursionEntity) {
        List<String> destinations = Arrays.asList(excursionEntity.getDestinations().split(","));
        Excursion excursion = Excursion.builder()
                .id(excursionEntity.getId())
                .name(excursionEntity.getName())
                .destinations(destinations)
                .startDate(excursionEntity.getStartDate())
                .endDate(excursionEntity.getEndDate())
                .travelAgency(excursionEntity.getTravelAgency())
                .price(excursionEntity.getPrice())
                .numberOfAvaliableSpaces(excursionEntity.getNumberOfAvaliableSpaces())
                .build();
        return excursion;
    }
    public static List<Excursion> mapToDomainList(List<ExcursionEntity> excursionEntities) {
        return excursionEntities.stream()
                .map(ExcursionConverter::mapToDomain)
                .collect(Collectors.toList());
    }

    public static ExcursionEntity convertToEntity(Excursion excursion){
        String destinationsAsString = String.join(",", excursion.getDestinations());
        ExcursionEntity excursionEntity = ExcursionEntity.builder()
                .id(excursion.getId())
                .name(excursion.getName())
                .destinations(destinationsAsString)
                .startDate(excursion.getStartDate())
                .endDate(excursion.getEndDate())
                .travelAgency(excursion.getTravelAgency())
                .price(excursion.getPrice())
                .numberOfAvaliableSpaces(excursion.getNumberOfAvaliableSpaces())
                .build();
        return excursionEntity;
    }
}
