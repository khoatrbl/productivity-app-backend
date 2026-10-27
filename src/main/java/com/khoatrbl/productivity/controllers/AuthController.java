package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.components.RefreshTokenCookieFactory;
import com.khoatrbl.productivity.domains.dtos.AuthResponse;
import com.khoatrbl.productivity.domains.dtos.LogInRequest;
import com.khoatrbl.productivity.domains.dtos.LogInResult;
import com.khoatrbl.productivity.domains.dtos.RegisterRequest;
import com.khoatrbl.productivity.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenCookieFactory refreshTokenCookieFactory;

    @Value("${jwt.expiration}")
    private Duration tokenExp;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LogInRequest logInRequest) {
        LogInResult loginResult = authenticationService.login(logInRequest.getEmail(), logInRequest.getPassword());

        ResponseCookie refreshCookie =  refreshTokenCookieFactory.create(loginResult.getRefreshToken());

        AuthResponse res = AuthResponse.builder()
                .token(loginResult.getAccessToken())
                .expiresIn(tokenExp.toSeconds())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(res);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LogInResult registerResult = authenticationService.registerUser(registerRequest);

        ResponseCookie responseCookie = refreshTokenCookieFactory.create(registerResult.getRefreshToken());

        AuthResponse res = AuthResponse.builder()
                .token(registerResult.getAccessToken())
                .expiresIn(tokenExp.toSeconds())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(res);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = RefreshTokenCookieFactory.COOKIE_NAME, required = false) String refreshToken) {

        String token = authenticationService.refresh(refreshToken);

        AuthResponse res = AuthResponse.builder()
                .token(token)
                .expiresIn(tokenExp.toSeconds())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieFactory.create(refreshToken).toString())
                .body(res);
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = RefreshTokenCookieFactory.COOKIE_NAME, required = false) String refreshToken
    ) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authenticationService.revokeRefreshToken(refreshToken);
        }

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieFactory.clear().toString())
                .build();
    }

    @GetMapping(path = "/validate")
    public ResponseEntity<Void> validateToken() {
        // If execution reaches here at all,
        // the JWT is already successfully validated by Spring Security.

        return ResponseEntity.ok().build();
    }
}
