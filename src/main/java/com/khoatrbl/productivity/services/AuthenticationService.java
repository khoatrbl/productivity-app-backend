package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.RegisterRequest;
import com.khoatrbl.productivity.domains.entities.Users;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    String login(String email, String password);

    String generateJwtToken(UserDetails userDetails);

    UserDetails validateToken(String token);

    Users registerUser(RegisterRequest registerRequest);
}
