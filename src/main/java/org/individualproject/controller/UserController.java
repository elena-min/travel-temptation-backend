package org.individualproject.controller;


import jakarta.validation.Valid;
import org.individualproject.business.UserService;
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

    public UserController(UserService uService){
        this.userService = uService;
    }

    @GetMapping("/{id}")
    public User getBooking(@PathVariable(value = "id") final Long id)
    {
        final Optional<User> userOptional = userService.getUser(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }
    @GetMapping()
    public List<User> getBookings()
    {
        return userService.getUsers();
    }

    @PostMapping()
    public ResponseEntity<User> createBooking(@RequestBody @Valid CreateUserRequest request) {
        User response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteBooking(@PathVariable(value = "id") final Long id)
    {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBooking(@PathVariable(value = "id") final long id, @RequestBody @Valid UpdateUserRequest request){

        request.setId(id);
        userService.updateUser(request);
        return ResponseEntity.noContent().build();
    }
}
