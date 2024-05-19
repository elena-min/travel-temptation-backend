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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ExcursionService {
    private ExcursionRepository excursionRepository;
    private AccessToken accessToken;
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

        if (!accessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
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
            UserEntity userEntity = UserConverter.convertToEntity(request.getTravelAgency());
            ExcursionEntity existingExcursion = optionalExcursion.get();
            existingExcursion.setName(request.getName());
            List<String> destinations = request.getDestinations();
            String destinationsString = String.join(",", destinations);
            existingExcursion.setDestinations(destinationsString);
            existingExcursion.setStartDate(request.getStartDate());
            existingExcursion.setEndDate(request.getEndDate());
            existingExcursion.setTravelAgency(userEntity);
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

    public List<Excursion> findExcursionsByName(String name){
        List<ExcursionEntity> excursionEntities = excursionRepository.findByNameContainingIgnoreCase(name);
        return ExcursionConverter.mapToDomainList(excursionEntities);
    }

    public List<Excursion> findExcursionsByPriceRange(String priceRange){
        List<ExcursionEntity> excursionEntities;
        if (priceRange.startsWith("<") || priceRange.startsWith(">")) {
            double price = Double.parseDouble(priceRange.substring(1)); // Remove the ">" or "<" symbol
            if (priceRange.startsWith(">")) {
                excursionEntities = excursionRepository.findByPriceGreaterThan(price);
            } else {
                excursionEntities = excursionRepository.findByPriceLessThan(price);
            }
        } else {
            String[] prices = priceRange.split("-");
            double minPrice = Double.parseDouble(prices[0]);
            double maxPrice = Double.parseDouble(prices[1]);
            excursionEntities = excursionRepository.findByPriceRange(minPrice, maxPrice);
        }
        return ExcursionConverter.mapToDomainList(excursionEntities);
    }

    public List<Excursion> searchExcursionsByNameAndPriceRange(String name, String priceRange) {
        List<ExcursionEntity> excursionEntities = new ArrayList<>();
        if (name != null && !name.isEmpty()) {
            // Search by both name and price range
            if (priceRange != null && !priceRange.isEmpty()) {
                if (priceRange.startsWith(">") || priceRange.endsWith(">")) {
                    double price = Double.parseDouble(priceRange.substring(1)); // Remove the ">" symbol
                    excursionEntities = excursionRepository.findByNameContainingIgnoreCaseAndPriceGreaterThan(name, price);
                } else if (priceRange.startsWith("<") || priceRange.endsWith("<")) {
                    double price = Double.parseDouble(priceRange.substring(1)); // Remove the "<" symbol
                    excursionEntities = excursionRepository.findByNameContainingIgnoreCaseAndPriceLessThan(name, price);
                } else {
                    String[] prices = priceRange.split("-");
                    double minPrice = Double.parseDouble(prices[0]);
                    double maxPrice = Double.parseDouble(prices[1]);
                    excursionEntities = excursionRepository.findByNameContainingIgnoreCaseAndPriceRange(name, minPrice, maxPrice);
                }
            } else {
                excursionEntities = excursionRepository.findByNameContainingIgnoreCase(name);
            }
        } else {
            // Search only by price range
            if (priceRange != null && !priceRange.isEmpty()) {
                if (priceRange.startsWith(">") || priceRange.endsWith(">")) {
                    double price = Double.parseDouble(priceRange.substring(1)); // Remove the ">" symbol
                    excursionEntities = excursionRepository.findByPriceGreaterThan(price);
                } else if (priceRange.startsWith("<") || priceRange.endsWith("<")) {
                    double price = Double.parseDouble(priceRange.substring(1)); // Remove the "<" symbol
                    excursionEntities = excursionRepository.findByPriceLessThan(price);
                } else {
                    String[] prices = priceRange.split("-");
                    double minPrice = Double.parseDouble(prices[0]);
                    double maxPrice = Double.parseDouble(prices[1]);
                    excursionEntities = excursionRepository.findByPriceRange(minPrice, maxPrice);
                }
            }
        }
        return ExcursionConverter.mapToDomainList(excursionEntities);
    }

    public void bookSpaces(Long id, int spacesBooked){
        int updatedRows = excursionRepository.decrementSpacesLeft(id, spacesBooked);
        if(updatedRows == 0){
            throw new IllegalStateException("Not enough spaces left for this excursion!");
        }
    }

    public List<Excursion> getExcursionsByTravelAgency(User travelAgency) {

        if (!accessToken.hasRole(UserRole.ADMIN.name())) {
            if (accessToken.getUserID() != travelAgency.getId()) {
                throw new UnauthorizedDataAccessException("USER_ID_NOT_FROM_LOGGED_IN_USER");
            }
        }

        if (!accessToken.hasRole(UserRole.TRAVELAGENCY.name())) {
            throw new UnauthorizedDataAccessException("Only travel agencies see own listings!");
        }

        UserEntity userEntity = UserConverter.convertToEntity(travelAgency);
        List<ExcursionEntity> excursionEntities = excursionRepository.findByTravelAgency(userEntity);
        return ExcursionConverter.mapToDomainList(excursionEntities);
    }


}
