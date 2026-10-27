package com.khoatrbl.productivity.components;

import com.khoatrbl.productivity.domains.entities.RefreshToken;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshTokenCookieFactory {
    public static final String COOKIE_NAME = "capydo_refresh";
    public static final String COOKIE_PATH = "/api/v1/auth";

    public ResponseCookie create(String rawToken) {
        return ResponseCookie.from(COOKIE_NAME, rawToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("LAX")
                .path(COOKIE_PATH)
                .maxAge(RefreshToken.SLIDING_TTL)
                .build();
    }

    public ResponseCookie clear() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("LAX")
                .path(COOKIE_PATH)
                .maxAge(Duration.ZERO)
                .build();
    }
}
