package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.ProfileDto;
import com.khoatrbl.productivity.domains.dtos.UpdateProfileRequest;
import com.khoatrbl.productivity.domains.dtos.UpdateProfileResponse;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.security.CustomUserDetails;
import com.khoatrbl.productivity.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ProfileDto> getMyProfile(Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);
        Users user = userService.getUserById(userId);

        ProfileDto profileDto = ProfileDto.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .timezone(user.getTimezone())
                .build();

        return new ResponseEntity<>(profileDto, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);
        Users user = userService.updateUserProfile(userId, updateProfileRequest);

        UpdateProfileResponse res = UpdateProfileResponse.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .timezone(user.getTimezone())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private UUID getCurrentUserId(Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        return currentUser.getUserId();
    }
}
