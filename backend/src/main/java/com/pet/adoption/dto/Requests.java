package com.pet.adoption.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requests {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "请输入用户名")
        public String username;
        @NotBlank(message = "请输入密码")
        public String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "请输入用户名")
        @Size(min = 3, max = 30, message = "用户名长度为3-30位")
        public String username;
        @NotBlank(message = "请输入密码")
        @Size(min = 6, max = 30, message = "密码至少6位")
        public String password;
        public String nickname;
        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
        public String phone;
        @Email(message = "邮箱格式不正确")
        public String email;
    }

    @Data
    public static class LoginResponse {
        public String token;
        public Object user;

        public LoginResponse(String token, Object user) {
            this.token = token;
            this.user = user;
        }
    }

    @Data
    public static class UserProfileRequest {
        public String nickname;
        @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
        public String phone;
        @Email(message = "邮箱格式不正确")
        public String email;
        public String avatar;
    }

    @Data
    public static class UserAdminRequest {
        public String nickname;
        public String phone;
        public String email;
        public String role;
        public String avatar;
        public Boolean disabled;
    }

    @Data
    public static class PetRequest {
        @NotBlank(message = "请输入宠物类型")
        public String type;
        @NotBlank(message = "请输入宠物名字")
        public String name;
        public String brief, description, habit, health, gender, ageUnit, bodySize, mainImage, requirements, status;
        public Double height, weight;
        public Integer age;
        public Boolean sterilized;
        public List<String> detailImages = new ArrayList<>();
    }

    @Data
    public static class AdoptionRequest {
        @NotNull(message = "请选择宠物")
        public Long petId;
        @NotBlank(message = "请输入申请人姓名")
        public String applicantName;
        @NotBlank(message = "请输入联系电话")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        public String phone;
        public String housing, experience;
        @NotBlank(message = "请填写申请理由")
        public String reason;
        @AssertTrue(message = "请先勾选领养承诺")
        public Boolean agreement;
    }

    @Data
    public static class ReviewRequest {
        @NotBlank(message = "审核状态不能为空")
        public String status;
        public String reviewComment;
    }

    @Data
    public static class IdsRequest {
        public List<Long> ids;
    }

    @Data
    public static class BatchReviewRequest {
        public List<Long> ids;
        public String status;
        public String reviewComment;
    }

    @Data
    public static class TypeCount {
        public String type;
        public Long count;

        public TypeCount() {}
        public TypeCount(String type, Long count) {
            this.type = type;
            this.count = count;
        }
    }

    @Data
    public static class TimelineItem {
        public String date;
        public Long applied;
        public Long approved;
        public Long rejected;

        public TimelineItem() {}
        public TimelineItem(String date, Long applied, Long approved, Long rejected) {
            this.date = date;
            this.applied = applied;
            this.approved = approved;
            this.rejected = rejected;
        }
    }

    @Data
    public static class StatisticsResponse {
        public List<TypeCount> typeStats;
        public Map<String, Long> statusStats;
        public Long total, pending, approved, rejected, canceled;
        public Double approvalRate;
        public List<TimelineItem> timeline;
    }
}