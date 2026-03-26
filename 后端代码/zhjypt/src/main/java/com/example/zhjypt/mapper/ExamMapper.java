package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 考试表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    /**
     * 根据课程ID获取考试列表
     */
    List<Exam> selectExamsByCourseId(@Param("courseId") Integer courseId);

    /**
     * 获取学生可参加的考试列表（已发布且在有效期内）
     */
    List<Exam> selectAvailableExams(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);
}