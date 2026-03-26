package com.example.zhjypt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.zhjypt.pojo.ChapterContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 章节内容表 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Mapper
public interface ChapterContentMapper extends BaseMapper<ChapterContent> {

    /**
     * 根据章节ID获取内容列表
     */
    List<ChapterContent> getContentsByChapterId(@Param("chapterId") Integer chapterId);

    /**
     * 获取章节内容数量
     */
    Integer getContentCountByChapterId(@Param("chapterId") Integer chapterId);

    /**
     * 更新内容排序
     */
    int updateContentSort(@Param("contentId") Integer contentId, @Param("sortOrder") Integer sortOrder);
}