package com.roleplay10.price_comparator.service.auth;

import com.roleplay10.price_comparator.dto.request.auth.LoginRequest;
import com.roleplay10.price_comparator.dto.request.auth.RegisterRequest;
import com.roleplay10.price_comparator.dto.response.auth.JwtResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
}

