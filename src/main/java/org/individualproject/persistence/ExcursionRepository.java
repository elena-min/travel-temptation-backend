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
//    Optional<Excursion> getExcursion(long excursionID);
//    List<Excursion> getExcursions();
//    Long createExcursion(Excursion newExcursion);
//
//    boolean deleteExcursion(long excursionID);
//
//    boolean updateExcursion(Excursion excursionToUpdate);
//    Optional<Excursion> getExcursionByName(String excursionName);


    @Query("UPDATE ExcursionEntity e SET e.numberOfSpacesLeft = e.numberOfSpacesLeft - :spacesBooked WHERE e.id = :id AND e.numberOfSpacesLeft >= :spacesBooked")
    int decrementSpacesLeft(@Param("id") Long id, @Param("spacesBooked") int spacesBooked);

    @Query("select e from ExcursionEntity e where e.name = ?1")
    ExcursionEntity getExcursionByName(String name);

    Optional<ExcursionEntity> findByName(String name);

    List<ExcursionEntity> findByNameContainingIgnoreCase(String name);

    @Query("select e from ExcursionEntity e where e.price >= ?1 and e.price <= ?2 ")
    List<ExcursionEntity> findByPriceRange(double minPrice, double maxPrice);

    List<ExcursionEntity> findByPriceGreaterThan(double price);

    List<ExcursionEntity> findByPriceLessThan(double price);

    @Query("select e from ExcursionEntity e where lower(e.name) like %:name% and e.price >= :minPrice and e.price <= :maxPrice")
    List<ExcursionEntity> findByNameContainingIgnoreCaseAndPriceRange(@Param("name") String name, @Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

    List<ExcursionEntity> findByNameContainingIgnoreCaseAndPriceGreaterThan(String name,double price);

    List<ExcursionEntity> findByNameContainingIgnoreCaseAndPriceLessThan(String name, double price);

    List<ExcursionEntity> findByTravelAgency(UserEntity travelAgency);

}
