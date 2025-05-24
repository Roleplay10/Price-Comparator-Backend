package com.roleplay10.price_comparator.controller;

import com.roleplay10.price_comparator.dto.request.auth.LoginRequest;
import com.roleplay10.price_comparator.dto.request.auth.RegisterRequest;
import com.roleplay10.price_comparator.dto.response.auth.JwtResponse;
import com.roleplay10.price_comparator.service.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        return userService.register(req);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        JwtResponse jwt = userService.login(req);
        return !jwt.getToken().isEmpty() ? ResponseEntity.ok(jwt) : ResponseEntity.badRequest().build();
    }
}
