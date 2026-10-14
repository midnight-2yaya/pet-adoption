package com.pet.adoption.controller;

import com.pet.adoption.dto.ApiResponse;
import com.pet.adoption.dto.PageResult;
import com.pet.adoption.dto.Requests.*;
import com.pet.adoption.entity.*;
import com.pet.adoption.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    final PetService petService;
    final AdoptionService adoptionService;
    final UserService userService;

    public AdminController(PetService p, AdoptionService a, UserService u) {
        this.petService = p;
        this.adoptionService = a;
        this.userService = u;
    }

    @GetMapping("/pets")
    public ApiResponse<PageResult<Pet>> pets(@RequestParam(required = false) String type,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String gender,
                                             @RequestParam(required = false) String bodySize,
                                             @RequestParam(required = false) Boolean sterilized,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "latest") String sort,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(required = false) Integer size,
                                             @RequestParam(required = false) Integer pageSize) {
        int s = pageSize != null ? pageSize : (size != null ? size : 10);
        return ApiResponse.ok(petService.search(type, keyword, gender, bodySize, sterilized, status, sort, page, s));
    }

    @PostMapping("/pets")
    public ApiResponse<Pet> createPet(@Valid @RequestBody PetRequest r) {
        return ApiResponse.ok(petService.create(r));
    }

    @PutMapping("/pets/{id}")
    public ApiResponse<Pet> updatePet(@PathVariable Long id, @Valid @RequestBody PetRequest r) {
        return ApiResponse.ok(petService.update(id, r));
    }

    @DeleteMapping("/pets/{id}")
    public ApiResponse<Void> deletePet(@PathVariable Long id) {
        petService.delete(id);
        return ApiResponse.ok();
    }

    @PostMapping("/pets/batch-offline")
    public ApiResponse<Void> batchOffline(@RequestBody IdsRequest r) {
        petService.batchOffline(r.ids);
        return ApiResponse.ok();
    }

    @GetMapping("/adoptions")
    public ApiResponse<PageResult<Adoption>> adoptions(@RequestParam(required = false) String keyword,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(required = false) Integer size,
                                                       @RequestParam(required = false) Integer pageSize) {
        int s = pageSize != null ? pageSize : (size != null ? size : 10);
        return ApiResponse.ok(adoptionService.adminSearch(keyword, type, status, page, s));
    }

    @PutMapping("/adoptions/{id}/status")
    public ApiResponse<Adoption> review(@PathVariable Long id, @Valid @RequestBody ReviewRequest r) {
        return ApiResponse.ok(adoptionService.review(id, r));
    }

    @PostMapping("/adoptions/batch-review")
    public ApiResponse<Map<String, Object>> batchReview(@RequestBody BatchReviewRequest r) {
        return ApiResponse.ok(adoptionService.batchReview(r.ids, r.status, r.reviewComment));
    }

    @GetMapping("/users")
    public ApiResponse<PageResult<User>> users(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) String role,
                                               @RequestParam(required = false) Boolean disabled,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(required = false) Integer size,
                                               @RequestParam(required = false) Integer pageSize) {
        int s = pageSize != null ? pageSize : (size != null ? size : 10);
        return ApiResponse.ok(userService.adminSearch(keyword, role, disabled, page, s));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody UserAdminRequest r) {
        return ApiResponse.ok(userService.adminUpdate(id, r));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.ok();
    }

    @PostMapping("/users/batch-disable")
    public ApiResponse<Void> batchDisable(@RequestBody IdsRequest r) {
        userService.batchDisable(r.ids);
        return ApiResponse.ok();
    }
}