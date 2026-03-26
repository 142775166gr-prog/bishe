package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.Course;

import java.util.List;

/**
 * <p>
 * 课程表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页查询课程列表（管理员视图）
     */
    Page<Course> getCoursePage(String courseTitle, Integer categoryId, Integer courseStatus, Integer pageNum, Integer pageSize);

    /**
     * 分页查询教师课程列表
     */
    Page<Course> getTeacherCoursePage(Integer teacherId, String courseTitle, Integer courseStatus, Integer pageNum, Integer pageSize);

    /**
     * 分页查询已发布课程列表（学生视图）
     */
    Page<Course> getPublishedCoursePage(Integer categoryId, String courseTitle, Integer pageNum, Integer pageSize);

    /**
     * 创建课程
     */
    boolean createCourse(Course course);

    /**
     * 更新课程
     */
    boolean updateCourse(Course course);

    /**
     * 发布课程
     */
    boolean publishCourse(Integer courseId, Integer teacherId);

    /**
     * 下架课程
     */
    boolean unpublishCourse(Integer courseId, Integer teacherId);

    /**
     * 删除课程
     */
    boolean deleteCourse(Integer courseId, Integer teacherId);

    /**
     * 获取课程详情
     */
    Course getCourseDetail(Integer courseId);

    /**
     * 根据ID获取课程信息
     */
    Course getCourseById(Integer courseId);

    /**
     * 获取教师的课程列表
     */
    List<Course> getTeacherCoursesList(Integer teacherId);

    /**
     * 获取已发布的课程列表
     */
    List<Course> getPublishedCoursesList();

    /**
     * 根据分类获取课程
     */
    List<Course> getCoursesByCategoryId(Integer categoryId);
}