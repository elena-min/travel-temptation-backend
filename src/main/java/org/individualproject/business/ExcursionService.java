package org.individualproject.business;

import lombok.AllArgsConstructor;
import org.individualproject.business.converter.ExcursionConverter;
import org.individualproject.business.converter.UserConverter;
import org.individualproject.business.exception.InvalidExcursionDataException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.configuration.security.token.AccessToken;
import org.individualproject.domain.CreateExcursionRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateExcursionRequest;
import org.individualproject.domain.User;
import org.individualproject.domain.enums.UserRole;
import org.individualproject.persistence.ExcursionRepository;
import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExcursionService {
    private ExcursionRepository excursionRepository;
    private AccessToken requestAccessToken;
    public List<Excursion> getExcursions() {
        List<ExcursionEntity> excursionEntities = excursionRepository.findAll();
        return ExcursionConverter.mapToDomainList(excursionEntities);
    }
    public Optional<Excursion> getExcursion(Long id) {
        Optional<ExcursionEntity> excursionEntity = excursionRepository.findById(id);
        return excursionEntity.map(ExcursionConverter::mapToDomain);
    }

    public Excursion createExcursion(CreateExcursionRequest request){
        if (request.getName() == null || request.getDestinations() == null || request.getStartDate() == null ||
                request.getEndDate() == null || request.getTravelAgency() == null || request.getPrice() < 0 ||
                request.getNumberOfAvaliableSpaces() < 0) {
            throw new InvalidExcursionDataException("Invalid input data");
        }

        if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
            throw new UnauthorizedDataAccessException("Only travel agencies can list excursions!");
        }

        UserEntity userEntity = UserConverter.convertToEntity(request.getTravelAgency());
        ExcursionEntity newExcursion = ExcursionEntity.builder()
                .name(request.getName())
                .destinations(String.join(",", request.getDestinations()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .travelAgency(userEntity)
                .price(request.getPrice())
                .numberOfAvaliableSpaces(request.getNumberOfAvaliableSpaces())
                .numberOfSpacesLeft(request.getNumberOfAvaliableSpaces())
                .build();

        ExcursionEntity excursionEntity = excursionRepository.save(newExcursion);
        return ExcursionConverter.mapToDomain(excursionEntity);
    }

    public boolean deleteExcursion(Long id) {
        Optional<ExcursionEntity> optionalExcursion = excursionRepository.findById(id);

        if (optionalExcursion.isPresent()) {
            ExcursionEntity excursion = optionalExcursion.get();
            if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
                throw new UnauthorizedDataAccessException("ONLY_TRAVEL_AGENCIES_ALLOWED");
            }

            if (!requestAccessToken.getUserID().equals(excursion.getTravelAgency().getId())) {
                throw new UnauthorizedDataAccessException("EXCURSION_NOT_OWNED_BY_LOGGED_IN_USER");
            }

            excursionRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateExcursion(UpdateExcursionRequest request) {
        Optional<ExcursionEntity> optionalExcursion = excursionRepository.findById(request.getId());

        if (optionalExcursion.isPresent()) {
            ExcursionEntity existingExcursion = optionalExcursion.get();

            if (!requestAccessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
                throw new UnauthorizedDataAccessException("ONLY_TRAVEL_AGENCIES_ALLOWED");
            }

            if (!requestAccessToken.getUserID().equals(existingExcursion.getTravelAgency().getId())) {
                throw new UnauthorizedDataAccessException("EXCURSION_NOT_OWNED_BY_LOGGED_IN_USER");
            }

            existingExcursion.setName(request.getName());
            List<String> destinations = request.getDestinations();
            String destinationsString = String.join(",", destinations);
            existingExcursion.setDestinations(destinationsString);
            existingExcursion.setStartDate(request.getStartDate());
            existingExcursion.setEndDate(request.getEndDate());
            existingExcursion.setPrice(request.getPrice());
            existingExcursion.setNumberOfAvaliableSpaces(request.getNumberOfAvaliableSpaces());
            excursionRepository.save(existingExcursion);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Excursion> getExcursionByName(String name){
        Optional<ExcursionEntity> excursionEntity = excursionRepository.findByName(name);
        return excursionEntity.map(ExcursionConverter::mapToDomain);
    }


    public void bookSpaces(Long id, int spacesBooked){
        int updatedRows = excursionRepository.decrementSpacesLeft(id, spacesBooked);
        if(updatedRows == 0){
            throw new IllegalStateException("Not enough spaces left for this excursion!");
        }
    }

    public List<Excursion> getExcursionsByTravelAgency(User travelAgency) {
        UserEntity userEntity = UserConverter.convertToEntity(travelAgency);
        List<ExcursionEntity> excursionEntities = excursionRepository.findByTravelAgency(userEntity);
        return ExcursionConverter.mapToDomainList(excursionEntities);
    }

    public List<Excursion> searchExcursionsByNameAndTravelAgency(String searchTerm) {
        List<ExcursionEntity> excursionEntities = excursionRepository.findByNameContainingIgnoreCaseOrTravelAgency_FirstNameContainingIgnoreCaseOrTravelAgency_LastNameContainingIgnoreCase(searchTerm, searchTerm, searchTerm);
        return ExcursionConverter.mapToDomainList(excursionEntities);
    }

    public List<Excursion> searchExcursions(String searchTerm, double minPrice, double maxPrice) {
        if ((searchTerm == null || searchTerm.isEmpty()) && (minPrice == 0 && maxPrice == Double.MAX_VALUE)) {
            return ExcursionConverter.mapToDomainList(excursionRepository.findAll());
        }

        if (searchTerm == null) {
            searchTerm = "";
        }

        if (searchTerm.isEmpty() && (minPrice > 0 || maxPrice < Double.MAX_VALUE)) {
            return ExcursionConverter.mapToDomainList(
                    excursionRepository.findByPriceBetween(minPrice, maxPrice)
            );
        } else if (searchTerm.isEmpty()) {
            return ExcursionConverter.mapToDomainList(excursionRepository.findAll());
        } else if (minPrice == 0 && maxPrice == Double.MAX_VALUE) {
            return ExcursionConverter.mapToDomainList(
                    excursionRepository.findByNameContainingIgnoreCaseOrTravelAgency_FirstNameContainingIgnoreCaseOrTravelAgency_LastNameContainingIgnoreCase(
                            searchTerm, searchTerm, searchTerm
                    )
            );
        } else {
            return ExcursionConverter.mapToDomainList(
                    excursionRepository.findByNameContainingIgnoreCaseAndPriceBetweenOrTravelAgency_FirstNameContainingIgnoreCaseAndPriceBetweenOrTravelAgency_LastNameContainingIgnoreCaseAndPriceBetween(
                            searchTerm, minPrice, maxPrice,
                            searchTerm, minPrice, maxPrice,
                            searchTerm, minPrice, maxPrice
                    )
            );
        }
    }
}
