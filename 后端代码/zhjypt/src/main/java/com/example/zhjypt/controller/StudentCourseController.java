package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.StudentCourse;
import com.example.zhjypt.service.StudentCourseService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 学生选课表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@RestController
@RequestMapping("/student-course")
@Api(tags = "学生选课管理")
@CrossOrigin
public class StudentCourseController {

    @Autowired
    private StudentCourseService studentCourseService;

    @ApiOperation("学生选课")
    @GetMapping("/enroll")
    public ResultVO enrollCourse(
            @RequestParam Integer studentId,
            @RequestParam Integer courseId) {
        try {
            // 检查是否已经选过课
            boolean isEnrolled = studentCourseService.checkEnrolled(studentId, courseId);
            if (isEnrolled) {
                return ResultVO.fail("您已经选过这门课程了");
            }
            
            boolean success = studentCourseService.enrollCourse(studentId, courseId);
            if (success) {
                return ResultVO.success("选课成功");
            } else {
                return ResultVO.fail("选课失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("选课失败");
        }
    }

    @ApiOperation("检查是否已选课")
    @GetMapping("/check")
    public ResultVO checkEnrolled(
            @RequestParam Integer studentId,
            @RequestParam Integer courseId) {
        try {
            boolean isEnrolled = studentCourseService.checkEnrolled(studentId, courseId);
            return ResultVO.success("查询成功", isEnrolled);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取学生的课程列表")
    @GetMapping("/list")
    public ResultVO getStudentCourses(@RequestParam Integer studentId) {
        try {
            List<StudentCourse> courses = studentCourseService.getStudentCourses(studentId);
            return ResultVO.success("查询成功", courses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("退课")
    @GetMapping("/withdraw")
    public ResultVO withdrawCourse(
            @RequestParam Integer studentId,
            @RequestParam Integer courseId) {
        try {
            boolean success = studentCourseService.withdrawCourse(studentId, courseId);
            if (success) {
                return ResultVO.success("退课成功");
            } else {
                return ResultVO.fail("退课失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("退课失败");
        }
    }

    @ApiOperation("更新学习进度")
    @GetMapping("/progress")
    public ResultVO updateProgress(
            @RequestParam Integer studentId,
            @RequestParam Integer courseId,
            @RequestParam BigDecimal progress) {
        try {
            boolean success = studentCourseService.updateProgress(studentId, courseId, progress);
            if (success) {
                return ResultVO.success("进度更新成功");
            } else {
                return ResultVO.fail("进度更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("进度更新失败");
        }
    }

    @ApiOperation("获取课程的选课学生列表（教师用）")
    @GetMapping("/courseStudents")
    public ResultVO getCourseStudents(@RequestParam Integer courseId) {
        try {
            List courses = studentCourseService.getCourseStudents(courseId);
            return ResultVO.success("查询成功", courses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取学生的章节学习详情")
    @GetMapping("/chapterProgress")
    public ResultVO getStudentChapterProgress(
            @RequestParam Integer studentId,
            @RequestParam Integer courseId) {
        try {
            List progress = studentCourseService.getStudentChapterProgress(studentId, courseId);
            return ResultVO.success("查询成功", progress);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }
}
