package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.StudentContentProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * <p>
 * 学生内容学习进度表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Mapper
public interface StudentContentProgressMapper extends BaseMapper<StudentContentProgress> {

    /**
     * 计算学生课程的总进度（基于权重）
     */
    @Select("SELECT CASE " +
            "WHEN SUM(cc.progress_weight) > 0 THEN " +
            "    ROUND(COALESCE(SUM(CASE WHEN scp.is_completed = 1 THEN cc.progress_weight ELSE 0 END), 0) * 100.0 / SUM(cc.progress_weight), 2) " +
            "ELSE 0 " +
            "END " +
            "FROM chapter_content cc " +
            "INNER JOIN course_chapter ch ON cc.chapter_id = ch.chapter_id " +
            "LEFT JOIN student_content_progress scp ON cc.content_id = scp.content_id AND scp.student_id = #{studentId} " +
            "WHERE ch.course_id = #{courseId} AND cc.content_status = 1 AND cc.del = 0 AND ch.del = 0")
    BigDecimal calculateCourseProgress(@Param("studentId") Integer studentId, 
                                      @Param("courseId") Integer courseId);

    /**
     * 获取课程进度统计信息
     */
    @Select("SELECT " +
            "SUM(cc.progress_weight) as total_weight, " +
            "COALESCE(SUM(CASE WHEN scp.is_completed = 1 THEN cc.progress_weight ELSE 0 END), 0) as completed_weight, " +
            "COUNT(cc.content_id) as total_contents, " +
            "COALESCE(SUM(CASE WHEN scp.is_completed = 1 THEN 1 ELSE 0 END), 0) as completed_contents " +
            "FROM chapter_content cc " +
            "INNER JOIN course_chapter ch ON cc.chapter_id = ch.chapter_id " +
            "LEFT JOIN student_content_progress scp ON cc.content_id = scp.content_id AND scp.student_id = #{studentId} " +
            "WHERE ch.course_id = #{courseId} AND cc.content_status = 1 AND cc.del = 0 AND ch.del = 0")
    java.util.Map<String, Object> getCourseProgressStats(@Param("studentId") Integer studentId, 
                                                         @Param("courseId") Integer courseId);
}