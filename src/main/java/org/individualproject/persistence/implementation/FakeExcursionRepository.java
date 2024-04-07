package org.individualproject.persistence.implementation;

import org.individualproject.domain.Excursion;
import org.individualproject.persistence.ExcursionRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FakeExcursionRepository implements ExcursionRepository {
    private final List<Excursion> excursions;
    private Excursion excursion1;
    private Excursion excursion2;
    private Excursion excursion3;
    private long nextId;

    public FakeExcursionRepository() {
        this.excursions = new ArrayList<>();
        nextId = 1L;
        excursion1 = new Excursion(
                nextId++,
                "Mountain Hike",
                Arrays.asList("Mount Everest Base Camp", "Annapurna Circuit"),
                new Date(), // replace with actual date
                new Date(), // replace with actual date
                "Adventure Tours",
                1500.0
        );

        excursion2 = new Excursion(
                nextId++,
                "City Tour",
                Arrays.asList("Paris", "Rome", "Barcelona"),
                new Date(), // replace with actual date
                new Date(), // replace with actual date
                "City Explorers",
                1200.0
        );

        excursion3 = new Excursion(
                nextId++,
                "Beach Getaway",
                Arrays.asList("Maldives", "Phuket", "Bora Bora"),
                new Date(), // replace with actual date
                new Date(), // replace with actual date
                "Sunshine Travel",
                2000.0
        );

        excursions.add(excursion1);
        excursions.add(excursion2);
        excursions.add(excursion3);
    }

    @Override
    public Optional<Excursion> getExcursion(long excursionID) {
        return this.excursions
                .stream() //converts the list into a stream
                .filter(excursion -> excursion.getId() == excursionID)
                .findFirst();
    }

    @Override
    public List<Excursion> getExcursions() {
        return Collections.unmodifiableList(excursions);
    }

    @Override
    public Long createExcursion(Excursion newExcursion) {
        newExcursion.setId(nextId);
        nextId++;
        excursions.add(newExcursion);
        return newExcursion.getId();
    }

    @Override
    public boolean deleteExcursion(long excursionID){
        Excursion excursionToRemove = null;
        for(Excursion excursion : excursions) {
            if (excursion.getId() == excursionID) {
                excursionToRemove = excursion;
                break;
            }
        }
         if(excursionToRemove != null){
             excursions.remove(excursionToRemove);
             return true;
         }
        return false;
    }

    @Override
    public boolean updateExcursion(Excursion excursionToUpdate){
        for(Excursion excursion : excursions) {
            if (excursion.getId() == excursionToUpdate.getId()) {
                int index = excursions.indexOf(excursion);
                excursions.set(index, excursionToUpdate);
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Excursion> getExcursionByName(String excursionName) {
        return this.excursions
                .stream() //converts the list into a stream
                .filter(excursion -> excursion.getName().equals(excursionName))
                .findFirst();
    }

}
