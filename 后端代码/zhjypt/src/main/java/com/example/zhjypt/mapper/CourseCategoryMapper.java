package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.CourseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程分类表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * 获取启用的分类列表
     */
    @Select("SELECT * FROM course_category WHERE status = 1 AND del = 0 ORDER BY sort_order ASC")
    List<CourseCategory> getActiveCategoriesList();
}