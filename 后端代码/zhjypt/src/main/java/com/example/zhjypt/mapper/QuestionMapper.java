package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 题目表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 根据课程ID获取题目列表
     */
    List<Question> selectQuestionsByCourseId(@Param("courseId") Integer courseId);

    /**
     * 根据考试ID获取题目列表（包含选项）
     */
    List<Question> selectQuestionsByExamId(@Param("examId") Integer examId);

    /**
     * 获取题目详情（包含选项）
     */
    Question selectQuestionWithOptions(@Param("questionId") Integer questionId);
}