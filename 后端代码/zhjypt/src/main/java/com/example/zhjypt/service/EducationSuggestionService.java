package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.EducationSuggestion;

/**
 * <p>
 * 教育建议服务类
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
public interface EducationSuggestionService extends IService<EducationSuggestion> {

    /**
     * 教师创建建议
     */
    boolean createSuggestion(EducationSuggestion suggestion);

    /**
     * 标记为已读
     */
    boolean markAsRead(Integer suggestionId, Integer studentId);

    /**
     * 收藏/取消收藏
     */
    boolean toggleFavorite(Integer suggestionId, Integer studentId);

    /**
     * 获取学生收到的建议列表（分页）
     */
    Page<EducationSuggestion> getStudentSuggestions(Integer studentId, Integer suggestionType, Integer isRead, Integer isFavorite, Integer pageNum, Integer pageSize);

    /**
     * 获取教师发送的建议列表（分页）
     */
    Page<EducationSuggestion> getTeacherSuggestions(Integer teacherId, Integer pageNum, Integer pageSize);

    /**
     * 获取建议详情
     */
    EducationSuggestion getSuggestionDetail(Integer suggestionId);

    /**
     * 删除建议
     */
    boolean deleteSuggestion(Integer suggestionId, Integer userId, String userType);

    /**
     * 获取学生未读建议数量
     */
    Integer getUnreadCountByStudent(Integer studentId);
}
