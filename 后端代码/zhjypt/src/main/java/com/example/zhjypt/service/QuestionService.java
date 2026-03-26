package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.Question;

import java.util.List;

/**
 * <p>
 * 题目表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
public interface QuestionService extends IService<Question> {

    /**
     * 根据课程ID获取题目列表
     */
    List<Question> getQuestionsByCourseId(Integer courseId);

    /**
     * 创建题目
     */
    boolean createQuestion(Question question);

    /**
     * 创建题目（包含选项）
     */
    boolean createQuestionWithOptions(Question question, String optionsJson);

    /**
     * 更新题目
     */
    boolean updateQuestion(Question question);

    /**
     * 更新题目（包含选项）
     */
    boolean updateQuestionWithOptions(Question question, String optionsJson);

    /**
     * 删除题目
     */
    boolean deleteQuestion(Integer questionId);

    /**
     * 根据考试ID获取题目列表（包含选项）
     */
    List<Question> getQuestionsByExamId(Integer examId);

    /**
     * 获取题目详情（包含选项）
     */
    Question getQuestionWithOptions(Integer questionId);
}