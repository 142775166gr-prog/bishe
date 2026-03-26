package com.example.zhjypt.controller;


import com.example.zhjypt.pojo.CourseExam;
import com.example.zhjypt.service.CourseExamService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
@RestController
@RequestMapping("/Course-exam")
@CrossOrigin
public class CourseExamController {
    @Autowired
    private CourseExamService courseExamService;

    @GetMapping("/AddCourseExam")
    @ApiOperation(value = "添加课程习题试卷",notes = "添加课程习题试卷")
    public ResultVO<CourseExam> AddCourseExam(CourseExam courseExam){
        return courseExamService.AddCourseExam(courseExam);
    }
    @GetMapping("/UpdateCourseExam")
    @ApiOperation(value = "修改课程习题试卷",notes = "修改课程习题试卷")
    public ResultVO<CourseExam> UpdateCourseExam(CourseExam courseExam){
        return courseExamService.UpdateCourseExam(courseExam);
    }
    @GetMapping("/DeleteCourseExam")
    @ApiOperation(value = "删除课程习题试卷",notes = "删除课程习题试卷")
    public ResultVO<CourseExam> DeleteCourseExam(CourseExam courseExam){
        return courseExamService.DeleteCourseExam(courseExam);
    }
    @GetMapping("/GetCourseExam")
    @ApiOperation(value = "获取课程习题试卷",notes = "获取课程习题试卷")
    public ResultVO<CourseExam> GetCourseExam(CourseExam courseExam){
        return courseExamService.GetCourseExam(courseExam);
    }
    @GetMapping("/GetCourseExamList")
    @ApiOperation(value = "获取课程习题试卷列表",notes = "获取课程习题试卷列表")
    public ResultVO<List> GetCourseExamList(CourseExam courseExam){
        return courseExamService.GetCourseExamList(courseExam);
    }
    @GetMapping("/GetCourseExamListByPage")
    @ApiOperation(value = "分页获取课程习题试卷列表",notes = "分页获取课程习题试卷列表")
    public ResultVO<List> GetCourseExamListByPage(CourseExam courseExam,int pageNum,int pageSize){
        return courseExamService.GetCourseExamListByPage(courseExam,pageNum,pageSize);
    }
}
