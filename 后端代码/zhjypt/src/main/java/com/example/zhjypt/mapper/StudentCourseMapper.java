package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.StudentCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 学生选课表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-27
 */
@Mapper
public interface StudentCourseMapper extends BaseMapper<StudentCourse> {

    /**
     * 获取学生已选课程列表（带课程详细信息）
     */
    @Select("SELECT sc.*, c.course_title, c.course_desc, c.course_cover, " +
            "c.difficulty_level, t.teach_name as teacher_name, cc.category_name " +
            "FROM student_course sc " +
            "LEFT JOIN course c ON sc.course_id = c.course_id " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE sc.student_id = #{studentId} AND sc.status = 1 AND c.del = 0 " +
            "ORDER BY sc.enroll_time DESC")
    List<StudentCourse> getStudentCoursesList(@Param("studentId") Integer studentId);

    /**
     * 检查学生是否已选择某课程
     */
    @Select("SELECT COUNT(*) FROM student_course " +
            "WHERE student_id = #{studentId} AND course_id = #{courseId} AND status = 1")
    int checkStudentCourseExists(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);

    /**
     * 获取学生的课程列表（包含课程详细信息）
     */
    @Select("SELECT sc.*, c.course_title, c.course_desc, c.course_cover, " +
            "c.difficulty_level, t.teach_name as teacher_name, cc.category_name " +
            "FROM student_course sc " +
            "LEFT JOIN course c ON sc.course_id = c.course_id " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE sc.student_id = #{studentId} AND sc.status = 1 AND c.del = 0 " +
            "ORDER BY sc.enroll_time DESC")
    List<StudentCourse> getStudentCoursesWithDetails(@Param("studentId") Integer studentId);

    /**
     * 获取课程的学生数量
     */
    @Select("SELECT COUNT(*) FROM student_course " +
            "WHERE course_id = #{courseId} AND status = 1")
    int getCourseStudentCount(@Param("courseId") Integer courseId);

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
     * 获取课程的选课学生列表（包含学生信息）
     */
    @Select("SELECT sc.*, " +
            "s.stu_id as stuId, s.stu_account as stuNum, s.stu_name as stuName " +
            "FROM student_course sc " +
            "LEFT JOIN student s ON sc.student_id = s.stu_id " +
            "WHERE sc.course_id = #{courseId} AND sc.status = 1 AND s.del = 0 " +
            "ORDER BY sc.enroll_time DESC")
    List<StudentCourse> getCourseStudentsWithInfo(@Param("courseId") Integer courseId);
}