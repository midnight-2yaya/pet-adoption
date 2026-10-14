package com.pet.adoption.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("pets")
public class Pet {
    @TableId(type = IdType.AUTO)
    public Long id;

    @TableField("type")
    public String type;

    @TableField("name")
    public String name;

    @TableField("brief")
    public String brief;

    @TableField("description")
    public String description;

    @TableField("habit")
    public String habit;

    @TableField("health")
    public String health;

    @TableField("height")
    public Double height;

    @TableField("weight")
    public Double weight;

    @TableField("gender")
    public String gender;

    @TableField("age")
    public Integer age;

    @TableField("age_unit")
    public String ageUnit;

    @TableField("body_size")
    public String bodySize;

    @TableField("sterilized")
    public Boolean sterilized;

    @TableField("main_image")
    public String mainImage;

    /**
     * detail_images 在数据库里是 JSON 字符串
     * 通过 TypeHandler 自动转 List<String>
     */
    @TableField(value = "detail_images", typeHandler = com.pet.adoption.config.StringListTypeHandler.class)
    public List<String> detailImages = new ArrayList<>();

    @TableField("requirements")
    public String requirements;

    @TableField("status")
    public String status;

    @TableField("view_count")
    public Integer viewCount;

    @TableField("application_count")
    public Integer applicationCount;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime createTime;
}