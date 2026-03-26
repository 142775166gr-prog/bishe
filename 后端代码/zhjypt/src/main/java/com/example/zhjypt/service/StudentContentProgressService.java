package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.StudentContentProgress;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生内容学习进度表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
public interface StudentContentProgressService extends IService<StudentContentProgress> {

    /**
     * 更新学习进度
     */
    boolean updateProgress(Integer studentId, Integer courseId, Integer chapterId, 
                          Integer contentId, Integer watchProgress);

    /**
     * 标记内容为已完成
     */
    boolean markCompleted(Integer studentId, Integer courseId, Integer chapterId, Integer contentId);

    /**
     * 计算课程总进度
     */
    BigDecimal calculateCourseProgress(Integer studentId, Integer courseId);

    /**
     * 获取课程进度统计
     */
    Map<String, Object> getCourseProgressStats(Integer studentId, Integer courseId);

    /**
     * 获取学生在课程中的所有学习记录
     */
    List<StudentContentProgress> getStudentCourseProgress(Integer studentId, Integer courseId);

    /**
     * 检查内容是否已完成
     */
    boolean isContentCompleted(Integer studentId, Integer contentId);
}