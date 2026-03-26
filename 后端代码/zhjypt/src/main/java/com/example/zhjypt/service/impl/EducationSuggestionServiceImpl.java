package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.EducationSuggestionMapper;
import com.example.zhjypt.pojo.EducationSuggestion;
import com.example.zhjypt.service.EducationSuggestionService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 教育建议服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@Service
public class EducationSuggestionServiceImpl extends ServiceImpl<EducationSuggestionMapper, EducationSuggestion> implements EducationSuggestionService {

    @Override
    public boolean createSuggestion(EducationSuggestion suggestion) {
        suggestion.setIsRead(0); // 未读
        suggestion.setIsFavorite(0); // 未收藏
        suggestion.setCreateTime(new Date());
        return this.save(suggestion);
    }

    @Override
    public boolean markAsRead(Integer suggestionId, Integer studentId) {
        EducationSuggestion suggestion = this.getById(suggestionId);
        if (suggestion == null || !suggestion.getStudentId().equals(studentId)) {
            return false;
        }
        
        suggestion.setIsRead(1);
        suggestion.setReadTime(new Date());
        return this.updateById(suggestion);
    }

    @Override
    public boolean toggleFavorite(Integer suggestionId, Integer studentId) {
        EducationSuggestion suggestion = this.getById(suggestionId);
        if (suggestion == null || !suggestion.getStudentId().equals(studentId)) {
            return false;
        }
        
        suggestion.setIsFavorite(suggestion.getIsFavorite() == 1 ? 0 : 1);
        return this.updateById(suggestion);
    }

    @Override
    public Page<EducationSuggestion> getStudentSuggestions(Integer studentId, Integer suggestionType, Integer isRead, Integer isFavorite, Integer pageNum, Integer pageSize) {
        Page<EducationSuggestion> page = new Page<>(pageNum, pageSize);
        QueryWrapper<EducationSuggestion> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        // 按数据库字段过滤逻辑删除
        wrapper.eq("del", 0);
        
        if (suggestionType != null) {
            wrapper.eq("suggestion_type", suggestionType);
        }
        
        if (isRead != null) {
            wrapper.eq("is_read", isRead);
        }

        if (isFavorite != null) {
            wrapper.eq("is_favorite", isFavorite);
        }
        
        wrapper.orderByDesc("create_time");
        
        return this.page(page, wrapper);
    }

    @Override
    public Page<EducationSuggestion> getTeacherSuggestions(Integer teacherId, Integer pageNum, Integer pageSize) {
        Page<EducationSuggestion> page = new Page<>(pageNum, pageSize);
        QueryWrapper<EducationSuggestion> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id", teacherId);
        wrapper.eq("del", 0);
        wrapper.orderByDesc("create_time");
        
        return this.page(page, wrapper);
    }

    @Override
    public EducationSuggestion getSuggestionDetail(Integer suggestionId) {
        return this.getById(suggestionId);
    }

    @Override
    public boolean deleteSuggestion(Integer suggestionId, Integer userId, String userType) {
        EducationSuggestion suggestion = this.getById(suggestionId);
        if (suggestion == null) {
            return false;
        }
        
        // 验证权限：学生只能删除自己收到的建议，教师只能删除自己发送的建议
        if ("student".equals(userType) && !suggestion.getStudentId().equals(userId)) {
            return false;
        }
        if ("teacher".equals(userType) && !suggestion.getTeacherId().equals(userId)) {
            return false;
        }
        
        return this.removeById(suggestionId);
    }

    @Override
    public Integer getUnreadCountByStudent(Integer studentId) {
        QueryWrapper<EducationSuggestion> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        wrapper.eq("is_read", 0);
        wrapper.eq("del", 0);
        long count = this.count(wrapper);
        return (int) count;
    }
}
