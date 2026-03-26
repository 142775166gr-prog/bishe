package com.example.zhjypt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.zhjypt.pojo.EducationSuggestion;
import com.example.zhjypt.service.EducationSuggestionService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 教育建议 前端控制器
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@RestController
@RequestMapping("/suggestion")
@Api(tags = "教育建议管理")
@CrossOrigin
public class EducationSuggestionController {

    @Autowired
    private EducationSuggestionService suggestionService;

    @ApiOperation("教师创建建议")
    @GetMapping("/create")
    public ResultVO createSuggestion(
            @RequestParam Integer teacherId,
            @RequestParam String teacherName,
            @RequestParam Integer studentId,
            @RequestParam String studentName,
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) String courseTitle,
            @RequestParam Integer suggestionType,
            @RequestParam String suggestionTitle,
            @RequestParam String suggestionContent) {
        try {
            EducationSuggestion suggestion = new EducationSuggestion();
            suggestion.setTeacherId(teacherId);
            suggestion.setTeacherName(teacherName);
            suggestion.setStudentId(studentId);
            suggestion.setStudentName(studentName);
            suggestion.setCourseId(courseId);
            suggestion.setCourseTitle(courseTitle);
            suggestion.setSuggestionType(suggestionType);
            suggestion.setSuggestionTitle(suggestionTitle);
            suggestion.setSuggestionContent(suggestionContent);
            
            boolean success = suggestionService.createSuggestion(suggestion);
            if (success) {
                return ResultVO.success("建议发送成功");
            } else {
                return ResultVO.fail("建议发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("建议发送失败");
        }
    }

    @ApiOperation("标记为已读")
    @GetMapping("/markread")
    public ResultVO markAsRead(
            @RequestParam Integer suggestionId,
            @RequestParam Integer studentId) {
        try {
            boolean success = suggestionService.markAsRead(suggestionId, studentId);
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

    @ApiOperation("收藏/取消收藏")
    @GetMapping("/togglefavorite")
    public ResultVO toggleFavorite(
            @RequestParam Integer suggestionId,
            @RequestParam Integer studentId) {
        try {
            boolean success = suggestionService.toggleFavorite(suggestionId, studentId);
            if (success) {
                return ResultVO.success("操作成功");
            } else {
                return ResultVO.fail("操作失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("操作失败");
        }
    }

    @ApiOperation("获取学生收到的建议列表")
    @GetMapping("/student/list")
    public ResultVO getStudentSuggestions(
            @RequestParam Integer studentId,
            @RequestParam(required = false) Integer suggestionType,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<EducationSuggestion> page = suggestionService.getStudentSuggestions(studentId, suggestionType, isRead, pageNum, pageSize);
            return ResultVO.success("查询成功", page.getRecords(), page.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取教师发送的建议列表")
    @GetMapping("/teacher/list")
    public ResultVO getTeacherSuggestions(
            @RequestParam Integer teacherId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<EducationSuggestion> page = suggestionService.getTeacherSuggestions(teacherId, pageNum, pageSize);
            return ResultVO.success("查询成功", page.getRecords(), page.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取建议详情")
    @GetMapping("/detail")
    public ResultVO getSuggestionDetail(@RequestParam Integer suggestionId) {
        try {
            EducationSuggestion suggestion = suggestionService.getSuggestionDetail(suggestionId);
            if (suggestion != null) {
                return ResultVO.success("查询成功", suggestion);
            } else {
                return ResultVO.fail("建议不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("删除建议")
    @GetMapping("/delete")
    public ResultVO deleteSuggestion(
            @RequestParam Integer suggestionId,
            @RequestParam Integer userId,
            @RequestParam String userType) {
        try {
            boolean success = suggestionService.deleteSuggestion(suggestionId, userId, userType);
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

    @ApiOperation("获取学生未读建议数量")
    @GetMapping("/student/unread/count")
    public ResultVO getUnreadCount(@RequestParam Integer studentId) {
        try {
            Integer count = suggestionService.getUnreadCountByStudent(studentId);
            return ResultVO.success("查询成功", count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }
}
