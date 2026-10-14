package com.pet.adoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("adoptions")
public class Adoption {
    @TableId(type = IdType.AUTO)
    public Long id;

    @TableField("user_id")
    public Long userId;

    @TableField("pet_id")
    public Long petId;

    @TableField("pet_name")
    public String petName;

    @TableField("pet_type")
    public String petType;

    @TableField("pet_image")
    public String petImage;

    @TableField("applicant_name")
    public String applicantName;

    @TableField("phone")
    public String phone;

    @TableField("housing")
    public String housing;

    @TableField("experience")
    public String experience;

    @TableField("reason")
    public String reason;

    @TableField("status")
    public String status;

    @TableField(value = "apply_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime applyTime;

    @TableField("review_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime reviewTime;

    @TableField("review_comment")
    public String reviewComment;
}