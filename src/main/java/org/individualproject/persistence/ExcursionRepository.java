package org.individualproject.persistence;

import org.individualproject.domain.Excursion;

import java.util.List;

public interface ExcursionRepository {
    Excursion getExcursion(long excursionID);
    List<Excursion> getExcursions();
    Long createExcursion(Excursion newExcursion);




}
