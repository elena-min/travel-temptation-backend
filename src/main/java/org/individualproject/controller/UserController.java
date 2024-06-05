package org.individualproject.controller;


import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<User> getUser(@PathVariable(value = "id")@NotNull final Long id)
    {
        final Optional<User> userOptional = userService.getUser(id);
        return userOptional.map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable(value = "username")@NotNull final String username)
    {
        final User user = userService.getUserByUsername(username);
        return ResponseEntity.ok().body(user);
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
    @RolesAllowed({"USER"})
    public ResponseEntity<Long> deleteUser(@PathVariable(value = "id")@NotNull final Long id)
    {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @RolesAllowed({"USER"})
    public ResponseEntity<Void> updateUser(@PathVariable(value = "id")@NotNull final long id, @RequestBody @Valid UpdateUserRequest request){

        request.setId(id);
        userService.updateUser(request);
        return ResponseEntity.noContent().build();
    }
}
