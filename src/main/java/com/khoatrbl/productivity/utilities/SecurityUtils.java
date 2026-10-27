package com.khoatrbl.productivity.utilities;

import com.khoatrbl.productivity.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("Invalid authentication.");
        }

        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        return currentUser.getUserId();
    }
}
