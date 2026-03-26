package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.zhjypt.pojo.Course;
import com.example.zhjypt.pojo.CourseExam;
import com.example.zhjypt.mapper.CourseExamMapper;
import com.example.zhjypt.service.CourseExamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
@Service
public class CourseExamServiceImpl extends ServiceImpl<CourseExamMapper, CourseExam> implements CourseExamService {
    @Autowired
    private CourseExamMapper courseExamMapper;

    @Override
    public ResultVO<CourseExam> AddCourseExam(CourseExam courseExam) {
        this.save(courseExam);
        return ResultVO.success("添加成功", courseExam);
    }

    @Override
    public ResultVO<CourseExam> UpdateCourseExam(CourseExam courseExam) {
        // 1. 校验主键是否存在（必须传入course_exam_id才能定位要修改的记录）
        if (courseExam.getCourseExamId() == null) {
            return ResultVO.fail("更新失败：必须传入course_exam_id");
        }

        UpdateWrapper<CourseExam> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("course_exam_id", courseExam.getCourseExamId()); // 定位要修改的记录

        // 2. 执行更新（CourseExam表无其他业务字段，直接更新）
        boolean isUpdate = this.update(courseExam, updateWrapper);
        if (isUpdate) {
            // 更新成功后查询最新数据返回
            CourseExam updatedCourseExam = this.getById(courseExam.getCourseExamId());
            return ResultVO.success("更新成功", updatedCourseExam);
        } else {
            return ResultVO.fail("更新失败：无匹配记录或未传入任何修改字段");
        }
    }

    @Override
    public ResultVO<CourseExam> DeleteCourseExam(CourseExam courseExam) {
        if (courseExam.getCourseExamId() == null) {
            return ResultVO.fail("逻辑删除失败：必须传入course_exam_id");
        }
        CourseExam delCourseExam = this.getById(courseExam.getCourseExamId());
        // 调用MyBatis-Plus的removeById方法，会自动执行逻辑删除（无需手动构建UpdateWrapper）
        boolean isLogicDelete = this.removeById(courseExam.getCourseExamId());
        if (isLogicDelete) {
            return ResultVO.success("逻辑删除成功", delCourseExam);
        } else {
            return ResultVO.fail("逻辑删除失败：无匹配记录或该记录已被删除");
        }
    }

    @Override
    public ResultVO<CourseExam> GetCourseExam(CourseExam courseExam) {
        QueryWrapper<CourseExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        if (courseExam.getCourseExamId() != null) {
            queryWrapper.eq("course_exam_id", courseExam.getCourseExamId());
        }
        CourseExam currentCourseExam = this.getOne(queryWrapper);
        if (currentCourseExam != null) {
            return ResultVO.success("查询成功", currentCourseExam);
        } else {
            return ResultVO.fail("无数据");
        }
    }

    @Override
    public ResultVO<List> GetCourseExamList(CourseExam courseExam) {
        QueryWrapper<CourseExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        List<CourseExam> courseExamList = this.list(queryWrapper);
        if(courseExamList==null||courseExamList.isEmpty()){
            return ResultVO.fail("无数据");
        }else{
            return ResultVO.success("查询成功", courseExamList);
        }
    }

    @Override
    public ResultVO<List> GetCourseExamListByPage(CourseExam courseExam, int pageNum, int pageSize) {
        Page<CourseExam> page = new Page<>(pageNum, pageSize);
        QueryWrapper<CourseExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del", 0);
        Page<CourseExam> courseExamPage = courseExamMapper.selectPage(page, queryWrapper);
        List<CourseExam> courseExamList = courseExamPage.getRecords();
        long total = courseExamPage.getTotal();
        if(courseExamList==null||courseExamList.isEmpty()){
            return ResultVO.fail("无数据");
        }else{
            return ResultVO.success("查询成功", courseExamList,total);
        }
    }
}
