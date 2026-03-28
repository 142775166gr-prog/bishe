package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.StudentAnswer;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生答题记录表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-09
 */
public interface StudentAnswerService extends IService<StudentAnswer> {

    /**
     * 保存学生答案
     */
    boolean saveStudentAnswer(Integer recordId, Integer questionId, String studentAnswer, Integer questionScore);

    /**
     * 批量保存学生答案
     */
    boolean batchSaveStudentAnswers(Integer recordId, String answersJson);

    /**
     * 获取学生答案（包含题目信息）
     */
    List<Map<String, Object>> getStudentAnswersWithQuestions(Integer recordId);

    /**
     * 批改主观题
     */
    boolean gradeSubjectiveAnswers(Integer recordId, String gradingData);

    /**
     * 交卷时根据题库中的题目与 {@link com.example.zhjypt.pojo.StudentAnswer} 重新计算客观题得分并汇总；
     * 主观题得分清空待教师批改。不可信任前端传入的总分。
     *
     * @param recordId 考试记录 ID
     * @return 是否处理成功
     */
    boolean recalculateScoresFromServer(Integer recordId);
}