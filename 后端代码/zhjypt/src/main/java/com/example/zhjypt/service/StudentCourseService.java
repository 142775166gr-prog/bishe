package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.StudentCourse;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 学生选课表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
public interface StudentCourseService extends IService<StudentCourse> {

    /**
     * 学生选课
     */
    boolean enrollCourse(Integer studentId, Integer courseId);

    /**
     * 检查是否已选课
     */
    boolean checkEnrolled(Integer studentId, Integer courseId);

    /**
     * 获取学生的课程列表
     */
    List<StudentCourse> getStudentCourses(Integer studentId);

    /**
     * 退课
     */
    boolean withdrawCourse(Integer studentId, Integer courseId);

    /**
     * 更新学习进度
     */
    boolean updateProgress(Integer studentId, Integer courseId, BigDecimal progress);

    /**
     * 获取课程的选课学生列表（包含学习情况）
     */
    List getCourseStudents(Integer courseId);

    /**
     * 获取学生的章节学习详情
     */
    List getStudentChapterProgress(Integer studentId, Integer courseId);
}
