package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.RegisterRequest;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.EmailAlreadyExistsException;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.security.CustomUserDetailsService;
import com.khoatrbl.productivity.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration}")
    private Duration JWT_EXP;

    @Override
    public String login(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails authenticatedUserDetails = (UserDetails) auth.getPrincipal();

        return this.generateJwtToken(authenticatedUserDetails);
    }

    @Override
    public String generateJwtToken(UserDetails userDetails) {
        Date issueDate = new Date(System.currentTimeMillis());
        Date expDate = new Date(System.currentTimeMillis() + JWT_EXP.toMillis());

        return Jwts.builder()
                .claim("role", userDetails.getAuthorities())
                .subject(userDetails.getUsername())
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

        String email = claims.getSubject();

        return customUserDetailsService.loadUserByUsername(email);
    }

    @Override
    public Users registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already used.");
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getRawPassword());

        Users newUser = Users.builder()
                .email(registerRequest.getEmail())
                .displayName(registerRequest.getDisplayName())
                .passwordHash(hashedPassword)
                .role("USER")
                .timezone(registerRequest.getTimezone())
                .build();

        return userRepository.save(newUser);
    }

    private Key getSigningKey() {
        byte[] keyBytes = JWT_SECRET.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
