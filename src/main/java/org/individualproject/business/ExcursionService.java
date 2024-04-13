package org.individualproject.business;

import org.individualproject.domain.CreateExcursionRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateExcursionRequest;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExcursionService {
    private ExcursionRepository excursionRepository;
    @Autowired
    public ExcursionService(ExcursionRepository exRepository){
        this.excursionRepository = exRepository;
    }
    public List<Excursion> getExcursions() {
        List<ExcursionEntity> excursionEntities = excursionRepository.findAll();
        return mapToDomainList(excursionEntities);
    }
    public Optional<Excursion> getExcursion(Long id) {
        Optional<ExcursionEntity> excursionEntity = excursionRepository.findById(id);
        return excursionEntity.map(this::mapToDomain);
    }

    public Excursion createExcursion(CreateExcursionRequest request){
        ExcursionEntity newExcursion = ExcursionEntity.builder()
                .name(request.getName())
                .destinations(String.join(",", request.getDestinations()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .travelAgency(request.getTravelAgency())
                .price(request.getPrice())
                .build();

        ExcursionEntity excursionEntity = excursionRepository.save(newExcursion);
        return mapToDomain(excursionEntity);
    }

    public boolean deleteExcursion(Long id) {
        try {
            excursionRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean updateExcursion(UpdateExcursionRequest request) {
        Optional<ExcursionEntity> optionalExcursion = excursionRepository.findById(request.getId());
        if (optionalExcursion.isPresent()) {
            ExcursionEntity existingExcursion = optionalExcursion.get();
            existingExcursion.setName(request.getName());
            List<String> destinations = request.getDestinations();
            String destinationsString = String.join(",", destinations);
            existingExcursion.setDestinations(destinationsString);
            existingExcursion.setStartDate(request.getStartDate());
            existingExcursion.setEndDate(request.getEndDate());
            existingExcursion.setTravelAgency(request.getTravelAgency());
            existingExcursion.setPrice(request.getPrice());
            excursionRepository.save(existingExcursion);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Excursion> getExcursionByName(String name){
        Optional<ExcursionEntity> excursionEntity = excursionRepository.findByName(name);
        return excursionEntity.map(this::mapToDomain);
    }

    public List<Excursion> findExcursionsByName(String name){
        List<ExcursionEntity> excursionEntities = excursionRepository.findByNameContainingIgnoreCase(name);
        return mapToDomainList(excursionEntities);
    }
    private Excursion mapToDomain(ExcursionEntity excursionEntity) {
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
    private List<Excursion> mapToDomainList(List<ExcursionEntity> excursionEntities) {
        return excursionEntities.stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }
}
