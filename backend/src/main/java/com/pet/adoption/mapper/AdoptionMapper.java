package com.pet.adoption.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.adoption.entity.Adoption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface AdoptionMapper extends BaseMapper<Adoption> {

    @Select("""
            <script>
            SELECT * FROM adoptions
            <where>
                <if test="userId != null">AND user_id = #{userId}</if>
                <if test="keyword != null and keyword != ''">
                    AND (pet_name LIKE CONCAT('%', #{keyword}, '%')
                         OR applicant_name LIKE CONCAT('%', #{keyword}, '%')
                         OR phone LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="type != null and type != ''">AND pet_type = #{type}</if>
                <if test="status != null and status != ''">AND status = #{status}</if>
            </where>
            ORDER BY apply_time DESC
            </script>
            """)
    IPage<Adoption> search(Page<Adoption> page,
                           @Param("userId") Long userId,
                           @Param("keyword") String keyword,
                           @Param("type") String type,
                           @Param("status") String status);

    @Select("""
        <script>
        SELECT pet_type AS k, COUNT(*) AS v FROM adoptions
        <where>
            <if test="scope != null and scope == 'approved'">status = 'approved'</if>
        </where>
        GROUP BY pet_type
        </script>
        """)
    List<Map<String, Object>> countGroupByType(@Param("scope") String scope);

    @Select("""
        <script>
        SELECT status AS k, COUNT(*) AS v FROM adoptions
        <where>
            <if test="scope != null and scope == 'approved'">status = 'approved'</if>
        </where>
        GROUP BY status
        </script>
        """)
    List<Map<String, Object>> countGroupByStatus(@Param("scope") String scope);

    /**
     * 按天统计申请量
     * 返回 Map: { d: 'yyyy-MM-dd', c: count }
     */
    @Select("""
            SELECT DATE_FORMAT(apply_time, '%Y-%m-%d') AS d, COUNT(*) AS c
            FROM adoptions
            WHERE apply_time >= #{start} AND apply_time < #{end}
            GROUP BY d ORDER BY d
            """)
    List<java.util.Map<String, Object>> countAppliedByDay(@Param("start") LocalDateTime start,
                                                          @Param("end") LocalDateTime end);

    /**
     * 按天统计审核结果
     * 返回 Map: { d: 'yyyy-MM-dd', s: status, c: count }
     */
    @Select("""
            SELECT DATE_FORMAT(review_time, '%Y-%m-%d') AS d, status AS s, COUNT(*) AS c
            FROM adoptions
            WHERE review_time >= #{start} AND review_time < #{end}
              AND status IN ('approved','rejected')
            GROUP BY d, s ORDER BY d
            """)
    List<java.util.Map<String, Object>> countReviewedByDay(@Param("start") LocalDateTime start,
                                                            @Param("end") LocalDateTime end);
}