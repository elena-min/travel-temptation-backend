package org.individualproject.controller;

import jakarta.validation.Valid;

import org.individualproject.business.ReviewService;
import org.individualproject.business.UserService;
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
    public ResponseEntity<Review> getReview(@PathVariable(value = "id") final Long id)
    {
        final Optional<Review> reviewOptional = reviewService.getReview(id);
        return reviewOptional.map(review -> ResponseEntity.ok().body(review))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping()
    public ResponseEntity<List<Review>> getBookings()
    {
        List<Review> reviews = reviewService.getReviews();
        return ResponseEntity.ok().body(reviews);
    }

    @PostMapping()
    public ResponseEntity<Review> createReview(@RequestBody @Valid CreateReviewRequest request) {
        Review response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteReview(@PathVariable(value = "id") final Long id)
    {
        if (reviewService.deleteReview(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable(value = "userId") final Long userId)
    {
        Optional<User> userOptional = userService.getUser(userId);
        if(userOptional == null){
            return  ResponseEntity.notFound().build();
        }
        User user = userOptional.get();
        List<Review> reviews = reviewService.getReviewsByUser(user);
        return ResponseEntity.ok().body(reviews);
    }
}
