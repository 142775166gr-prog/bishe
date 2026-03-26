package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.CourseCategoryMapper;
import com.example.zhjypt.pojo.CourseCategory;
import com.example.zhjypt.service.CourseCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程分类表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

    @Override
    public List<CourseCategory> getActiveCategoriesList() {
        return baseMapper.getActiveCategoriesList();
    }
}