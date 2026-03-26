package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 获取教师的课程列表
     */
    @Select("SELECT c.*, t.teach_name as teacher_name, cc.category_name, " +
            "(SELECT COUNT(*) FROM student_course sc WHERE sc.course_id = c.course_id AND sc.status = 1) as studentCount " +
            "FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE c.teacher_id = #{teacherId} AND c.del = 0 " +
            "ORDER BY c.create_time DESC")
    List<Course> getTeacherCoursesList(@Param("teacherId") Integer teacherId);

    /**
     * 获取已发布的课程列表（学生视图）
     */
    @Select("SELECT c.*, t.teach_name as teacher_name, cc.category_name FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE c.course_status = 1 AND c.del = 0 " +
            "ORDER BY c.publish_time DESC")
    List<Course> getPublishedCoursesList();

    /**
     * 根据分类获取已发布的课程
     */
    @Select("SELECT c.*, t.teach_name as teacher_name, cc.category_name FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE c.course_status = 1 AND c.del = 0 AND c.category_id = #{categoryId} " +
            "ORDER BY c.publish_time DESC")
    List<Course> getCoursesByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * 获取课程详情（带教师姓名和分类名称）
     */
    @Select("SELECT c.*, t.teach_name as teacher_name, cc.category_name FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE c.course_id = #{courseId} AND c.del = 0")
    Course getCourseDetail(@Param("courseId") Integer courseId);

    /**
     * 分页查询课程列表（管理员视图）- 带教师姓名和分类名称
     */
    @Select("<script>" +
            "SELECT c.*, t.teach_name as teacher_name, cc.category_name FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE c.del = 0 " +
            "<if test='courseTitle != null and courseTitle != \"\"'>" +
            "AND c.course_title LIKE CONCAT('%', #{courseTitle}, '%') " +
            "</if>" +
            "<if test='categoryId != null and categoryId > 0'>" +
            "AND c.category_id = #{categoryId} " +
            "</if>" +
            "<if test='courseStatus != null'>" +
            "AND c.course_status = #{courseStatus} " +
            "</if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    List<Course> getCoursePageWithDetails(@Param("courseTitle") String courseTitle, 
                                         @Param("categoryId") Integer categoryId, 
                                         @Param("courseStatus") Integer courseStatus);

    /**
     * 分页查询教师课程列表 - 带分类名称和学生数
     */
    @Select("<script>" +
            "SELECT c.*, t.teach_name as teacher_name, cc.category_name, " +
            "(SELECT COUNT(*) FROM student_course sc WHERE sc.course_id = c.course_id AND sc.status = 1) as studentCount " +
            "FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE c.del = 0 AND c.teacher_id = #{teacherId} " +
            "<if test='courseTitle != null and courseTitle != \"\"'>" +
            "AND c.course_title LIKE CONCAT('%', #{courseTitle}, '%') " +
            "</if>" +
            "<if test='courseStatus != null'>" +
            "AND c.course_status = #{courseStatus} " +
            "</if>" +
            "ORDER BY c.create_time DESC" +
            "</script>")
    List<Course> getTeacherCoursePageWithDetails(@Param("teacherId") Integer teacherId,
                                                @Param("courseTitle") String courseTitle, 
                                                @Param("courseStatus") Integer courseStatus);

    /**
     * 分页查询已发布课程列表（学生视图）- 带教师姓名和分类名称
     */
    @Select("<script>" +
            "SELECT c.*, t.teach_name as teacher_name, cc.category_name FROM course c " +
            "LEFT JOIN teacher t ON c.teacher_id = t.teach_id " +
            "LEFT JOIN course_category cc ON c.category_id = cc.category_id " +
            "WHERE c.del = 0 AND c.course_status = 1 " +
            "<if test='categoryId != null and categoryId > 0'>" +
            "AND c.category_id = #{categoryId} " +
            "</if>" +
            "<if test='courseTitle != null and courseTitle != \"\"'>" +
            "AND c.course_title LIKE CONCAT('%', #{courseTitle}, '%') " +
            "</if>" +
            "ORDER BY c.publish_time DESC" +
            "</script>")
    List<Course> getPublishedCoursePageWithDetails(@Param("categoryId") Integer categoryId, 
                                                  @Param("courseTitle") String courseTitle);
}