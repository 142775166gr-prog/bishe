package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 公告表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    /**
     * 获取用户未读公告列表
     */
    @Select("SELECT a.* FROM announcement a " +
            "WHERE a.status = 1 AND a.del = 0 " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM announcement_read ar " +
            "    WHERE ar.announcement_id = a.announcement_id " +
            "    AND ar.user_id = #{userId} AND ar.user_type = #{userType}" +
            ") " +
            "ORDER BY a.publish_time DESC")
    List<Announcement> getUnreadAnnouncements(@Param("userId") Integer userId, @Param("userType") String userType);
}