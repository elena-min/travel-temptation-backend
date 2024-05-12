package org.individualproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.individualproject.business.LoginService;
import org.individualproject.domain.LoginRequest;
import org.individualproject.domain.LoginRegisterResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    @PostMapping()
    public ResponseEntity<LoginRegisterResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginRegisterResponse loginResponse = loginService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }
}