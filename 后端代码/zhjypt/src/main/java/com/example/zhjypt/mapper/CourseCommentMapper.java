package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.CourseComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程评论表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@Mapper
public interface CourseCommentMapper extends BaseMapper<CourseComment> {

    /**
     * 获取课程的顶级评论列表
     */
    @Select("SELECT * FROM course_comment WHERE course_id = #{courseId} AND parent_id = 0 AND del = 0 ORDER BY create_time DESC")
    List<CourseComment> getTopLevelComments(@Param("courseId") Integer courseId);

    /**
     * 获取评论的回复列表
     */
    @Select("SELECT * FROM course_comment WHERE parent_id = #{parentId} AND del = 0 ORDER BY create_time ASC")
    List<CourseComment> getReplies(@Param("parentId") Integer parentId);

    /**
     * 获取课程的评论总数
     */
    @Select("SELECT COUNT(*) FROM course_comment WHERE course_id = #{courseId} AND del = 0")
    Integer getCommentCount(@Param("courseId") Integer courseId);

    /**
     * 获取用户未读的回复数量
     */
    @Select("SELECT COUNT(*) FROM course_comment c1 " +
            "WHERE c1.del = 0 AND c1.is_read = 0 AND c1.reply_to_id > 0 " +
            "AND EXISTS (SELECT 1 FROM course_comment c2 WHERE c2.comment_id = c1.reply_to_id " +
            "AND c2.user_id = #{userId} AND c2.user_type = #{userType} AND c2.del = 0)")
    Integer getUnreadReplyCount(@Param("userId") Integer userId, @Param("userType") String userType);

    /**
     * 获取用户未读的回复列表
     */
    @Select("SELECT c1.* FROM course_comment c1 " +
            "WHERE c1.del = 0 AND c1.is_read = 0 AND c1.reply_to_id > 0 " +
            "AND EXISTS (SELECT 1 FROM course_comment c2 WHERE c2.comment_id = c1.reply_to_id " +
            "AND c2.user_id = #{userId} AND c2.user_type = #{userType} AND c2.del = 0) " +
            "ORDER BY c1.create_time DESC")
    List<CourseComment> getUnreadReplies(@Param("userId") Integer userId, @Param("userType") String userType);
}
