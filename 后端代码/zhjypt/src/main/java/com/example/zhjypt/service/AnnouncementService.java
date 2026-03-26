package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.Announcement;

import java.util.List;

/**
 * <p>
 * 公告表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
public interface AnnouncementService extends IService<Announcement> {

    /**
     * 分页查询公告列表
     */
    Page<Announcement> getAnnouncementPage(String title, Integer pageNum, Integer pageSize);

    /**
     * 发布公告
     */
    boolean publishAnnouncement(Announcement announcement);

    /**
     * 获取用户未读公告列表
     */
    List<Announcement> getUnreadAnnouncements(Integer userId, String userType);

    /**
     * 标记公告为已读
     */
    boolean markAsRead(Integer announcementId, Integer userId, String userType);

    /**
     * 获取公告详情
     */
    Announcement getAnnouncementDetail(Integer announcementId);
}