package org.individualproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.individualproject.business.LoginService;
import org.individualproject.business.RegisterService;
import org.individualproject.domain.LoginRegisterResponse;
import org.individualproject.domain.LoginRequest;
import org.individualproject.domain.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;
    @PostMapping("/user")
    public ResponseEntity<LoginRegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        LoginRegisterResponse registerResponse = registerService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }

    @PostMapping("/traveling-agency")
    public ResponseEntity<LoginRegisterResponse> registerTravelingAgency(@RequestBody @Valid RegisterRequest registerRequest) {
        LoginRegisterResponse registerResponse = registerService.registerTravelingAgency(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerResponse);
    }
}
