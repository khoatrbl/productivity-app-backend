package com.khoatrbl.productivity.utilities;

import com.khoatrbl.productivity.security.CustomUserDetails;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UUID getCurrentUserId(Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        return currentUser.getUserId();
    }
}
