package com.pet.adoption.controller;

import com.pet.adoption.dto.ApiResponse;
import com.pet.adoption.dto.Requests.*;
import com.pet.adoption.entity.User;
import com.pet.adoption.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest r) {
        return ApiResponse.ok(service.register(r));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest r) {
        return ApiResponse.ok(service.login(r));
    }
}