package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.CourseCategory;
import com.example.zhjypt.service.CourseCategoryService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程分类表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@RestController
@RequestMapping("/course-category")
@Api(tags = "课程分类管理")
@CrossOrigin
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @ApiOperation("获取所有启用的分类")
    @GetMapping("/list")
    public ResultVO getActiveCategoriesList() {
        try {
            List<CourseCategory> categories = courseCategoryService.getActiveCategoriesList();
            return ResultVO.success("查询成功", categories);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("获取分类详情")
    @GetMapping("/detail")
    public ResultVO getCategoryDetail(@RequestParam Integer categoryId) {
        try {
            CourseCategory category = courseCategoryService.getById(categoryId);
            if (category != null && category.getDel() == 0) {
                return ResultVO.success("查询成功", category);
            } else {
                return ResultVO.fail("分类不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }
}