package org.individualproject.persistence;

import org.individualproject.domain.Excursion;

import java.util.List;
import java.util.Optional;

public interface ExcursionRepository {
    Optional<Excursion> getExcursion(long excursionID);
    List<Excursion> getExcursions();
    Long createExcursion(Excursion newExcursion);

    boolean deleteExcursion(long excursionID);

    boolean updateExcursion(Excursion excursionToUpdate);
    Optional<Excursion> getExcursionByName(String excursionName);




}
