package com.example.zhjypt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.zhjypt.pojo.Course;
import com.example.zhjypt.service.CourseService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 课程表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@RestController
@RequestMapping("/course")
@Api(tags = "课程管理")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    @ApiOperation("分页查询课程列表（管理员）")
    @GetMapping("/page")
    public ResultVO getCoursePage(
            @RequestParam(required = false) String courseTitle,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer courseStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Course> page = courseService.getCoursePage(courseTitle, categoryId, courseStatus, pageNum, pageSize);
            return ResultVO.success("查询成功", page.getRecords(), page.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("分页查询教师课程列表")
    @GetMapping("/teacher/page")
    public ResultVO getTeacherCoursePage(
            @RequestParam Integer teacherId,
            @RequestParam(required = false) String courseTitle,
            @RequestParam(required = false) Integer courseStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Course> page = courseService.getTeacherCoursePage(teacherId, courseTitle, courseStatus, pageNum, pageSize);
            return ResultVO.success("查询成功", page.getRecords(), page.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("分页查询已发布课程列表（学生）")
    @GetMapping("/published/page")
    public ResultVO getPublishedCoursePage(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String courseTitle,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Course> page = courseService.getPublishedCoursePage(categoryId, courseTitle, pageNum, pageSize);
            return ResultVO.success("查询成功", page.getRecords(), page.getTotal());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("创建课程")
    @GetMapping("/create")
    public ResultVO createCourse(
            @RequestParam String courseTitle,
            @RequestParam String courseDesc,
            @RequestParam Integer categoryId,
            @RequestParam Integer teacherId,
            @RequestParam(defaultValue = "0.00") BigDecimal coursePrice,
            @RequestParam(defaultValue = "1") Integer isFree,
            @RequestParam(defaultValue = "1") Integer difficultyLevel,
            @RequestParam(required = false) String courseCover) {
        try {
            Course course = new Course();
            course.setCourseTitle(courseTitle);
            course.setCourseDesc(courseDesc);
            course.setCategoryId(categoryId);
            course.setTeacherId(teacherId);
            course.setCoursePrice(coursePrice);
            course.setIsFree(isFree);
            course.setDifficultyLevel(difficultyLevel);
            course.setCourseCover(courseCover);
            
            boolean success = courseService.createCourse(course);
            if (success) {
                return ResultVO.success("创建成功");
            } else {
                return ResultVO.fail("创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("创建失败");
        }
    }

    @ApiOperation("更新课程")
    @GetMapping("/update")
    public ResultVO updateCourse(
            @RequestParam Integer courseId,
            @RequestParam String courseTitle,
            @RequestParam String courseDesc,
            @RequestParam Integer categoryId,
            @RequestParam(defaultValue = "0.00") BigDecimal coursePrice,
            @RequestParam(defaultValue = "1") Integer isFree,
            @RequestParam(defaultValue = "1") Integer difficultyLevel,
            @RequestParam(required = false) String courseCover) {
        try {
            Course course = new Course();
            course.setCourseId(courseId);
            course.setCourseTitle(courseTitle);
            course.setCourseDesc(courseDesc);
            course.setCategoryId(categoryId);
            course.setCoursePrice(coursePrice);
            course.setIsFree(isFree);
            course.setDifficultyLevel(difficultyLevel);
            course.setCourseCover(courseCover);
            
            boolean success = courseService.updateCourse(course);
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

    @ApiOperation("发布课程")
    @GetMapping("/publish")
    public ResultVO publishCourse(
            @RequestParam Integer courseId,
            @RequestParam Integer teacherId) {
        try {
            boolean success = courseService.publishCourse(courseId, teacherId);
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

    @ApiOperation("下架课程")
    @GetMapping("/unpublish")
    public ResultVO unpublishCourse(
            @RequestParam Integer courseId,
            @RequestParam Integer teacherId) {
        try {
            boolean success = courseService.unpublishCourse(courseId, teacherId);
            if (success) {
                return ResultVO.success("下架成功");
            } else {
                return ResultVO.fail("下架失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("下架失败");
        }
    }

    @ApiOperation("删除课程")
    @GetMapping("/delete")
    public ResultVO deleteCourse(
            @RequestParam Integer courseId,
            @RequestParam Integer teacherId) {
        try {
            boolean success = courseService.deleteCourse(courseId, teacherId);
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

    @ApiOperation("获取课程详情")
    @GetMapping("/detail")
    public ResultVO getCourseDetail(@RequestParam Integer courseId) {
        try {
            Course course = courseService.getCourseDetail(courseId);
            if (course != null) {
                return ResultVO.success("查询成功", course);
            } else {
                return ResultVO.fail("课程不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("根据ID获取课程信息")
    @GetMapping("/getbyid")
    public ResultVO getCourseById(@RequestParam Integer courseId) {
        try {
            Course course = courseService.getCourseById(courseId);
            if (course != null) {
                return ResultVO.success("查询成功", course);
            } else {
                return ResultVO.fail("课程不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取教师课程列表")
    @GetMapping("/teacher/list")
    public ResultVO getTeacherCoursesList(@RequestParam Integer teacherId) {
        try {
            List<Course> courses = courseService.getTeacherCoursesList(teacherId);
            return ResultVO.success("查询成功", courses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取已发布课程列表")
    @GetMapping("/published/list")
    public ResultVO getPublishedCoursesList() {
        try {
            List<Course> courses = courseService.getPublishedCoursesList();
            return ResultVO.success("查询成功", courses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("根据分类获取课程")
    @GetMapping("/category")
    public ResultVO getCoursesByCategoryId(@RequestParam Integer categoryId) {
        try {
            List<Course> courses = courseService.getCoursesByCategoryId(categoryId);
            return ResultVO.success("查询成功", courses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取所有课程列表（管理员）")
    @GetMapping("/all")
    public ResultVO getAllCourses() {
        try {
            List<Course> courses = courseService.list();
            return ResultVO.success("查询成功", courses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }
}