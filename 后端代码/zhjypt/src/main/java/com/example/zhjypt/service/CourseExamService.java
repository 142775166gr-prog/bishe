package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.example.zhjypt.pojo.Admin;
import com.example.zhjypt.pojo.CourseExam;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.vo.ResultVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
public interface CourseExamService extends IService<CourseExam> {
    public ResultVO<CourseExam> AddCourseExam(CourseExam courseExam);
    public ResultVO<CourseExam> UpdateCourseExam(CourseExam courseExam);
    public ResultVO<CourseExam> DeleteCourseExam(CourseExam courseExam);
    public ResultVO<CourseExam> GetCourseExam(CourseExam courseExam);
    public ResultVO<List> GetCourseExamList(CourseExam courseExam);
    public ResultVO<List> GetCourseExamListByPage(CourseExam courseExam, int pageNum, int pageSize);
}
