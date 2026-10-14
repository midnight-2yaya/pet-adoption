package com.pet.adoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    public Long id;

    @TableField("username")
    public String username;

    @JsonIgnore
    @TableField("password")
    public String password;

    @TableField("nickname")
    public String nickname;

    @TableField("phone")
    public String phone;

    @TableField("email")
    public String email;

    @TableField("role")
    public String role;

    @TableField("avatar")
    public String avatar;

    @TableField("disabled")
    public Boolean disabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    public LocalDateTime createTime;
}