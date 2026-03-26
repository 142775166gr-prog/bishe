package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.AnnouncementMapper;
import com.example.zhjypt.mapper.AnnouncementReadMapper;
import com.example.zhjypt.pojo.Announcement;
import com.example.zhjypt.pojo.AnnouncementRead;
import com.example.zhjypt.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 公告表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Autowired
    private AnnouncementReadMapper announcementReadMapper;

    @Override
    public Page<Announcement> getAnnouncementPage(String title, Integer pageNum, Integer pageSize) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        
        if (StringUtils.hasText(title)) {
            queryWrapper.like("title", title);
        }
        
        queryWrapper.orderByDesc("publish_time");
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean publishAnnouncement(Announcement announcement) {
        announcement.setPublishTime(new Date());
        announcement.setStatus(1);
        announcement.setDel(0);
        return this.save(announcement);
    }

    @Override
    public List<Announcement> getUnreadAnnouncements(Integer userId, String userType) {
        return baseMapper.getUnreadAnnouncements(userId, userType);
    }

    @Override
    public boolean markAsRead(Integer announcementId, Integer userId, String userType) {
        // 检查是否已经标记为已读
        QueryWrapper<AnnouncementRead> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("announcement_id", announcementId)
                   .eq("user_id", userId)
                   .eq("user_type", userType);
        
        AnnouncementRead existingRead = announcementReadMapper.selectOne(queryWrapper);
        if (existingRead != null) {
            return true; // 已经标记过了
        }

        // 创建新的阅读记录
        AnnouncementRead announcementRead = new AnnouncementRead();
        announcementRead.setAnnouncementId(announcementId);
        announcementRead.setUserId(userId);
        announcementRead.setUserType(userType);
        announcementRead.setReadTime(new Date());
        
        return announcementReadMapper.insert(announcementRead) > 0;
    }

    @Override
    public Announcement getAnnouncementDetail(Integer announcementId) {
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("announcement_id", announcementId)
                   .eq("del", 0);
        return this.getOne(queryWrapper);
    }
}