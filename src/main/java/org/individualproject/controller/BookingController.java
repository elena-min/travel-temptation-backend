package org.individualproject.controller;


import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.individualproject.business.BookingService;
import org.individualproject.business.ExcursionService;
import org.individualproject.business.UserService;
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
@RequestMapping("/bookings")
public class BookingController {

    private BookingService bookingService;
    private UserService userService;
    private ExcursionService excursionService;
    public BookingController(BookingService bService, UserService uService, ExcursionService eService){
        this.bookingService = bService;
        this.userService = uService;
        this.excursionService = eService;
    }

    @GetMapping("/{id}")
    @RolesAllowed({"USER", "TRAVELAGENCY"})
    public ResponseEntity<Booking> getBooking(@PathVariable(value = "id") final Long id)
    {
        final Optional<Booking> bookingOptional = bookingService.getBooking(id);
        return bookingOptional.map(booking -> ResponseEntity.ok().body(booking))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping()
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<List<Booking>> getBookings()
    {
        List<Booking> bookings = bookingService.getBookings();
        return ResponseEntity.ok().body(bookings);
    }

    @PostMapping()
    @RolesAllowed({"USER"})
    public ResponseEntity<Booking> createBooking(@RequestBody @Valid CreateBookingRequest request) {
        Booking response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"USER", "TRAVELAGENCY"})
    public ResponseEntity<Long> deleteBooking(@PathVariable(value = "id") final Long id)
    {
        if (bookingService.deleteBooking(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @RolesAllowed({"USER", "TRAVELAGENCY"})
    public ResponseEntity<Void> updateBooking(@PathVariable(value = "id") final long id, @RequestBody @Valid UpdateBookingRequest request){

        request.setId(id);
        bookingService.updateBooking(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @RolesAllowed({"USER"})
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable(value = "userId") final Long userId)
    {
        Optional<User> userOptional = userService.getUser(userId);
        if(userOptional.isEmpty()){
            return  ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        List<Booking> bookings = bookingService.getBookingsByUser(user);
        return ResponseEntity.ok().body(bookings);
    }

    @GetMapping("/past/{userId}")
    @RolesAllowed({"USER"})
    public ResponseEntity<List<Booking>> getPastBookingsByUser(@PathVariable(value = "userId") final Long userId)
    {
        Optional<User> userOptional = userService.getUser(userId);
        if(userOptional.isEmpty()){
            return  ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        List<Booking> bookings = bookingService.getPastBookingsByUser(user);
        return ResponseEntity.ok().body(bookings);
    }


    @GetMapping("/future/{userId}")
    @RolesAllowed({"USER"})
    public ResponseEntity<List<Booking>> getFutureBookingsByUser(@PathVariable(value = "userId") final Long userId)
    {
        Optional<User> userOptional = userService.getUser(userId);
        if(userOptional.isEmpty()){
            return  ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        List<Booking> bookings = bookingService.getFutureBookingsByUser(user);
        return ResponseEntity.ok().body(bookings);
    }

    @GetMapping("/excursion/{excursionId}")
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

    @GetMapping("/total-sales-last-quarter")
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<Double> getTotalSalesLastQuarter(@RequestParam(value = "startDate") LocalDateTime startDate,
                                                            @RequestParam(value = "endDate") LocalDateTime endDate,
                                                            @RequestParam(value = "status") BookingStatus status)
    {
        Double totalSales = bookingService.getTotalSalesInLastQuarter(startDate, endDate, status);
        return ResponseEntity.ok().body(totalSales);
    }

    @GetMapping("/total-sales-last-quarter/{excursionId}")
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<Double> getTotalSalesLastQuarterPerExcursion(
                                                              @PathVariable Long excursionId,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                              @RequestParam(value = "status") BookingStatus status){
        Double totalSales = bookingService.getTotalSalesInLastQuarterForExcursion(excursionId, startDate, endDate, status);
        return ResponseEntity.ok().body(totalSales);
    }

    @GetMapping("/weekly-statistics/{excursionId}")
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<List<WeeklyStatisticsDTO>> getWeeklyStatistics(@PathVariable Long excursionId, @RequestParam(value = "status") BookingStatus status){
        List<WeeklyStatisticsDTO> weeklyStatistics = bookingService.getWeeklyStatistics(excursionId, status);
        return ResponseEntity.ok().body(weeklyStatistics);
    }

    @GetMapping("/booking-statistics/{excursionId}")
    @RolesAllowed({"TRAVELAGENCY"})
    public ResponseEntity<List<BookingDataDTO>> getBookingDataByDateRangePerExcursion(@PathVariable Long excursionId,
                                                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate){
        List<BookingDataDTO> bookingDataDTOS = bookingService.getBookingDataByDateRangePerExcursion(excursionId, startDate, endDate);
        return ResponseEntity.ok().body(bookingDataDTOS);
    }

}
