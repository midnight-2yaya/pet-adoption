package com.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.adoption.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("""
            <script>
            SELECT * FROM users
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (username LIKE CONCAT('%', #{keyword}, '%')
                         OR nickname LIKE CONCAT('%', #{keyword}, '%')
                         OR phone LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="role != null and role != ''">
                    AND role = #{role}
                </if>
                <if test="disabled != null">
                    AND disabled = #{disabled}
                </if>
            </where>
            ORDER BY id DESC
            </script>
            """)
    IPage<User> search(Page<User> page,
                       @Param("keyword") String keyword,
                       @Param("role") String role,
                       @Param("disabled") Boolean disabled);
}