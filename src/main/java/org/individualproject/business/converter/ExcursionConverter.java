package org.individualproject.business.converter;

import org.individualproject.domain.Excursion;
import org.individualproject.persistence.entity.ExcursionEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExcursionConverter {
    public static Excursion mapToDomain(ExcursionEntity excursionEntity) {
        List<String> destinations = Arrays.asList(excursionEntity.getDestinations().split(","));
        Excursion excursion = Excursion.builder()
                .name(excursionEntity.getName())
                .destinations(destinations)
                .startDate(excursionEntity.getStartDate())
                .endDate(excursionEntity.getEndDate())
                .travelAgency(excursionEntity.getTravelAgency())
                .price(excursionEntity.getPrice())
                .build();
        return excursion;
    }
    public static List<Excursion> mapToDomainList(List<ExcursionEntity> excursionEntities) {
        return excursionEntities.stream()
                .map(ExcursionConverter::mapToDomain)
                .collect(Collectors.toList());
    }
}
