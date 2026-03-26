package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.ChapterContentMapper;
import com.example.zhjypt.mapper.CourseChapterMapper;
import com.example.zhjypt.mapper.StudentContentProgressMapper;
import com.example.zhjypt.mapper.StudentCourseMapper;
import com.example.zhjypt.pojo.StudentCourse;
import com.example.zhjypt.service.StudentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 学生选课表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Service
public class StudentCourseServiceImpl extends ServiceImpl<StudentCourseMapper, StudentCourse> implements StudentCourseService {

    @Autowired
    private CourseChapterMapper courseChapterMapper;
    
    @Autowired
    private StudentContentProgressMapper studentContentProgressMapper;
    
    @Autowired
    private ChapterContentMapper chapterContentMapper;

    @Override
    public boolean enrollCourse(Integer studentId, Integer courseId) {
        // 检查是否已经有选课记录（任何状态）- 只查询基本字段
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("course_id", courseId)
                .select("id", "student_id", "course_id", "enroll_time", "progress", "last_study_time", "status"); // 只查询数据库中存在的字段
        
        StudentCourse existingRecord = this.getOne(wrapper);
        
        if (existingRecord != null) {
            // 如果已有记录但状态不是正常，更新为正常状态
            if (existingRecord.getStatus() != 1) {
                existingRecord.setStatus(1);
                existingRecord.setEnrollTime(new Date());
                existingRecord.setProgress(new BigDecimal("0.00"));
                return this.updateById(existingRecord);
            } else {
                // 已经是正常选课状态
                return false;
            }
        } else {
            // 没有记录，创建新的选课记录
            StudentCourse studentCourse = new StudentCourse();
            studentCourse.setStudentId(studentId);
            studentCourse.setCourseId(courseId);
            studentCourse.setEnrollTime(new Date());
            studentCourse.setProgress(new BigDecimal("0.00"));
            studentCourse.setStatus(1); // 正常状态
            
            return this.save(studentCourse);
        }
    }

    @Override
    public boolean checkEnrolled(Integer studentId, Integer courseId) {
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("course_id", courseId)
                .eq("status", 1); // 只查询正常状态的选课记录
        
        return this.count(wrapper) > 0;
    }

    @Override
    public List<StudentCourse> getStudentCourses(Integer studentId) {
        // 获取学生的课程列表
        List<StudentCourse> courses = baseMapper.getStudentCoursesWithDetails(studentId);
        
        // 为每门课程实时计算基于权重的进度
        for (StudentCourse course : courses) {
            try {
                // 直接调用Mapper方法计算进度，避免循环依赖
                BigDecimal realTimeProgress = baseMapper.calculateCourseProgress(
                    studentId, course.getCourseId());
                
                if (realTimeProgress != null) {
                    course.setProgress(realTimeProgress);
                    System.out.println("课程 " + course.getCourseTitle() + " 的实时进度: " + realTimeProgress + "%");
                } else {
                    course.setProgress(new BigDecimal("0.00"));
                }
            } catch (Exception e) {
                System.err.println("计算课程进度失败: " + e.getMessage());
                // 如果计算失败，保持原有进度
            }
        }
        
        return courses;
    }

    @Override
    public boolean withdrawCourse(Integer studentId, Integer courseId) {
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("course_id", courseId)
                .eq("status", 1);
        
        StudentCourse studentCourse = this.getOne(wrapper);
        if (studentCourse != null) {
            studentCourse.setStatus(0); // 设置为退课状态
            return this.updateById(studentCourse);
        }
        return false;
    }

    @Override
    public boolean updateProgress(Integer studentId, Integer courseId, BigDecimal progress) {
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("course_id", courseId)
                .eq("status", 1);
        
        StudentCourse studentCourse = this.getOne(wrapper);
        if (studentCourse != null) {
            studentCourse.setProgress(progress);
            studentCourse.setLastStudyTime(new Date());
            return this.updateById(studentCourse);
        }
        return false;
    }

