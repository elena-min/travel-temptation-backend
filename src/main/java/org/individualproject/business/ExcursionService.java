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

import java.util.List;
import java.util.Optional;

@Service
public class ExcursionService {
    private ExcursionRepository excursionRepository;
    @Autowired
    public ExcursionService(ExcursionRepository exRepository){
        this.excursionRepository = exRepository;
    }
    public List<ExcursionEntity> getExcursions() {
        return excursionRepository.findAll();
    }
    public Optional<ExcursionEntity> getExcursion(Long id) {
        return excursionRepository.findById(id);
    }

    public ExcursionEntity createExcursion(CreateExcursionRequest request){
        ExcursionEntity newExcursion = ExcursionEntity.builder()
                .name(request.getName())
                .destinations(request.getDestinations())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .travelAgency(request.getTravelAgency())
                .price(request.getPrice())
                .build();
        return  excursionRepository.save(newExcursion);
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
            existingExcursion.setDestinations(request.getDestinations());
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

    public Optional<ExcursionEntity> getExcursionByName(String name){
        return excursionRepository.findByName(name);

    }

}
