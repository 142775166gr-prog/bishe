package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.CourseChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程章节表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Mapper
public interface CourseChapterMapper extends BaseMapper<CourseChapter> {

    /**
     * 根据课程ID获取章节列表
     */
    List<CourseChapter> getChaptersByCourseId(@Param("courseId") Integer courseId);

    /**
     * 获取课程章节数量
     */
    Integer getChapterCountByCourseId(@Param("courseId") Integer courseId);

    /**
     * 更新章节排序
     */
    int updateChapterSort(@Param("chapterId") Integer chapterId, @Param("sortOrder") Integer sortOrder);
}