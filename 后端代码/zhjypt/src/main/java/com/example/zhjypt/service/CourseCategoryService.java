package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
public interface CourseCategoryService extends IService<CourseCategory> {

    /**
     * 获取启用的分类列表
     */
    List<CourseCategory> getActiveCategoriesList();
}