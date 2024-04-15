package org.individualproject.controller;


import jakarta.validation.Valid;
import org.individualproject.business.BookingService;
import org.individualproject.business.ExcursionService;
import org.individualproject.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

//    private BookingService bookingService;
//
//    public BookingController(BookingService bService){
//        this.bookingService = bService;
//    }
//
//    @GetMapping("/{id}")
//    public Booking getBooking(@PathVariable(value = "id") final Long id)
//    {
//        final Optional<Booking> bookingOptional = bookingService.getBooking(id);
//        if (bookingOptional.isPresent()) {
//            return bookingOptional.get();
//        }
//        return null;
//    }
//    @GetMapping()
//    public List<Booking> getBookings()
//    {
//        return bookingService.getBookings();
//    }
//
//    @PostMapping()
//    public ResponseEntity<Booking> createBooking(@RequestBody @Valid CreateBookingRequest request) {
//        Booking response = bookingService.createBooking(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Long> deleteBooking(@PathVariable(value = "id") final Long id)
//    {
//        if (bookingService.deleteBooking(id)) {
//            return ResponseEntity.ok().build();
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Void> updateBooking(@PathVariable(value = "id") final long id, @RequestBody @Valid UpdateBookingRequest request){
//
//        request.setId(id);
//        bookingService.updateBooking(request);
//        return ResponseEntity.noContent().build();
//    }

}
