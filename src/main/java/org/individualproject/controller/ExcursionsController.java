package org.individualproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.individualproject.business.ExcursionService;
import org.individualproject.business.UserService;
import org.individualproject.domain.CreateExcursionRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateExcursionRequest;
import org.individualproject.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.lang.Long;

@RestController
@RequestMapping("/excursions")
public class ExcursionsController {

    private ExcursionService excursionService;
    private UserService userService;

    public ExcursionsController(ExcursionService exService, UserService uService){
        this.excursionService = exService;
        this.userService = uService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Excursion> getExcursion(@PathVariable(value = "id")@NotNull final Long id)
    {
        final Optional<Excursion> excursionOptional = excursionService.getExcursion(id);
        return excursionOptional.map(excursion -> ResponseEntity.ok().body(excursion))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping()
    public ResponseEntity<List<Excursion>> getExcursions()
    {
        List<Excursion> excursions = excursionService.getExcursions();
        return ResponseEntity.ok().body(excursions);
    }

    @PostMapping()
    @RolesAllowed({"TRAVELAGENCY", "ADMIN"})
    public ResponseEntity<Excursion> createExcursion(@RequestBody @Valid CreateExcursionRequest request) {
        Excursion response = excursionService.createExcursion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"TRAVELAGENCY", "ADMIN"})
    public ResponseEntity<Long> deleteExcursion(@PathVariable(value = "id")@NotNull final Long id)
    {
        if (excursionService.deleteExcursion(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @RolesAllowed({"TRAVELAGENCY", "ADMIN"})
    public ResponseEntity<Void> updateExcursion(@PathVariable(value = "id")@NotNull final long id, @RequestBody @Valid UpdateExcursionRequest request){

        request.setId(id);
        excursionService.updateExcursion(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Excursion> getExcursionByName(@PathVariable(value = "name")@NotNull final String name)
    {
        final Optional<Excursion> excursionOptional = excursionService.getExcursionByName(name);
        return excursionOptional.map(excursion -> ResponseEntity.ok().body(excursion))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }


    @GetMapping("/travelAgency/{travelAgencyID}")
    public ResponseEntity<List<Excursion>> getExcursionsByTravelAgency(@PathVariable(value = "travelAgencyID")@NotNull Long travelAgency)
    {
        Optional<User> userOptional = userService.getUser(travelAgency);
        if(userOptional == null){
            return  ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        List<Excursion> excursions = excursionService.getExcursionsByTravelAgency(user);
        return ResponseEntity.ok().body(excursions);
    }

    @GetMapping("/searchName")
    public ResponseEntity<List<Excursion>> searchExcursionsByNameAndTravelAgency(@RequestParam(value = "searchTerm", required = false) String searchTerm) {
        List<Excursion> excursions;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            excursions = excursionService.searchExcursionsByNameAndTravelAgency(searchTerm);
        } else {
            excursions = excursionService.getExcursions();
        }
         return ResponseEntity.ok().body(excursions);
    }

    @GetMapping("/searchNameAndPrice")
    public ResponseEntity<List<Excursion>> searchExcursions(
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "minPrice", required = false) String minPrice,
            @RequestParam(value = "maxPrice", required = false) String maxPrice) {
        Double minPriceValue = 0.0;
        Double maxPriceValue = Double.MAX_VALUE;


            if (minPrice != null) {
                minPriceValue = Double.parseDouble(minPrice);
            }
            if (maxPrice != null && !maxPrice.equals("Infinity")) {
                maxPriceValue = Double.parseDouble(maxPrice);
            }

        List<Excursion> excursions = excursionService.searchExcursions(
                searchTerm, minPriceValue, maxPriceValue);

        return ResponseEntity.ok().body(excursions);
    }
}
