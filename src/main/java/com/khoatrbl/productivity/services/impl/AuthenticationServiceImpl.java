package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.CreatePetRequest;
import com.khoatrbl.productivity.domains.dtos.LogInResult;
import com.khoatrbl.productivity.domains.dtos.RegisterRequest;
import com.khoatrbl.productivity.domains.entities.Level;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.EmailAlreadyExistsException;
import com.khoatrbl.productivity.exceptions.InvalidCredentialsException;
import com.khoatrbl.productivity.exceptions.InvalidRefreshTokenException;
import com.khoatrbl.productivity.exceptions.PasswordsNotMatchException;
import com.khoatrbl.productivity.repositories.LevelRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.security.CustomUserDetails;
import com.khoatrbl.productivity.security.CustomUserDetailsService;
import com.khoatrbl.productivity.services.AuthenticationService;
import com.khoatrbl.productivity.services.InventoryItemService;
import com.khoatrbl.productivity.services.PetService;
import com.khoatrbl.productivity.services.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final PetService petService;
    private final InventoryItemService inventoryItemService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration}")
    private Duration JWT_EXP;

    @Override
    public LogInResult login(String email, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            CustomUserDetails authenticatedUserDetails = (CustomUserDetails) auth.getPrincipal();

            String refreshToken = refreshTokenService.createRefreshToken(authenticatedUserDetails.getUser());
            String accessJwtToken = this.generateJwtToken(authenticatedUserDetails);

            return LogInResult.builder()
                    .refreshToken(refreshToken)
                    .accessToken(accessJwtToken)
                    .build();

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

    }

    @Override
    public String refresh(String refreshToken) {
        Users user = refreshTokenService.validateAndExtend(refreshToken);

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        if (!customUserDetails.isEnabled() || !customUserDetails.isAccountNonLocked()) {
            throw new InvalidRefreshTokenException("Account is disabled or locked.");
        }

        return this.generateJwtToken(customUserDetails);
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    @Override
    public String generateJwtToken(CustomUserDetails customUserDetails) {
        Date issueDate = new Date(System.currentTimeMillis());
        Date expDate = new Date(System.currentTimeMillis() + JWT_EXP.toMillis());

        return Jwts.builder()
                .claim("role", customUserDetails.getAuthorities())
                .claim("email", customUserDetails.getUsername())
                .subject(customUserDetails.getUserId().toString())
                .issuedAt(issueDate)
                .expiration(expDate)
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userId = claims.getSubject();

        return customUserDetailsService.loadUserByUserId(userId);
    }

    @Override
    @Transactional
    public LogInResult registerUser(RegisterRequest registerRequest) {
        Users newUser = createNewUser(registerRequest);

        createUserDefaultPet(newUser);
        createUserInventory(newUser);

        CustomUserDetails newUserDetails = new CustomUserDetails(newUser);

        String refreshToken = refreshTokenService.createRefreshToken(newUserDetails.getUser());
        String accessJwtToken = this.generateJwtToken(newUserDetails);

        return LogInResult.builder()
                .refreshToken(refreshToken)
                .accessToken(accessJwtToken)
                .build();
    }

    private Key getSigningKey() {
        byte[] keyBytes = JWT_SECRET.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Users createNewUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already used.");
        }

        String rawPassword = registerRequest.getRawPassword();
        String confirmPassword = registerRequest.getConfirmPassword();

        if (!rawPassword.equals(confirmPassword)) {
            throw new PasswordsNotMatchException("Passwords are not matched.");
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getRawPassword());

        Level startingLevel = levelRepository.findByLevel(1)
                .orElseThrow(
                        () -> new EntityNotFoundException("Level not found for level 1.")
                );

        Users newUser = Users.builder()
                .email(registerRequest.getEmail())
                .displayName(registerRequest.getDisplayName())
                .passwordHash(hashedPassword)
                .role("USER")
                .timezone(registerRequest.getTimezone())
                .currentExp(0)
                .currentLevel(startingLevel)
                .coins(0)
                .build();

        return userRepository.save(newUser);
    }

    private void createUserDefaultPet(Users newUser) {
        String defaultName = "Buddy";
        CreatePetRequest request = CreatePetRequest.builder()
                .name(defaultName)
                .build();

        petService.createPetForUser(newUser.getId(), request);
    }

    private void createUserInventory(Users newUser) {
        inventoryItemService.initializeUserInventory(newUser.getId());
    }
}
