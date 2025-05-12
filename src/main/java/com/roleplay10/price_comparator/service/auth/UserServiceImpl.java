package com.roleplay10.price_comparator.service.auth;

import com.roleplay10.price_comparator.domain.entity.AppUser;
import com.roleplay10.price_comparator.dto.request.auth.LoginRequest;
import com.roleplay10.price_comparator.dto.request.auth.RegisterRequest;
import com.roleplay10.price_comparator.dto.response.auth.JwtResponse;
import com.roleplay10.price_comparator.repository.AppUserRepository;
import com.roleplay10.price_comparator.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> register(RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }

        AppUser user = AppUser.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public JwtResponse login(LoginRequest req) {
        AppUser user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return new JwtResponse(token);
    }
}
