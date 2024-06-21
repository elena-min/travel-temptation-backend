package org.individualproject.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotNull;
import org.individualproject.business.ReviewService;
import org.individualproject.business.UserService;
import org.individualproject.business.exception.NotFoundException;
import org.individualproject.business.exception.UnauthorizedDataAccessException;
import org.individualproject.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private ReviewService reviewService;
    private UserService userService;
    public ReviewController(ReviewService rService, UserService uService){
        this.reviewService = rService;
        this.userService = uService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(@PathVariable(value = "id")@NotNull final Long id)
    {
        final Optional<Review> reviewOptional = reviewService.getReview(id);
        return reviewOptional.map(review -> ResponseEntity.ok().body(review))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping()
    public ResponseEntity<List<Review>> geReviews()
    {
        List<Review> reviews = reviewService.getReviews();
        return ResponseEntity.ok().body(reviews);
    }

    @PostMapping()
    @RolesAllowed({"USER"})
    public ResponseEntity<Review> createReview(@RequestBody @Valid CreateReviewRequest request) {
        Review response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Long> deleteReview(@PathVariable(value = "id")@NotNull final Long id)
    {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedDataAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable(value = "userId")@NotNull final Long userId)
//    {
//        Optional<User> userOptional = userService.getUser(userId);
//        if (userOptional.isEmpty()) {
//            return  ResponseEntity.notFound().build();
//        }
//        User user = userOptional.get();
//        List<Review> reviews = reviewService.getReviewsByUser(user);
//        return ResponseEntity.ok().body(reviews);
//    }

    @GetMapping("/travel-agency/{travelAgencyId}")
    public ResponseEntity<List<Review>> getReviewsByTravelAgency(@PathVariable(value = "travelAgencyId")@NotNull final Long travelAgencyId)
    {
        Optional<User> userOptional = userService.getUser(travelAgencyId);
        if (!userOptional.isPresent()) {
            return  ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        List<Review> reviews = reviewService.getReviewsByTravelAgency(user);
        return ResponseEntity.ok().body(reviews);
    }
}
