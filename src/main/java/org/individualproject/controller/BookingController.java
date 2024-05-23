package org.individualproject.controller;


import jakarta.validation.Valid;
import org.individualproject.business.BookingService;
import org.individualproject.business.UserService;
import org.individualproject.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private BookingService bookingService;
    private UserService userService;
    public BookingController(BookingService bService, UserService uService){
        this.bookingService = bService;
        this.userService = uService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable(value = "id") final Long id)
    {
        final Optional<Booking> bookingOptional = bookingService.getBooking(id);
        return bookingOptional.map(booking -> ResponseEntity.ok().body(booking))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping()
    public ResponseEntity<List<Booking>> getBookings()
    {
        List<Booking> bookings = bookingService.getBookings();
        return ResponseEntity.ok().body(bookings);
    }

    @PostMapping()
    public ResponseEntity<Booking> createBooking(@RequestBody @Valid CreateBookingRequest request) {
        Booking response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteBooking(@PathVariable(value = "id") final Long id)
    {
        if (bookingService.deleteBooking(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBooking(@PathVariable(value = "id") final long id, @RequestBody @Valid UpdateBookingRequest request){

        request.setId(id);
        bookingService.updateBooking(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable(value = "userId") final Long userId)
    {
        Optional<User> userOptional = userService.getUser(userId);
        if(userOptional == null){
            return  ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        List<Booking> bookings = bookingService.getBookingsByUser(user);
        return ResponseEntity.ok().body(bookings);
    }

}
