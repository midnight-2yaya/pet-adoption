package com.pet.adoption.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.adoption.dto.PageResult;
import com.pet.adoption.dto.Requests.PetRequest;
import com.pet.adoption.entity.Pet;
import com.pet.adoption.exception.BusinessException;
import com.pet.adoption.mapper.PetMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PetService {
    final PetMapper petMapper;
    final RedisTemplate<String, Object> redis;

    public PetService(PetMapper petMapper, RedisTemplate<String, Object> redis) {
        this.petMapper = petMapper;
        this.redis = redis;
    }

    public PageResult<Pet> search(String type, String keyword, String gender, String bodySize,
                                  Boolean sterilized, String status, String sort, int page, int size) {
        Page<Pet> p = new Page<>(Math.max(page, 1), size);
        IPage<Pet> result = petMapper.search(p,
                StringUtils.hasText(type) ? type : null,
                StringUtils.hasText(status) ? status : null,
                StringUtils.hasText(keyword) ? keyword.trim() : null,
                StringUtils.hasText(gender) ? normalizeGender(gender) : null,
                StringUtils.hasText(bodySize) ? bodySize : null,
                sterilized,
                StringUtils.hasText(sort) ? sort : "latest");
        return PageResult.of(result);
    }

    public Pet detail(Long id, boolean inc) {
        Pet p = petMapper.selectById(id);
        if (p == null) throw new BusinessException("宠物不存在");
        if (inc) {
            petMapper.incViewCount(id);
            p.viewCount = n(p.viewCount) + 1;
        }
        return p;
    }

    public Pet create(PetRequest r) {
        Pet p = new Pet();
        copy(r, p);
        petMapper.insert(p);
        clearHotCache();
        return p;
    }

    public Pet update(Long id, PetRequest r) {
        Pet p = petMapper.selectById(id);
        if (p == null) throw new BusinessException("宠物不存在");
        copy(r, p);
        petMapper.updateById(p);
        clearHotCache();
        return p;
    }

    public void delete(Long id) {
        petMapper.deleteById(id);
        clearHotCache();
    }

    public void batchOffline(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        List<Pet> pets = petMapper.selectBatchIds(ids);
        for (Pet p : pets) {
            if (!"adopted".equals(p.status)) {
                p.status = "offline";
                petMapper.updateById(p);
            }
        }
        clearHotCache();
    }

    public List<String> types() {
        return petMapper.findDistinctTypes();
    }

    @SuppressWarnings("unchecked")
    public List<Pet> hotPets(int size) {
        int s = Math.max(1, size);
        String key = "pet:hot:" + s;
        Object cached = redis.opsForValue().get(key);
        if (cached instanceof List) {
            return (List<Pet>) cached;
        }
        List<Pet> list = petMapper.findHot("available", s);
        long ttl = 300 + new Random().nextInt(60);
        redis.opsForValue().set(key, list, ttl, TimeUnit.SECONDS);
        return list;
    }

    private void clearHotCache() {
        // 热门 key 数量有限，直接删固定 key，避免 KEYS 阻塞
        for (int i = 1; i <= 20; i++) {
            redis.delete("pet:hot:" + i);
        }
    }

    void copy(PetRequest r, Pet p) {
        p.type = r.type;
        p.name = r.name;
        p.brief = r.brief;
        p.description = r.description;
        p.habit = r.habit;
        p.health = r.health;
        p.height = r.height;
        p.weight = r.weight;
        p.gender = normalizeGender(r.gender);
        p.age = r.age;
        p.ageUnit = StringUtils.hasText(r.ageUnit) ? r.ageUnit : "岁";
        p.bodySize = r.bodySize;
        p.sterilized = r.sterilized;
        p.mainImage = r.mainImage;
        p.detailImages = r.detailImages == null ? new ArrayList<>() : r.detailImages;
        p.requirements = r.requirements;
        p.status = StringUtils.hasText(r.status) ? r.status : "available";
    }

    static String normalizeGender(String g) {
        if ("男".equals(g) || "male".equalsIgnoreCase(String.valueOf(g))) return "公";
        if ("女".equals(g) || "female".equalsIgnoreCase(String.valueOf(g))) return "母";
        return g;
    }

    static int n(Integer i) {
        return i == null ? 0 : i;
    }
}