package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.ProfileDto;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.security.CustomUserDetails;
import com.khoatrbl.productivity.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ProfileDto> getMyProfile(Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        UUID userId = currentUser.getUserId();

        Users user = userService.getUserById(userId);

        ProfileDto profileDto = ProfileDto.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .timezone(user.getTimezone())
                .build();

        return new ResponseEntity<>(profileDto, HttpStatus.OK);
    }
}
