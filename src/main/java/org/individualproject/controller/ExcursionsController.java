package org.individualproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.individualproject.business.ExcursionService;
import org.individualproject.domain.CreateExcursionRequest;
import org.individualproject.domain.Excursion;
import org.individualproject.domain.UpdateExcursionRequest;
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

    public ExcursionsController(ExcursionService exService){
        this.excursionService = exService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Excursion> getExcursion(@PathVariable(value = "id") final Long id)
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

    //@RolesAllowed({"TRAVELINGAGENCY", "ADMIN"})
    @PostMapping()
    public ResponseEntity<Excursion> createExcursion(@RequestBody @Valid CreateExcursionRequest request) {
        Excursion response = excursionService.createExcursion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RolesAllowed({"TRAVELINGAGENCY", "ADMIN"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteExcursion(@PathVariable(value = "id") final Long id)
    {
        if (excursionService.deleteExcursion(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @RolesAllowed({"TRAVELINGAGENCY", "ADMIN"})
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateExcursion(@PathVariable(value = "id") final long id, @RequestBody @Valid UpdateExcursionRequest request){

        request.setId(id);
        excursionService.updateExcursion(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Excursion> getExcursionByName(@PathVariable(value = "name") final String name)
    {
        final Optional<Excursion> excursionOptional = excursionService.getExcursionByName(name);
        return excursionOptional.map(excursion -> ResponseEntity.ok().body(excursion))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

//    @GetMapping("/search")
//    public ResponseEntity<List<Excursion>> searchExcursionByName(final String name){
//        List<Excursion> excursions = excursionService.findExcursionsByName(name);
//        return ResponseEntity.ok().body(excursions);
//    }

    @GetMapping("/search")
    public ResponseEntity<List<Excursion>> searchExcursionByNameAndPrice(@RequestParam(value= "name", required = false) String name, @RequestParam(value= "priceRange", required = false) String priceRange ){
        List<Excursion> excursions;
        if(name != null && priceRange != null){
            excursions = excursionService.searchExcursionsByNameAndPriceRange(name, priceRange);
        }
        else if(name != null){
            excursions = excursionService.findExcursionsByName(name);

        }
        else if(priceRange != null){
            excursions = excursionService.findExcursionsByPriceRange(priceRange);
        }
        else{
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(excursions);
    }
}
