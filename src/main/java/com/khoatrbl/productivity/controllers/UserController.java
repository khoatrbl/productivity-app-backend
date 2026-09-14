package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.*;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.mappers.LevelMapper;
import com.khoatrbl.productivity.utilities.SecurityUtils;
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

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Users user = userService.getUserById(userId);

        ProfileDto profileDto = ProfileDto.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .timezone(user.getTimezone())
                .currentLevel(LevelMapper.toDto(user.getCurrentLevel()))
                .currentExp(user.getCurrentExp())
                .coins(user.getCoins())
                .build();

        return new ResponseEntity<>(profileDto, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UpdateProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        Users user = userService.updateUserProfile(userId, updateProfileRequest);

        UpdateProfileResponse res = UpdateProfileResponse.builder()
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .timezone(user.getTimezone())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);
        userService.updateUserPassword(userId, updatePasswordRequest);

        return new ResponseEntity<>(new UpdatePasswordResponse(), HttpStatus.OK);
    }

    @PatchMapping(path = "/level")
    public ResponseEntity<UpdateLevelResponse> updateUserLevel(
            @Valid @RequestBody UpdateLevelRequest updateLevelRequest,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);

        Users user = userService.updateUserLevel(userId, updateLevelRequest);

        UpdateLevelResponse res = UpdateLevelResponse.builder()
                .currentLevel(LevelMapper.toDto(user.getCurrentLevel()))
                .currentExp(user.getCurrentExp())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping(path = "/coins")
    public ResponseEntity<UpdateCoinsResponse> updateUserCoins(
            @Valid @RequestBody UpdateCoinsRequest updateCoinsRequest,
            Authentication authentication) {

        UUID userId = SecurityUtils.getCurrentUserId(authentication);

        Users user = userService.updateUserCoins(userId, updateCoinsRequest);

        UpdateCoinsResponse res = UpdateCoinsResponse.builder()
                .coins(user.getCoins())
                .build();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }



}
