package com.example.zhjypt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.zhjypt.pojo.StudentContentProgress;
import com.example.zhjypt.service.StudentContentProgressService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 学生内容学习进度表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@RestController
@RequestMapping("/student-content-progress")
@Api(tags = "学生内容学习进度管理")
@CrossOrigin
public class StudentContentProgressController {

    @Autowired
    private StudentContentProgressService studentContentProgressService;

    @ApiOperation("更新学习进度")
    @PostMapping("/update")
    public ResultVO updateProgress(
            @RequestParam Integer studentId,
            @RequestParam Integer courseId,
            @RequestParam Integer chapterId,
            @RequestParam Integer contentId,
            @RequestParam Integer watchProgress) {
        try {
            boolean success = studentContentProgressService.updateProgress(
                studentId, courseId, chapterId, contentId, watchProgress);
            
            if (success) {
                return ResultVO.success("进度更新成功");
            } else {
                return ResultVO.fail("进度更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("进度更新失败：" + e.getMessage());
        }
    }

    @ApiOperation("标记内容为已完成")
    @PostMapping("/complete")
    public ResultVO markCompleted(
            @RequestParam Integer studentId,
            @RequestParam Integer courseId,
            @RequestParam Integer chapterId,
            @RequestParam Integer contentId) {
        try {
            boolean success = studentContentProgressService.markCompleted(
                studentId, courseId, chapterId, contentId);
            
            if (success) {
                return ResultVO.success("标记完成成功");
            } else {
                return ResultVO.fail("标记完成失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("标记完成失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取课程学习进度")
    @GetMapping("/course-progress")
    public ResultVO getCourseProgress(
            @RequestParam Integer studentId,
            @RequestParam Integer courseId) {
        try {
            BigDecimal progress = studentContentProgressService.calculateCourseProgress(studentId, courseId);
            Map<String, Object> stats = studentContentProgressService.getCourseProgressStats(studentId, courseId);
            
            stats.put("progress", progress);
            
            return ResultVO.success("查询成功", stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取学生的具体内容学习进度")
    @GetMapping("/content-progress")
    public ResultVO getContentProgress(
            @RequestParam Integer studentId,
            @RequestParam Integer contentId) {
        try {
            QueryWrapper<StudentContentProgress> wrapper = new QueryWrapper<>();
            wrapper.eq("student_id", studentId)
                    .eq("content_id", contentId);
            
            StudentContentProgress progress = studentContentProgressService.getOne(wrapper);
            
            if (progress != null) {
                return ResultVO.success("查询成功", progress);
            } else {
                // 返回默认进度
                StudentContentProgress defaultProgress = new StudentContentProgress();
                defaultProgress.setStudentId(studentId);
                defaultProgress.setContentId(contentId);
                defaultProgress.setIsCompleted(0);
                defaultProgress.setWatchProgress(0);
                return ResultVO.success("查询成功", defaultProgress);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation("检查内容是否已完成")
    @GetMapping("/check-completed")
    public ResultVO checkCompleted(
            @RequestParam Integer studentId,
            @RequestParam Integer contentId) {
        try {
            boolean completed = studentContentProgressService.isContentCompleted(studentId, contentId);
            return ResultVO.success("查询成功", completed);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败：" + e.getMessage());
        }
    }
}