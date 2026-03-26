package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.CourseComment;

import java.util.List;

/**
 * <p>
 * 课程评论表 服务类
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
public interface CourseCommentService extends IService<CourseComment> {

    /**
     * 获取课程的评论列表（包含回复）
     */
    List<CourseComment> getCommentsByCourseId(Integer courseId);

    /**
     * 发表评论
     */
    boolean addComment(CourseComment comment);

    /**
     * 回复评论
     */
    boolean replyComment(CourseComment comment);

    /**
     * 删除评论（用户删除自己的评论）
     */
    boolean deleteComment(Integer commentId, Integer userId, String userType);

    /**
     * 管理员删除评论
     */
    boolean adminDeleteComment(Integer commentId);

    /**
     * 获取课程评论总数
     */
    Integer getCommentCount(Integer courseId);

    /**
     * 获取用户未读的回复数量
     */
    Integer getUnreadReplyCount(Integer userId, String userType);

    /**
     * 获取用户未读的回复列表
     */
    List<CourseComment> getUnreadReplies(Integer userId, String userType);

    /**
     * 标记回复为已读
     */
    boolean markAsRead(Integer commentId, Integer userId, String userType);

    /**
     * 标记所有回复为已读
     */
    boolean markAllAsRead(Integer userId, String userType);
}