    @Override
    public List getCourseStudents(Integer courseId) {
        // 使用 Mapper 方法获取包含学生信息的选课列表
        List<StudentCourse> studentCourses = baseMapper.getCourseStudentsWithInfo(courseId);
        
        // 统计课程的总章节数
        QueryWrapper<com.example.zhjypt.pojo.CourseChapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("course_id", courseId);
        chapterWrapper.eq("del", 0);
        Integer totalChapters = courseChapterMapper.selectCount(chapterWrapper);
        
        // 为每个学生计算统计信息
        for (StudentCourse sc : studentCourses) {
            try {
                // 计算学习进度
                BigDecimal realTimeProgress = baseMapper.calculateCourseProgress(
                    sc.getStudentId(), courseId);
                sc.setProgress(realTimeProgress != null ? realTimeProgress : new BigDecimal("0.00"));
                
                // 设置总章节数
                sc.setTotalChapters(totalChapters);
                
                // 统计已完成的章节数（至少完成了该章节中一个内容的章节）
                int completedChapters = 0;
                if (totalChapters > 0) {
                    // 获取该课程的所有章节ID
                    QueryWrapper<com.example.zhjypt.pojo.CourseChapter> chaptersQuery = new QueryWrapper<>();
                    chaptersQuery.eq("course_id", courseId);
                    chaptersQuery.eq("del", 0);
                    chaptersQuery.select("chapter_id");
                    List<com.example.zhjypt.pojo.CourseChapter> chapters = courseChapterMapper.selectList(chaptersQuery);
                    
                    // 对每个章节，检查学生是否完成了至少一个内容
                    for (com.example.zhjypt.pojo.CourseChapter chapter : chapters) {
                        Integer chapterId = chapter.getChapterId();
                        QueryWrapper<com.example.zhjypt.pojo.StudentContentProgress> progressWrapper = new QueryWrapper<>();
                        progressWrapper.eq("student_id", sc.getStudentId());
                        progressWrapper.eq("is_completed", 1);
                        progressWrapper.inSql("content_id", "SELECT content_id FROM chapter_content WHERE chapter_id = " + chapterId + " AND del = 0");
                        Integer count = studentContentProgressMapper.selectCount(progressWrapper);
                        if (count > 0) {
                            completedChapters++;
                        }
                    }
                }
                sc.setCompletedChapters(completedChapters);
                
            } catch (Exception e) {
                System.err.println("计算学生统计信息失败: " + e.getMessage());
                e.printStackTrace();
                sc.setProgress(new BigDecimal("0.00"));
                sc.setTotalChapters(totalChapters);
                sc.setCompletedChapters(0);
            }
        }
        
        return studentCourses;
    }

    @Override
    public List getStudentChapterProgress(Integer studentId, Integer courseId) {
        // 获取课程的所有章节
        QueryWrapper<com.example.zhjypt.pojo.CourseChapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("course_id", courseId);
        chapterWrapper.eq("del", 0);
        chapterWrapper.orderByAsc("sort_order");
        List<com.example.zhjypt.pojo.CourseChapter> chapters = courseChapterMapper.selectList(chapterWrapper);
        
        // 为每个章节统计学习情况
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (com.example.zhjypt.pojo.CourseChapter chapter : chapters) {
            java.util.Map<String, Object> chapterData = new java.util.HashMap<>();
            chapterData.put("chapterId", chapter.getChapterId());
            chapterData.put("chapterTitle", chapter.getChapterTitle());
            
            // 统计该章节的总内容数
            QueryWrapper<com.example.zhjypt.pojo.ChapterContent> contentWrapper = new QueryWrapper<>();
            contentWrapper.eq("chapter_id", chapter.getChapterId());
            contentWrapper.eq("del", 0);
            Integer totalContents = chapterContentMapper.selectCount(contentWrapper);
            chapterData.put("totalContents", totalContents);
            
            // 统计学生已完成的内容数
            QueryWrapper<com.example.zhjypt.pojo.StudentContentProgress> progressWrapper = new QueryWrapper<>();
            progressWrapper.eq("student_id", studentId);
            progressWrapper.eq("chapter_id", chapter.getChapterId());
            progressWrapper.eq("is_completed", 1);
            Integer completedContents = studentContentProgressMapper.selectCount(progressWrapper);
            chapterData.put("completedContents", completedContents);
            
            // 计算完成度
            double progress = totalContents > 0 ? (completedContents * 100.0 / totalContents) : 0;
            chapterData.put("progress", Math.round(progress));
            
            // 获取最后学习时间
            QueryWrapper<com.example.zhjypt.pojo.StudentContentProgress> lastStudyWrapper = new QueryWrapper<>();
            lastStudyWrapper.eq("student_id", studentId);
            lastStudyWrapper.eq("chapter_id", chapter.getChapterId());
            lastStudyWrapper.orderByDesc("update_time");
            lastStudyWrapper.last("LIMIT 1");
            com.example.zhjypt.pojo.StudentContentProgress lastProgress = studentContentProgressMapper.selectOne(lastStudyWrapper);
            chapterData.put("lastStudyTime", lastProgress != null ? lastProgress.getUpdateTime() : null);
            
            result.add(chapterData);
        }
        
        return result;
    }
}
