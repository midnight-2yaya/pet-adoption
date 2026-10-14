package com.pet.adoption.controller;

import com.pet.adoption.dto.ApiResponse;
import com.pet.adoption.dto.PageResult;
import com.pet.adoption.dto.Requests.*;
import com.pet.adoption.entity.Adoption;
import com.pet.adoption.service.AdoptionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {
    final AdoptionService service;

    public AdoptionController(AdoptionService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<Adoption> apply(@Valid @RequestBody AdoptionRequest r) {
        return ApiResponse.ok(service.apply(r));
    }

    @GetMapping("/my")
    public ApiResponse<PageResult<Adoption>> my(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String type,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(required = false) Integer size,
                                                @RequestParam(required = false) Integer pageSize) {
        int s = pageSize != null ? pageSize : (size != null ? size : 8);
        return ApiResponse.ok(service.my(keyword, type, status, page, s));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ApiResponse.ok();
    }

    @GetMapping("/statistics")
    public ApiResponse<StatisticsResponse> statistics(@RequestParam(defaultValue = "all") String scope) {
        return ApiResponse.ok(service.statistics(scope));
    }

    @GetMapping("/summary")
    public ApiResponse<StatisticsResponse> summary() {
        return ApiResponse.ok(service.statistics("all"));
    }
}