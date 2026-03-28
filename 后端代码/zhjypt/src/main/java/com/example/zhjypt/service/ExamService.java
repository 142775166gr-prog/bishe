package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.Exam;
import com.example.zhjypt.pojo.StudentExamRecord;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 考试表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
public interface ExamService extends IService<Exam> {

    /**
     * 根据课程ID获取考试列表
     */
    List<Exam> getExamsByCourseId(Integer courseId);

    /**
     * 创建考试
     */
    boolean createExam(Exam exam);

    /**
     * 更新考试
     */
    boolean updateExam(Exam exam);

    /**
     * 删除考试
     */
    boolean deleteExam(Integer examId);

    /**
     * 发布考试
     */
    boolean publishExam(Integer examId);

    /**
     * 取消发布考试
     */
    boolean unpublishExam(Integer examId);

    /**
     * 获取学生可参加的考试列表
     */
    List<Exam> getAvailableExams(Integer courseId, Integer studentId);

    /**
     * 获取学生某个考试的所有记录
     */
    List<StudentExamRecord> getStudentExamRecords(Integer studentId, Integer examId);

    /**
     * 获取学生最新的考试记录
     */
    StudentExamRecord getLatestExamRecord(Integer studentId, Integer examId);

    /**
     * 获取学生考试次数
     */
    Integer getExamAttemptCount(Integer studentId, Integer examId);

    /**
     * 检查学生是否已提交过该考试（不包括进行中的记录）
     */
    boolean hasSubmittedExam(Integer studentId, Integer examId);

    /**
     * 创建考试记录（学生每次开始考试生成一条）。当 {@code exam.attempt_limit} 非空且大于 0 时，
     * 若当前学生该考试已有记录数已达上限，则抛出 {@link IllegalStateException}；{@code null} 或 0 表示不限次数（由实体约定）。
     */
    StudentExamRecord createExamRecord(Integer studentId, Integer examId, Integer totalScore);

    /**
     * 提交考试记录。服务端根据 {@code student_answer} 与题库 {@code question.correct_answer} 重算分数；
     * 参数 {@code studentScore} 仅为兼容旧前端，将被忽略。
     */
    boolean submitExamRecord(Integer recordId, Integer timeUsed, Double studentScore);

    /**
     * 获取需要批改的考试记录
     */
    List<Map<String, Object>> getRecordsForGrading(Integer courseId);

    /**
     * 批改考试记录
     */
    boolean gradeExamRecord(Integer recordId, Double subjectiveScore, String feedback);

    /**
     * 获取教师待批改数量
     */
    Integer getPendingGradingCount(Integer teacherId);

    /**
     * 获取学生待完成考试数量
     */
    Integer getPendingExamCount(Integer studentId);
}
