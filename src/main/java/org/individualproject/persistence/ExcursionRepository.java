package org.individualproject.persistence;

import org.individualproject.persistence.entity.ExcursionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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


    @Query("select e from ExcursionEntity e where e.name = ?1")
    ExcursionEntity getExcursionByName(String name);

    Optional<ExcursionEntity> findByName(String name);

    List<ExcursionEntity> findByNameContainingIgnoreCase(String name);

    @Query("select e from ExcursionEntity e where e.price >= ?1 and e.price <= ?2 ")
    List<ExcursionEntity> findByPriceRange(double minPrice, double maxPrice);

    List<ExcursionEntity> findByPriceGreaterThan(double price);

    List<ExcursionEntity> findByPriceLessThan(double price);

}
