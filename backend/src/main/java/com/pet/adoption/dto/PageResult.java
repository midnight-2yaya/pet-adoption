package com.pet.adoption.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    public List<T> content;
    public long totalElements;
    public long page;
    public long size;
    public long pages;

    public static <T> PageResult<T> of(IPage<T> p) {
        PageResult<T> r = new PageResult<>();
        r.content = p.getRecords();
        r.totalElements = p.getTotal();
        r.page = p.getCurrent();
        r.size = p.getSize();
        r.pages = p.getPages();
        return r;
    }
}