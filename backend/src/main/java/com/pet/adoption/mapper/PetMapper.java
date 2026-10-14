package com.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.adoption.entity.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {

    @Select("""
            <script>
            SELECT * FROM pets
            <where>
                <if test="type != null and type != ''">AND type = #{type}</if>
                <if test="status != null and status != ''">AND status = #{status}</if>
                <if test="keyword != null and keyword != ''">
                    AND (name LIKE CONCAT('%', #{keyword}, '%')
                         OR brief LIKE CONCAT('%', #{keyword}, '%')
                         OR description LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="gender != null and gender != ''">AND gender = #{gender}</if>
                <if test="bodySize != null and bodySize != ''">AND body_size = #{bodySize}</if>
                <if test="sterilized != null">AND sterilized = #{sterilized}</if>
            </where>
            <choose>
                <when test="sort == 'hot'">
                    ORDER BY application_count DESC, view_count DESC
                </when>
                <otherwise>
                    ORDER BY create_time DESC
                </otherwise>
            </choose>
            </script>
            """)
    IPage<Pet> search(Page<Pet> page,
                      @Param("type") String type,
                      @Param("status") String status,
                      @Param("keyword") String keyword,
                      @Param("gender") String gender,
                      @Param("bodySize") String bodySize,
                      @Param("sterilized") Boolean sterilized,
                      @Param("sort") String sort);

    @Select("SELECT DISTINCT type FROM pets WHERE type IS NOT NULL AND type <> '' ORDER BY type")
    List<String> findDistinctTypes();

    @Select("SELECT * FROM pets WHERE status = #{status} ORDER BY view_count DESC, application_count DESC, create_time DESC LIMIT #{limit}")
    List<Pet> findHot(@Param("status") String status, @Param("limit") int limit);

    @Update("UPDATE pets SET view_count = view_count + 1 WHERE id = #{id}")
    int incViewCount(@Param("id") Long id);

    @Select("SELECT view_count FROM pets WHERE id = #{id}")
    Integer selectViewCount(@Param("id") Long id);

    @Update("UPDATE pets SET application_count = application_count + 1 WHERE id = #{id}")
    int incApplicationCount(@Param("id") Long id);
}