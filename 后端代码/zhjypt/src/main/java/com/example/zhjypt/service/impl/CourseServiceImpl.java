package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.CourseMapper;
import com.example.zhjypt.pojo.Course;
import com.example.zhjypt.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 课程服务实现类
 * <p>
 * 负责课程的增删改查、发布/下架、分页查询等核心业务逻辑。
 * 分页查询采用"先全量查询再内存分页"的方式，以便在 SQL 层做联表查询（带教师名、分类名等扩展字段）。
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    /**
     * 管理员视角：分页查询课程列表（支持按标题、分类、状态筛选）
     * <p>
     * 实现思路：先通过自定义 Mapper 方法联表查出全量数据（含教师名、分类名），
     * 再在内存中手动截取当前页的数据，避免复杂联表 SQL 与 MyBatis-Plus 分页插件冲突。
     * </p>
     *
     * @param courseTitle  课程标题关键词（模糊匹配，可为 null）
     * @param categoryId   课程分类 ID（精确匹配，可为 null）
     * @param courseStatus 课程状态（0草稿/1发布/2下架，可为 null 表示不过滤）
     * @param pageNum      当前页码（从 1 开始）
     * @param pageSize     每页条数
     * @return 包含分页数据和总数的 Page 对象
     */
    @Override
    public Page<Course> getCoursePage(String courseTitle, Integer categoryId, Integer courseStatus, Integer pageNum, Integer pageSize) {
        // 构建分页对象（仅用于承载结果，实际分页在内存中完成）
        Page<Course> page = new Page<>(pageNum, pageSize);
        // 调用自定义 Mapper 方法，联表查询全量数据
        List<Course> records = baseMapper.getCoursePageWithDetails(courseTitle, categoryId, courseStatus);

        // 内存分页：计算当前页的起止下标
        int total = records.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        if (start < total) {
            page.setRecords(records.subList(start, end));
        }
        page.setTotal(total);
        
        return page;
    }

    @Override
    public Page<Course> getTeacherCoursePage(Integer teacherId, String courseTitle, Integer courseStatus, Integer pageNum, Integer pageSize) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        List<Course> records = baseMapper.getTeacherCoursePageWithDetails(teacherId, courseTitle, courseStatus);
        
        // 手动分页
        int total = records.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        if (start < total) {
            page.setRecords(records.subList(start, end));
        }
        page.setTotal(total);
        
        return page;
    }

    @Override
    public Page<Course> getPublishedCoursePage(Integer categoryId, String courseTitle, Integer pageNum, Integer pageSize) {
        Page<Course> page = new Page<>(pageNum, pageSize);
        List<Course> records = baseMapper.getPublishedCoursePageWithDetails(categoryId, courseTitle);
        
        // 手动分页
        int total = records.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        if (start < total) {
            page.setRecords(records.subList(start, end));
        }
        page.setTotal(total);
        
        return page;
    }

    @Override
    public boolean createCourse(Course course) {
        course.setCreateTime(new Date());
        course.setUpdateTime(new Date());
        course.setCourseStatus(0); // 默认为草稿状态
        course.setStudentCount(0);
        course.setCourseDuration(0);
        course.setDel(0);
        return this.save(course);
    }

    @Override
    public boolean updateCourse(Course course) {
        course.setUpdateTime(new Date());
        return this.updateById(course);
    }

    @Override
    public boolean publishCourse(Integer courseId, Integer teacherId) {
        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseStatus(1);
        course.setPublishTime(new Date());
        course.setUpdateTime(new Date());
        
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.eq("del", 0);
        
        return this.update(course, queryWrapper);
    }

    @Override
    public boolean unpublishCourse(Integer courseId, Integer teacherId) {
        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseStatus(2);
        course.setUpdateTime(new Date());
        
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.eq("del", 0);
        
        return this.update(course, queryWrapper);
    }

    @Override
    public boolean deleteCourse(Integer courseId, Integer teacherId) {
        Course course = new Course();
        course.setCourseId(courseId);
        course.setDel(1);
        course.setUpdateTime(new Date());
        
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("teacher_id", teacherId);
        queryWrapper.eq("del", 0);
        
        return this.update(course, queryWrapper);
    }

    @Override
    public Course getCourseDetail(Integer courseId) {
        return baseMapper.getCourseDetail(courseId);
    }

    @Override
    public Course getCourseById(Integer courseId) {
        return baseMapper.getCourseDetail(courseId);
    }

    @Override
    public List<Course> getTeacherCoursesList(Integer teacherId) {
        return baseMapper.getTeacherCoursesList(teacherId);
    }

    @Override
    public List<Course> getPublishedCoursesList() {
        return baseMapper.getPublishedCoursesList();
    }

    @Override
    public List<Course> getCoursesByCategoryId(Integer categoryId) {
        return baseMapper.getCoursesByCategoryId(categoryId);
    }
}