package org.individualproject.controller;

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

//    @GetMapping("/{id}")
//    public Excursion getExcursion(@PathVariable(value = "id") final Integer id)
//    {
//        final Optional<Excursion> excursionOptional = excursionService.getExcursion(id);
//        if (excursionOptional.isPresent()) {
//            return excursionOptional.get();
//        }
//        return null;
//    }
//
//    @GetMapping()
//    public List<Excursion> getExcursions()
//    {
//        return excursionService.getExcursions();
//    }
//
//    @PostMapping()
//        public ResponseEntity<Long> createExcursion(@RequestBody @Valid CreateExcursionRequest request) {
//        Long response = excursionService.createExcursion(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Long> deleteExcursion(@PathVariable(value = "id") final Integer id)
//    {
//        if (excursionService.deleteExcursion(id)) {
//            return ResponseEntity.ok().build();
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Void> updateExcursion(@PathVariable(value = "id") final long id, @RequestBody @Valid UpdateExcursionRequest request){
//
//        request.setId(id);
//        excursionService.updateExcursion(request);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/name/{name}")
//    public Excursion getExcursionByName(@PathVariable(value = "name") final String name)
//    {
//        final Optional<Excursion> excursionOptional = excursionService.getExcursionByName(name);
//        if (excursionOptional.isPresent()) {
//            return excursionOptional.get();
//        }
//        return null;
//    }


}
