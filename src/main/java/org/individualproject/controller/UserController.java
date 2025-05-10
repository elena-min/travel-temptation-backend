package org.individualproject.controller;


import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.individualproject.business.BookingService;
import org.individualproject.business.ReviewService;
import org.individualproject.business.UserService;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    private ReviewService reviewService;
    private BookingService bookingService;

    public UserController(UserService uService, ReviewService rService, BookingService bService){
        this.userService = uService;
        this.reviewService = rService;
        this.bookingService = bService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id")@NotNull final Long id)
    {
        try {
            final Optional<User> userOptional = userService.getUser(id);
            return userOptional.map(user -> ResponseEntity.ok().body(user))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable(value = "username")@NotNull final String username)
    {
        try {
            final Optional<User> userOptional = userService.getUserByUsername(username);
            return userOptional.map(user -> ResponseEntity.ok().body(user))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping()
    public ResponseEntity<List<User>> getUsers()
    {
        List<User> users = userService.getUsers();
        return ResponseEntity.ok().body(users);
    }

    @PostMapping()
    public ResponseEntity<User> createUser(@RequestBody @Valid CreateUserRequest request) {
        User response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"USER", "TRAVELAGENCY"})
    public ResponseEntity<Long> deleteUser(@PathVariable(value = "id")@NotNull final Long id)
    {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @RolesAllowed({"USER", "TRAVELAGENCY"})
    public ResponseEntity<Void> updateUser(@PathVariable(value = "id")@NotNull final long id, @RequestBody @Valid UpdateUserRequest request){

        request.setId(id);
        userService.updateUser(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/reviews")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable(value = "userId")@NotNull final Long userId)
    {
        Optional<User> userOptional = userService.getUser(userId);
        if (userOptional.isEmpty()) {
            return  ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        List<Review> reviews = reviewService.getReviewsByUser(user);
        return ResponseEntity.ok().body(reviews);
    }

    @GetMapping("/{userId}/bookings")
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

    @GetMapping("/{userId}/past-bookings")
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


    @GetMapping("/{userId}/future-bookings")
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
}
