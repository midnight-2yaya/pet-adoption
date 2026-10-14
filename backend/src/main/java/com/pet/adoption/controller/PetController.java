package com.pet.adoption.controller;

import com.pet.adoption.dto.ApiResponse;
import com.pet.adoption.dto.PageResult;
import com.pet.adoption.entity.Pet;
import com.pet.adoption.service.PetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {
    final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<PageResult<Pet>> list(@RequestParam(required = false) String type,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false) String gender,
                                             @RequestParam(required = false) String bodySize,
                                             @RequestParam(required = false) Boolean sterilized,
                                             @RequestParam(defaultValue = "available") String status,
                                             @RequestParam(defaultValue = "latest") String sort,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(required = false) Integer size,
                                             @RequestParam(required = false) Integer pageSize) {
        int s = pageSize != null ? pageSize : (size != null ? size : 8);
        return ApiResponse.ok(service.search(type, keyword, gender, bodySize, sterilized, status, sort, page, s));
    }

    @GetMapping("/{id}")
    public ApiResponse<Pet> detail(@PathVariable Long id) {
        return ApiResponse.ok(service.detail(id, true));
    }

    @GetMapping("/types")
    public ApiResponse<List<String>> types() {
        return ApiResponse.ok(service.types());
    }

    @GetMapping("/hot")
    public ApiResponse<List<Pet>> hot(@RequestParam(defaultValue = "6") int size) {
        return ApiResponse.ok(service.hotPets(size));
    }
}