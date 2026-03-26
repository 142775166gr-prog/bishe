package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.CourseComment;
import com.example.zhjypt.service.CourseCommentService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程评论 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/courseComment")
@Api(tags = "课程评论管理")
@CrossOrigin
public class CourseCommentController {

    @Autowired
    private CourseCommentService courseCommentService;

    @ApiOperation("获取课程评论列表")
    @GetMapping("/list/{courseId}")
    public ResultVO getCommentList(@PathVariable Integer courseId) {
        try {
            List<CourseComment> comments = courseCommentService.getCommentsByCourseId(courseId);
            Integer total = courseCommentService.getCommentCount(courseId);
            return ResultVO.success("查询成功", comments, total);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败: " + e.getMessage());
        }
    }

    @ApiOperation("发表评论")
    @PostMapping("/add")
    public ResultVO addComment(@RequestBody CourseComment comment) {
        try {
            boolean success = courseCommentService.addComment(comment);
            if (success) {
                return ResultVO.success("评论成功");
            } else {
                return ResultVO.fail("评论失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("评论失败: " + e.getMessage());
        }
    }

    @ApiOperation("回复评论")
    @PostMapping("/reply")
    public ResultVO replyComment(@RequestBody CourseComment comment) {
        try {
            boolean success = courseCommentService.replyComment(comment);
            if (success) {
                return ResultVO.success("回复成功");
            } else {
                return ResultVO.fail("回复失败，原评论不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("回复失败: " + e.getMessage());
        }
    }

    @ApiOperation("删除自己的评论")
    @DeleteMapping("/delete")
    public ResultVO deleteComment(
            @RequestParam Integer commentId,
            @RequestParam Integer userId,
            @RequestParam String userType) {
        try {
            boolean success = courseCommentService.deleteComment(commentId, userId, userType);
            if (success) {
                return ResultVO.success("删除成功");
            } else {
                return ResultVO.fail("删除失败，您无权删除此评论");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("删除失败: " + e.getMessage());
        }
    }

    @ApiOperation("管理员删除评论")
    @DeleteMapping("/adminDelete/{commentId}")
    public ResultVO adminDeleteComment(@PathVariable Integer commentId) {
        try {
            boolean success = courseCommentService.adminDeleteComment(commentId);
            if (success) {
                return ResultVO.success("删除成功");
            } else {
                return ResultVO.fail("删除失败，评论不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("删除失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取课程评论数量")
    @GetMapping("/count/{courseId}")
    public ResultVO getCommentCount(@PathVariable Integer courseId) {
        try {
            Integer count = courseCommentService.getCommentCount(courseId);
            return ResultVO.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取用户未读回复数量")
    @GetMapping("/unreadCount")
    public ResultVO getUnreadReplyCount(@RequestParam Integer userId, @RequestParam String userType) {
        try {
            Integer count = courseCommentService.getUnreadReplyCount(userId, userType);
            return ResultVO.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取用户未读回复列表")
    @GetMapping("/unreadList")
    public ResultVO getUnreadReplies(@RequestParam Integer userId, @RequestParam String userType) {
        try {
            java.util.List<CourseComment> replies = courseCommentService.getUnreadReplies(userId, userType);
            return ResultVO.success("查询成功", replies, replies.size());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败: " + e.getMessage());
        }
    }

    @ApiOperation("标记回复为已读")
    @GetMapping("/markRead")
    public ResultVO markAsRead(@RequestParam Integer commentId, @RequestParam Integer userId, @RequestParam String userType) {
        try {
            boolean success = courseCommentService.markAsRead(commentId, userId, userType);
            if (success) {
                return ResultVO.success("标记成功");
            } else {
                return ResultVO.fail("标记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("标记失败: " + e.getMessage());
        }
    }

    @ApiOperation("标记所有回复为已读")
    @GetMapping("/markAllRead")
    public ResultVO markAllAsRead(@RequestParam Integer userId, @RequestParam String userType) {
        try {
            boolean success = courseCommentService.markAllAsRead(userId, userType);
            if (success) {
                return ResultVO.success("标记成功");
            } else {
                return ResultVO.fail("标记失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("标记失败: " + e.getMessage());
        }
    }
}
