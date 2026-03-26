package com.example.zhjypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.zhjypt.pojo.ChapterContent;

import java.util.List;

/**
 * <p>
 * 章节内容表 服务类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
public interface ChapterContentService extends IService<ChapterContent> {

    /**
     * 根据章节ID获取内容列表
     */
    List<ChapterContent> getContentsByChapterId(Integer chapterId);

    /**
     * 创建内容
     */
    boolean createContent(ChapterContent content);

    /**
     * 更新内容
     */
    boolean updateContent(ChapterContent content);

    /**
     * 删除内容
     */
    boolean deleteContent(Integer contentId, Integer chapterId);

    /**
     * 更新内容排序
     */
    boolean updateContentSort(Integer contentId, Integer sortOrder);

    /**
     * 发布内容
     */
    boolean publishContent(Integer contentId);

    /**
     * 取消发布内容
     */
    boolean unpublishContent(Integer contentId);
}