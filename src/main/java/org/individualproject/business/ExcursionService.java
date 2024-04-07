package org.individualproject.business;

import org.individualproject.domain.CreateExcursionRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateExcursionRequest;
import org.individualproject.persistence.ExcursionRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Excursion> getExcursions() {
        List<Excursion> excursions = excursionRepository.getExcursions().stream().toList();
        return excursions;
    }
    public Optional<Excursion> getExcursion(Integer id) {
        return excursionRepository.getExcursion(id);
    }

    public Long createExcursion(CreateExcursionRequest request){
        Excursion newExcursion = Excursion.builder()
                .name(request.getName())
                .destinations(request.getDestinations())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .travelAgency(request.getTravelAgency())
                .price(request.getPrice())
                .build();
        return  excursionRepository.createExcursion(newExcursion);
    }

    public boolean deleteExcursion(Integer id){
        return excursionRepository.deleteExcursion(id);
    }

    public boolean updateExcursion(UpdateExcursionRequest request) {
        Optional<Excursion> optionalExcursion = excursionRepository.getExcursion(request.getId());
        if(optionalExcursion.isPresent()){
            Excursion existingExcursion = optionalExcursion.get();
            existingExcursion.setName(request.getName());
            existingExcursion.setDestinations(request.getDestinations());
            existingExcursion.setStartDate(request.getStartDate());
            existingExcursion.setEndDate(request.getEndDate());
            existingExcursion.setTravelAgency(request.getTravelAgency());
            existingExcursion.setPrice(request.getPrice());
            return excursionRepository.updateExcursion(existingExcursion);

        }
        else {
            return false;
        }
    }

    public Optional<Excursion> getExcursionByName(String name){
        return excursionRepository.getExcursionByName(name);

    }

}
