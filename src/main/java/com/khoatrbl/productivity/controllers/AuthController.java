package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.LogInRequest;
import com.khoatrbl.productivity.domains.dtos.LogInResponse;
import com.khoatrbl.productivity.domains.dtos.RegisterRequest;
import com.khoatrbl.productivity.domains.dtos.RegisterResponse;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @Value("${jwt.expiration}")
    private Duration tokenExp;

    @PostMapping(path = "/login")
    public ResponseEntity<LogInResponse> login(@Valid @RequestBody LogInRequest logInRequest) {
        String token = authenticationService.login(logInRequest.getEmail(), logInRequest.getPassword());


        LogInResponse res = LogInResponse.builder()
                .token(token)
                .expiresIn(tokenExp.toSeconds())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Users newUser = authenticationService.registerUser(registerRequest);

        RegisterResponse res = RegisterResponse.builder()
                .email(newUser.getEmail())
                .displayName(newUser.getDisplayName())
                .timezone(newUser.getTimezone())
                .build();

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
