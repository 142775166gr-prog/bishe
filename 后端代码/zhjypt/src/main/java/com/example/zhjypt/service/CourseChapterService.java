package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.CourseChapter;

import java.util.List;

/**
 * <p>
 * 课程章节表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
public interface CourseChapterService extends IService<CourseChapter> {

    /**
     * 根据课程ID获取章节列表
     */
    List<CourseChapter> getChaptersByCourseId(Integer courseId);

    /**
     * 创建章节
     */
    boolean createChapter(CourseChapter chapter);

    /**
     * 更新章节
     */
    boolean updateChapter(CourseChapter chapter);

    /**
     * 删除章节
     */
    boolean deleteChapter(Integer chapterId, Integer courseId);

    /**
     * 更新章节排序
     */
    boolean updateChapterSort(Integer chapterId, Integer sortOrder);

    /**
     * 发布章节
     */
    boolean publishChapter(Integer chapterId);

    /**
     * 取消发布章节
     */
    boolean unpublishChapter(Integer chapterId);
}