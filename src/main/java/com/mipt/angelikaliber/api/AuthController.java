package com.mipt.angelikaliber.api;

import com.mipt.angelikaliber.dto.gateway.LoginRequest;
import com.mipt.angelikaliber.dto.gateway.LoginResponse;
import com.mipt.angelikaliber.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final long expirationMs;

    public AuthController(UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils,
                          @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.expirationMs = expirationMs;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException ex) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }
        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String token = jwtUtils.generateToken(user.getUsername(), authorities);
        return ResponseEntity.ok(new LoginResponse(token, expirationMs));
    }
}
