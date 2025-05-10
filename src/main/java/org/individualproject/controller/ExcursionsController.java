package org.individualproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.individualproject.business.BookingService;
import org.individualproject.business.ExcursionService;
import org.individualproject.business.UserService;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.domain.*;
import org.individualproject.domain.enums.BookingStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/excursions")
public class ExcursionsController {

    private ExcursionService excursionService;
    private UserService userService;
    private BookingService bookingService;

    public ExcursionsController(ExcursionService exService, UserService uService, BookingService bService){
        this.excursionService = exService;
        this.userService = uService;
        this.bookingService = bService;
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

        try {
            request.setId(id);
            boolean updated = excursionService.updateExcursion(request);
            if (updated) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch (UnauthorizedDataAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Excursion> getExcursionByName(@PathVariable(value = "name")@NotNull final String name)
    {
        final Optional<Excursion> excursionOptional = excursionService.getExcursionByName(name);
        return excursionOptional.map(excursion -> ResponseEntity.ok().body(excursion))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }


    @GetMapping("/travel-agency/{travelAgencyID}")
    public ResponseEntity<List<Excursion>> getExcursionsByTravelAgency(@PathVariable(value = "travelAgencyID")@NotNull Long travelAgency)
    {
        Optional<User> userOptional = userService.getUser(travelAgency);
        if (!userOptional.isPresent()) {
            throw new NotFoundException("Travel agency not found with ID: " + travelAgency);
        }
        User user = userOptional.get();
        List<Excursion> excursions = excursionService.getExcursionsByTravelAgency(user);
        return ResponseEntity.ok().body(excursions);
    }

    @GetMapping("/{excursionId}/bookings")
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<List<Booking>> getBookingsByExcursion(@PathVariable(value = "excursionId") final Long excursionId)
    {
        Optional<Excursion> excursionOptional = excursionService.getExcursion(excursionId);
        if(excursionOptional.isEmpty()){
            return  ResponseEntity.notFound().build();
        }
        Excursion excursion = excursionOptional.get();
        List<Booking> bookings = bookingService.getBookingsByExcursion(excursion);
        return ResponseEntity.ok().body(bookings);
    }

    @GetMapping("/{excursionId}/weekly-statistics")
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<List<WeeklyStatisticsDTO>> getWeeklyStatistics(@PathVariable Long excursionId, @RequestParam(value = "status") BookingStatus status){
        List<WeeklyStatisticsDTO> weeklyStatistics = bookingService.getWeeklyStatistics(excursionId, status);
        return ResponseEntity.ok().body(weeklyStatistics);
    }

    @GetMapping("/{excursionId}/booking-statistics")
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<List<BookingDataDTO>> getBookingDataByDateRangePerExcursion(@PathVariable Long excursionId,
                                                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate){
        List<BookingDataDTO> bookingDataDTOS = bookingService.getBookingDataByDateRangePerExcursion(excursionId, startDate, endDate);
        return ResponseEntity.ok().body(bookingDataDTOS);
    }

    @GetMapping("/{excursionId}/total-sales-last-quarter")
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<Double> getTotalSalesLastQuarterPerExcursion(
            @PathVariable Long excursionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(value = "status") BookingStatus status){
        Double totalSales = bookingService.getTotalSalesInLastQuarterForExcursion(excursionId, startDate, endDate, status);
        return ResponseEntity.ok().body(totalSales);
    }


    @GetMapping("/search-name")
    public ResponseEntity<List<Excursion>> searchExcursionsByNameAndTravelAgency(@RequestParam(value = "searchTerm", required = false) String searchTerm) {
        List<Excursion> excursions;
        if (searchTerm != null && !searchTerm.isEmpty()) {
            excursions = excursionService.searchExcursionsByNameAndTravelAgency(searchTerm);
        } else {
            excursions = excursionService.getExcursions();
        }
         return ResponseEntity.ok().body(excursions);
    }

    @GetMapping("/search-name-and-price")
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
