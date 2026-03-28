package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.CourseCommentMapper;
import com.example.zhjypt.mapper.StudentMapper;
import com.example.zhjypt.mapper.TeacherMapper;
import com.example.zhjypt.pojo.CourseComment;
import com.example.zhjypt.pojo.Student;
import com.example.zhjypt.pojo.Teacher;
import com.example.zhjypt.service.CourseCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 课程评论表 服务实现类
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@Service
public class CourseCommentServiceImpl extends ServiceImpl<CourseCommentMapper, CourseComment> implements CourseCommentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public List<CourseComment> getCommentsByCourseId(Integer courseId) {
        // 获取顶级评论
        List<CourseComment> topComments = baseMapper.getTopLevelComments(courseId);
        
        // 为每个顶级评论加载用户最新信息和回复
        for (CourseComment comment : topComments) {
            // 加载用户最新头像和名字
            fillUserInfo(comment);
            
            List<CourseComment> replies = baseMapper.getReplies(comment.getCommentId());
            // 为每个回复加载用户最新信息
            for (CourseComment reply : replies) {
                fillUserInfo(reply);
                // 更新被回复人的最新名字
                if (reply.getReplyToId() != null && reply.getReplyToId() > 0) {
                    CourseComment replyToComment = this.getById(reply.getReplyToId());
                    if (replyToComment != null) {
                        String replyToName = getUserName(replyToComment.getUserId(), replyToComment.getUserType());
                        if (replyToName != null) {
                            reply.setReplyToName(replyToName);
                        }
                    }
                }
            }
            comment.setReplies(replies);
        }
        
        return topComments;
    }

    /**
     * 填充用户最新的头像和名字
     */
    private void fillUserInfo(CourseComment comment) {
        if ("student".equals(comment.getUserType())) {
            Student student = studentMapper.selectById(comment.getUserId());
            if (student != null) {
                comment.setUserName(student.getStuName());
                comment.setUserAvatar(student.getAvatar());
            }
        } else if ("teacher".equals(comment.getUserType())) {
            Teacher teacher = teacherMapper.selectById(comment.getUserId());
            if (teacher != null) {
                comment.setUserName(teacher.getTeachName());
                comment.setUserAvatar(teacher.getAvatar());
            }
        }
    }

    /**
     * 获取用户最新名字
     */
    private String getUserName(Integer userId, String userType) {
        if ("student".equals(userType)) {
            Student student = studentMapper.selectById(userId);
            return student != null ? student.getStuName() : null;
        } else if ("teacher".equals(userType)) {
            Teacher teacher = teacherMapper.selectById(userId);
            return teacher != null ? teacher.getTeachName() : null;
        }
        return null;
    }

    @Override
    public boolean addComment(CourseComment comment) {
        comment.setParentId(0);
        comment.setReplyToId(0);
        comment.setCreateTime(new Date());
        comment.setDel(0);
        return this.save(comment);
    }

    @Override
    public boolean replyComment(CourseComment comment) {
        // 获取被回复的评论
        CourseComment parentComment = this.getById(comment.getParentId());
        if (parentComment == null) {
            return false;
        }
        
        // 如果回复的是子评论，则parentId设为顶级评论的ID
        if (parentComment.getParentId() != 0) {
            comment.setParentId(parentComment.getParentId());
        }
        
        comment.setCreateTime(new Date());
        comment.setDel(0);
        comment.setIsRead(0); // 新回复默认未读
        return this.save(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Integer commentId, Integer userId, String userType) {
        // 验证是否是评论的作者
        CourseComment comment = this.getById(commentId);
        if (comment == null) {
            return false;
        }
        
        if (!comment.getUserId().equals(userId) || !comment.getUserType().equals(userType)) {
            return false; // 不是作者，无权删除
        }
        
        // 逻辑删除评论
        UpdateWrapper<CourseComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("comment_id", commentId).set("del", 1);
        boolean success = this.update(updateWrapper);
        
        // 同时删除该评论的所有回复
        if (success && comment.getParentId() == 0) {
            UpdateWrapper<CourseComment> replyWrapper = new UpdateWrapper<>();
            replyWrapper.eq("parent_id", commentId).set("del", 1);
            this.update(replyWrapper);
        }
        
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adminDeleteComment(Integer commentId) {
        CourseComment comment = this.getById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 逻辑删除评论
        UpdateWrapper<CourseComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("comment_id", commentId).set("del", 1);
        boolean success = this.update(updateWrapper);
        
        // 如果是顶级评论，同时删除所有回复
        if (success && comment.getParentId() == 0) {
            UpdateWrapper<CourseComment> replyWrapper = new UpdateWrapper<>();
            replyWrapper.eq("parent_id", commentId).set("del", 1);
            this.update(replyWrapper);
        }
        
        return success;
    }

    @Override
    public Integer getCommentCount(Integer courseId) {
        return baseMapper.getCommentCount(courseId);
    }

    @Override
    public Integer getUnreadReplyCount(Integer userId, String userType) {
        return baseMapper.getUnreadReplyCount(userId, userType);
    }

    @Override
    public List<CourseComment> getUnreadReplies(Integer userId, String userType) {
        List<CourseComment> replies = baseMapper.getUnreadReplies(userId, userType);
        // 填充用户最新信息
        for (CourseComment reply : replies) {
            fillUserInfo(reply);
        }
        return replies;
    }

    @Override
    public boolean markAsRead(Integer commentId, Integer userId, String userType) {
        // 验证这条回复确实是回复给该用户的
        CourseComment reply = this.getById(commentId);
        if (reply == null || reply.getReplyToId() == null || reply.getReplyToId() == 0) {
            return false;
        }
        
        CourseComment originalComment = this.getById(reply.getReplyToId());
        if (originalComment == null || !originalComment.getUserId().equals(userId) 
                || !originalComment.getUserType().equals(userType)) {
            return false;
        }
        
        UpdateWrapper<CourseComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("comment_id", commentId).set("is_read", 1);
        return this.update(updateWrapper);
    }

    @Override
    public boolean markAllAsRead(Integer userId, String userType) {
        // 获取所有未读回复并标记为已读
        List<CourseComment> unreadReplies = baseMapper.getUnreadReplies(userId, userType);
        if (unreadReplies.isEmpty()) {
            return true;
        }
        
        for (CourseComment reply : unreadReplies) {
            UpdateWrapper<CourseComment> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("comment_id", reply.getCommentId()).set("is_read", 1);
            this.update(updateWrapper);
        }
        return true;
    }
}
