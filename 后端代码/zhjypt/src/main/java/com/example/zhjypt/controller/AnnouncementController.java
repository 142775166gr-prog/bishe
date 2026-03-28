package com.example.zhjypt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.zhjypt.pojo.Announcement;
import com.example.zhjypt.service.AnnouncementService;
import com.example.zhjypt.vo.ResultVO;
import com.example.zhjypt.security.JwtContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 公告表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@RestController
@RequestMapping("/announcement")
@Api(tags = "公告管理")
@CrossOrigin
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @ApiOperation("分页查询公告列表")
    @GetMapping("/page")
    public ResultVO getAnnouncementPage(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Announcement> page = announcementService.getAnnouncementPage(title, pageNum, pageSize);
            return ResultVO.success("查询成功", page.getRecords(), page.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("发布公告")
    @GetMapping("/publish")
    public ResultVO publishAnnouncement(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Integer publisherId,
            @RequestParam String publisherName,
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            Announcement announcement = new Announcement();
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setPublisherId(publisherId);
            announcement.setPublisherName(publisherName);
            announcement.setStatus(status);
            
            boolean success = announcementService.publishAnnouncement(announcement);
            if (success) {
                return ResultVO.success("发布成功");
            } else {
                return ResultVO.fail("发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("发布失败");
        }
    }

    @ApiOperation("更新公告")
    @GetMapping("/update")
    public ResultVO updateAnnouncement(
            @RequestParam Integer announcementId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "1") Integer status) {
        try {
            Announcement announcement = new Announcement();
            announcement.setAnnouncementId(announcementId);
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setStatus(status);
            
            boolean success = announcementService.updateById(announcement);
            if (success) {
                return ResultVO.success("更新成功");
            } else {
                return ResultVO.fail("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("更新失败");
        }
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/delete/{id}")
    public ResultVO deleteAnnouncement(@PathVariable Integer id) {
        try {
            Announcement announcement = new Announcement();
            announcement.setAnnouncementId(id);
            announcement.setDel(1);
            boolean success = announcementService.updateById(announcement);
            if (success) {
                return ResultVO.success("删除成功");
            } else {
                return ResultVO.fail("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("删除失败");
        }
    }

    @ApiOperation("获取用户未读公告")
    @GetMapping("/unread")
    public ResultVO getUnreadAnnouncements(
            @RequestParam Integer userId,
            @RequestParam String userType) {
        try {
            // 越权止血：忽略前端传入的 userId/userType，仅使用 token 中身份
            if (JwtContext.getCurrentUser() == null) {
                return ResultVO.fail("未授权");
            }
            Integer uid = JwtContext.getCurrentUser().getUid();
            String role = JwtContext.getCurrentUser().getRole();
            List<Announcement> announcements = announcementService.getUnreadAnnouncements(uid, role);
            return ResultVO.success("查询成功", announcements);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("标记公告为已读")
    @GetMapping("/markRead")
    public ResultVO markAsRead(
            @RequestParam Integer announcementId,
            @RequestParam Integer userId,
            @RequestParam String userType) {
        try {
            // 越权止血：忽略前端传入的 userId/userType，仅使用 token 中身份
            if (JwtContext.getCurrentUser() == null) {
                return ResultVO.fail("未授权");
            }
            Integer uid = JwtContext.getCurrentUser().getUid();
            String role = JwtContext.getCurrentUser().getRole();
            boolean success = announcementService.markAsRead(announcementId, uid, role);
            if (success) {
                return ResultVO.success("标记成功");
            } else {
                return ResultVO.fail("标记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("标记失败");
        }
    }

    @ApiOperation("获取公告详情")
    @GetMapping("/detail/{id}")
    public ResultVO getAnnouncementDetail(@PathVariable Integer id) {
        try {
            Announcement announcement = announcementService.getAnnouncementDetail(id);
            if (announcement != null) {
                return ResultVO.success("查询成功", announcement);
            } else {
                return ResultVO.fail("公告不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }
}