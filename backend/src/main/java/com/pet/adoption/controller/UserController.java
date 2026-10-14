package com.pet.adoption.controller;

import com.pet.adoption.dto.ApiResponse;
import com.pet.adoption.dto.Requests.UserProfileRequest;
import com.pet.adoption.entity.User;
import com.pet.adoption.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/info")
    public ApiResponse<User> info() {
        return ApiResponse.ok(service.currentUser());
    }

    @PutMapping("/info")
    public ApiResponse<User> update(@Valid @RequestBody UserProfileRequest r) {
        return ApiResponse.ok(service.updateProfile(r));
    }
}