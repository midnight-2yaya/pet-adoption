package com.pet.adoption.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.adoption.dto.PageResult;
import com.pet.adoption.dto.Requests.*;
import com.pet.adoption.entity.*;
import com.pet.adoption.exception.BusinessException;
import com.pet.adoption.mapper.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AdoptionService {
    final AdoptionMapper adoptionMapper;
    final PetMapper petMapper;
    final UserService userService;
    final RedisTemplate<String, Object> redis;

    public AdoptionService(AdoptionMapper adoptionMapper, PetMapper petMapper,
                           UserService userService, RedisTemplate<String, Object> redis) {
        this.adoptionMapper = adoptionMapper;
        this.petMapper = petMapper;
        this.userService = userService;
        this.redis = redis;
    }

    @Transactional
    public Adoption apply(AdoptionRequest r) {
        User u = userService.currentUserRaw();
        Pet p = petMapper.selectById(r.petId);
        if (p == null) throw new BusinessException("宠物不存在");
        if (!"available".equals(p.status)) throw new BusinessException("这只小可爱暂时不能申请");
        Long cnt = adoptionMapper.selectCount(new LambdaQueryWrapper<Adoption>()
                .eq(Adoption::getUserId, u.id)
                .eq(Adoption::getPetId, p.id)
                .in(Adoption::getStatus, "pending", "approved"));
        if (cnt != null && cnt > 0) throw new BusinessException("你已经申请过这只宠物了");

        Adoption a = new Adoption();
        a.userId = u.id;
        a.petId = p.id;
        a.petName = p.name;
        a.petType = p.type;
        a.petImage = p.mainImage;
        a.applicantName = r.applicantName;
        a.phone = r.phone;
        a.housing = r.housing;
        a.experience = r.experience;
        a.reason = r.reason;
        a.status = "pending";
        a.applyTime = LocalDateTime.now();
        adoptionMapper.insert(a);

        petMapper.incApplicationCount(p.id);
        return a;
    }

    public PageResult<Adoption> my(String keyword, String type, String status, int page, int size) {
        User u = userService.currentUserRaw();
        Page<Adoption> p = new Page<>(Math.max(page, 1), size);
        IPage<Adoption> result = adoptionMapper.search(p, u.id,
                StringUtils.hasText(keyword) ? keyword.trim() : null,
                StringUtils.hasText(type) ? type : null,
                StringUtils.hasText(status) ? status : null);
        return PageResult.of(result);
    }

    @Transactional
    public void cancel(Long id) {
        User u = userService.currentUserRaw();
        Adoption a = adoptionMapper.selectById(id);
        if (a == null) throw new BusinessException("申请不存在");
        if (!Objects.equals(u.id, a.userId)) throw new BusinessException(403, "不能取消别人的申请");
        if (!"pending".equals(a.status)) throw new BusinessException("只有待审核申请可以取消");
        a.status = "canceled";
        a.reviewTime = LocalDateTime.now();
        a.reviewComment = "用户主动取消申请";
        adoptionMapper.updateById(a);
        clearStatCache();
    }

    public PageResult<Adoption> adminSearch(String keyword, String type, String status, int page, int size) {
        Page<Adoption> p = new Page<>(Math.max(page, 1), size);
        IPage<Adoption> result = adoptionMapper.search(p, null,
                StringUtils.hasText(keyword) ? keyword.trim() : null,
                StringUtils.hasText(type) ? type : null,
                StringUtils.hasText(status) ? status : null);
        return PageResult.of(result);
    }

    @Transactional
    public Adoption review(Long id, ReviewRequest r) {
        Adoption a = adoptionMapper.selectById(id);
        if (a == null) throw new BusinessException("申请不存在");
        if (!"pending".equals(a.status)) throw new BusinessException("只有待审核申请可以审核");
        if ("approved".equals(r.status)) approve(a, r.reviewComment);
        else if ("rejected".equals(r.status))
            reject(a, StringUtils.hasText(r.reviewComment) ? r.reviewComment : "暂不符合领养条件");
        else throw new BusinessException("审核状态只能是 approved 或 rejected");
        clearStatCache();
        return a;
    }

    void approve(Adoption a, String comment) {
        Pet p = petMapper.selectById(a.petId);
        if (p == null) throw new BusinessException("宠物不存在");
        if ("adopted".equals(p.status)) throw new BusinessException("这只宠物已经被领养");

        Long approvedCnt = adoptionMapper.selectCount(new LambdaQueryWrapper<Adoption>()
                .eq(Adoption::getPetId, p.id)
                .eq(Adoption::getStatus, "approved"));
        if (approvedCnt != null && approvedCnt > 0)
            throw new BusinessException("这只宠物已经被领养，不能重复通过");

        a.status = "approved";
        a.reviewTime = LocalDateTime.now();
        a.reviewComment = StringUtils.hasText(comment) ? comment : "审核通过，请保持电话畅通";
        adoptionMapper.updateById(a);

        p.status = "adopted";
        petMapper.updateById(p);

        for (int i = 1; i <= 20; i++) {
            redis.delete("pet:hot:" + i);
        }

        // 同宠物其他 pending 申请自动拒绝
        List<Adoption> others = adoptionMapper.selectList(new LambdaQueryWrapper<Adoption>()
                .eq(Adoption::getPetId, p.id)
                .eq(Adoption::getStatus, "pending"));
        for (Adoption o : others) {
            if (!Objects.equals(o.id, a.id)) {
                o.status = "rejected";
                o.reviewTime = LocalDateTime.now();
                o.reviewComment = "宠物已被其他用户领养";
                adoptionMapper.updateById(o);
            }
        }
    }

    void reject(Adoption a, String c) {
        a.status = "rejected";
        a.reviewTime = LocalDateTime.now();
        a.reviewComment = c;
        adoptionMapper.updateById(a);
    }

    public Map<String, Object> batchReview(List<Long> ids, String status, String comment) {
        int success = 0;
        List<String> failed = new ArrayList<>();
        if (ids != null) {
            for (Long id : ids) {
                try {
                    ReviewRequest r = new ReviewRequest();
                    r.status = status;
                    r.reviewComment = comment;
                    review(id, r);
                    success++;
                } catch (Exception e) {
                    failed.add("ID " + id + "：" + e.getMessage());
                }
            }
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("success", success);
        m.put("failed", failed);
        clearStatCache();
        return m;
    }

    public StatisticsResponse statistics(String scope) {
        String key = "adoption:stat:" + scope;
        Object cached = redis.opsForValue().get(key);
        if (cached instanceof StatisticsResponse) {
            return (StatisticsResponse) cached;
        }

        String filterScope = "approved".equals(scope) ? "approved" : null;

        StatisticsResponse res = new StatisticsResponse();

        // 类型统计
        Map<String, Long> byType = new LinkedHashMap<>();
        for (Map<String, Object> row : adoptionMapper.countGroupByType(filterScope)) {
            byType.put(String.valueOf(row.get("k")), ((Number) row.get("v")).longValue());
        }
        for (String t : petMapper.findDistinctTypes()) byType.putIfAbsent(t, 0L);
        res.typeStats = byType.entrySet().stream()
                .map(e -> new TypeCount(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // 状态统计
        Map<String, Long> byStatus = new LinkedHashMap<>();
        long total = 0;
        for (Map<String, Object> row : adoptionMapper.countGroupByStatus(filterScope)) {
            long c = ((Number) row.get("v")).longValue();
            byStatus.put(String.valueOf(row.get("k")), c);
            total += c;
        }
        res.statusStats = byStatus;
        res.total = total;
        res.pending = byStatus.getOrDefault("pending", 0L);
        res.approved = byStatus.getOrDefault("approved", 0L);
        res.rejected = byStatus.getOrDefault("rejected", 0L);
        res.canceled = byStatus.getOrDefault("canceled", 0L);
        res.approvalRate = total == 0 ? 0 : Math.round(res.approved * 10000.0 / total) / 100.0;

        // 时间轴
        res.timeline = buildTimeline(14);

        redis.opsForValue().set(key, res, 60, TimeUnit.SECONDS);
        return res;
    }

    private List<TimelineItem> buildTimeline(int days) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1L);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Map<String, Long> appliedMap = new HashMap<>();
        for (Map<String, Object> row : adoptionMapper.countAppliedByDay(start, end)) {
            appliedMap.put(String.valueOf(row.get("d")), ((Number) row.get("c")).longValue());
        }

        Map<String, Map<String, Long>> reviewedMap = new HashMap<>();
        for (Map<String, Object> row : adoptionMapper.countReviewedByDay(start, end)) {
            String d = String.valueOf(row.get("d"));
            String s = String.valueOf(row.get("s"));
            long c = ((Number) row.get("c")).longValue();
            reviewedMap.computeIfAbsent(d, k -> new HashMap<>()).put(s, c);
        }

        List<TimelineItem> list = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < days; i++) {
            String d = startDate.plusDays(i).format(fmt);
            long applied = appliedMap.getOrDefault(d, 0L);
            Map<String, Long> rv = reviewedMap.getOrDefault(d, Collections.emptyMap());
            list.add(new TimelineItem(d,
                    applied,
                    rv.getOrDefault("approved", 0L),
                    rv.getOrDefault("rejected", 0L)));
        }
        return list;
    }

    private void clearStatCache() {
        redis.delete("adoption:stat:all");
        redis.delete("adoption:stat:approved");
    }
}