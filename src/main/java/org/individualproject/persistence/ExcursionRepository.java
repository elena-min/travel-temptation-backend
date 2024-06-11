package org.individualproject.persistence;

import org.individualproject.persistence.entity.ExcursionEntity;
import org.individualproject.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExcursionRepository extends JpaRepository<ExcursionEntity, Long> {
    @Query("UPDATE ExcursionEntity e SET e.numberOfSpacesLeft = e.numberOfSpacesLeft - :spacesBooked WHERE e.id = :id AND e.numberOfSpacesLeft >= :spacesBooked")
    int decrementSpacesLeft(@Param("id") Long id, @Param("spacesBooked") int spacesBooked);

    Optional<ExcursionEntity> findByName(String name);

    List<ExcursionEntity> findByNameContainingIgnoreCaseOrTravelAgency_FirstNameContainingIgnoreCaseOrTravelAgency_LastNameContainingIgnoreCase(
            String excursionName, String travelAgencyFirstName, String travelAgencyLastName);

    List<ExcursionEntity> findByPriceBetween(double minPrice, double maxPrice);

    List<ExcursionEntity> findByNameContainingIgnoreCaseAndPriceBetweenOrTravelAgency_FirstNameContainingIgnoreCaseAndPriceBetweenOrTravelAgency_LastNameContainingIgnoreCaseAndPriceBetween(
            String excursionName, double minPrice, double maxPrice,
            String travelAgencyFirstName, double minPriceFname, double maxPriceFname,
            String travelAgencyLastName, double minPriceLname, double maxPriceLname
    );

    List<ExcursionEntity> findByTravelAgency(UserEntity travelAgency);

    void deleteByTravelAgency(UserEntity travelAgency);
}
