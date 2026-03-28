package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.StudentContentProgressMapper;
import com.example.zhjypt.pojo.StudentContentProgress;
import com.example.zhjypt.service.StudentContentProgressService;
import com.example.zhjypt.service.StudentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生内容学习进度表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Service
public class StudentContentProgressServiceImpl extends ServiceImpl<StudentContentProgressMapper, StudentContentProgress> 
        implements StudentContentProgressService {

    @Autowired
    private StudentCourseService studentCourseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProgress(Integer studentId, Integer courseId, Integer chapterId, 
                                 Integer contentId, Integer watchProgress) {
        // 查找现有记录
        QueryWrapper<StudentContentProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("content_id", contentId);
        
        StudentContentProgress progress = this.getOne(wrapper);
        
        if (progress != null) {
            // 更新现有记录 - 进度只能增加，不能倒退
            boolean needUpdate = false;
            
            // 如果新的观看进度更高，才更新
            if (watchProgress > progress.getWatchProgress()) {
                progress.setWatchProgress(watchProgress);
                progress.setUpdateTime(new Date());
                needUpdate = true;
            }
            
            // 如果还未完成，但观看进度达到80%以上，标记为已完成
            if (progress.getIsCompleted() == 0 && watchProgress >= 80) {
                progress.setIsCompleted(1);
                progress.setCompletedTime(new Date());
                needUpdate = true;
            }
            
            boolean updated = false;
            if (needUpdate) {
                updated = this.updateById(progress);
                
                // 更新学生课程总进度
                if (updated) {
                    updateStudentCourseProgress(studentId, courseId);
                }
            }
            
            return needUpdate ? updated : true; // 如果不需要更新也返回true
        } else {
            // 创建新记录
            StudentContentProgress newProgress = new StudentContentProgress();
            newProgress.setStudentId(studentId);
            newProgress.setCourseId(courseId);
            newProgress.setChapterId(chapterId);
            newProgress.setContentId(contentId);
            newProgress.setWatchProgress(watchProgress);
            newProgress.setFirstStudyTime(new Date());
            newProgress.setUpdateTime(new Date());
            
            // 如果观看进度达到80%以上，标记为已完成
            if (watchProgress >= 80) {
                newProgress.setIsCompleted(1);
                newProgress.setCompletedTime(new Date());
            }
            
            boolean saved = this.save(newProgress);
            
            // 更新学生课程总进度
            if (saved) {
                updateStudentCourseProgress(studentId, courseId);
            }
            
            return saved;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markCompleted(Integer studentId, Integer courseId, Integer chapterId, Integer contentId) {
        // 自调用不会走代理，此处单独加事务，保证与 updateProgress 内多步写库一致
        return updateProgress(studentId, courseId, chapterId, contentId, 100);
    }

    @Override
    public BigDecimal calculateCourseProgress(Integer studentId, Integer courseId) {
        return baseMapper.calculateCourseProgress(studentId, courseId);
    }

    @Override
    public Map<String, Object> getCourseProgressStats(Integer studentId, Integer courseId) {
        return baseMapper.getCourseProgressStats(studentId, courseId);
    }

    @Override
    public List<StudentContentProgress> getStudentCourseProgress(Integer studentId, Integer courseId) {
        QueryWrapper<StudentContentProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("course_id", courseId)
                .orderByAsc("chapter_id", "content_id");
        
        return this.list(wrapper);
    }

    @Override
    public boolean isContentCompleted(Integer studentId, Integer contentId) {
        QueryWrapper<StudentContentProgress> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("content_id", contentId)
                .eq("is_completed", 1);
        
        return this.count(wrapper) > 0;
    }

    /**
     * 更新学生课程总进度
     */
    private void updateStudentCourseProgress(Integer studentId, Integer courseId) {
        BigDecimal courseProgress = calculateCourseProgress(studentId, courseId);
        studentCourseService.updateProgress(studentId, courseId, courseProgress);
    }
}