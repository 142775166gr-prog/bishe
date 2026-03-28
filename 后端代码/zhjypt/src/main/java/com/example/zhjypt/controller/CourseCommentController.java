package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.CourseComment;
import com.example.zhjypt.service.CourseCommentService;
import com.example.zhjypt.vo.ResultVO;
import com.example.zhjypt.security.JwtContext;
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

    private boolean isLoginStudentOrTeacher() {
        return JwtContext.getCurrentUser() != null
                && ("student".equals(JwtContext.getCurrentUser().getRole())
                || "teacher".equals(JwtContext.getCurrentUser().getRole()));
    }

    private Integer currentUid() {
        return JwtContext.getCurrentUser() == null ? null : JwtContext.getCurrentUser().getUid();
    }

    private String currentRole() {
        return JwtContext.getCurrentUser() == null ? null : JwtContext.getCurrentUser().getRole();
    }

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
            // 越权止血：强制使用 token 身份覆盖前端传入 userId/userType
            if (!isLoginStudentOrTeacher()) {
                return ResultVO.fail("未授权");
            }
            comment.setUserId(currentUid());
            comment.setUserType(currentRole());
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
            // 越权止血：强制使用 token 身份覆盖前端传入 userId/userType
            if (!isLoginStudentOrTeacher()) {
                return ResultVO.fail("未授权");
            }
            comment.setUserId(currentUid());
            comment.setUserType(currentRole());
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
            if (!isLoginStudentOrTeacher()) {
                return ResultVO.fail("未授权");
            }
            // 越权止血：忽略前端传入的 userId/userType
            boolean success = courseCommentService.deleteComment(commentId, currentUid(), currentRole());
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
            if (!isLoginStudentOrTeacher()) {
                return ResultVO.fail("未授权");
            }
            // 越权止血：忽略前端传入的 userId/userType
            Integer count = courseCommentService.getUnreadReplyCount(currentUid(), currentRole());
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
            if (!isLoginStudentOrTeacher()) {
                return ResultVO.fail("未授权");
            }
            // 越权止血：忽略前端传入的 userId/userType
            java.util.List<CourseComment> replies = courseCommentService.getUnreadReplies(currentUid(), currentRole());
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
            if (!isLoginStudentOrTeacher()) {
                return ResultVO.fail("未授权");
            }
            // 越权止血：忽略前端传入的 userId/userType
            boolean success = courseCommentService.markAsRead(commentId, currentUid(), currentRole());
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
            if (!isLoginStudentOrTeacher()) {
                return ResultVO.fail("未授权");
            }
            // 越权止血：忽略前端传入的 userId/userType
            boolean success = courseCommentService.markAllAsRead(currentUid(), currentRole());
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
