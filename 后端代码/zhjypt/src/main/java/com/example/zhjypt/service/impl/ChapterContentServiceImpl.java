package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.ChapterContentMapper;
import com.example.zhjypt.pojo.ChapterContent;
import com.example.zhjypt.service.ChapterContentService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 章节内容表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Service
public class ChapterContentServiceImpl extends ServiceImpl<ChapterContentMapper, ChapterContent> implements ChapterContentService {

    @Override
    public List<ChapterContent> getContentsByChapterId(Integer chapterId) {
        QueryWrapper<ChapterContent> wrapper = new QueryWrapper<>();
        wrapper.eq("chapter_id", chapterId)
                .eq("del", 0)
                .orderByAsc("sort_order", "create_time");
        return this.list(wrapper);
    }

    @Override
    public boolean createContent(ChapterContent content) {
        // 设置默认值
        content.setContentStatus(0); // 草稿状态
        content.setCreateTime(new Date());
        content.setUpdateTime(new Date());
        content.setDel(0);
        
        // 如果没有设置排序，自动设置为最后
        if (content.getSortOrder() == null) {
            QueryWrapper<ChapterContent> wrapper = new QueryWrapper<>();
            wrapper.eq("chapter_id", content.getChapterId())
                    .eq("del", 0)
                    .orderByDesc("sort_order")
                    .last("LIMIT 1");
            ChapterContent lastContent = this.getOne(wrapper);
            content.setSortOrder(lastContent != null ? lastContent.getSortOrder() + 1 : 1);
        }
        
        return this.save(content);
    }

    @Override
    public boolean updateContent(ChapterContent content) {
        content.setUpdateTime(new Date());
        return this.updateById(content);
    }

    @Override
    public boolean deleteContent(Integer contentId, Integer chapterId) {
        // 使用MyBatis Plus的逻辑删除，直接调用removeById即可
        return this.removeById(contentId);
    }

    @Override
    public boolean updateContentSort(Integer contentId, Integer sortOrder) {
        ChapterContent content = new ChapterContent();
        content.setContentId(contentId);
        content.setSortOrder(sortOrder);
        content.setUpdateTime(new Date());
        return this.updateById(content);
    }

    @Override
    public boolean publishContent(Integer contentId) {
        ChapterContent content = new ChapterContent();
        content.setContentId(contentId);
        content.setContentStatus(1);
        content.setUpdateTime(new Date());
        return this.updateById(content);
    }

    @Override
    public boolean unpublishContent(Integer contentId) {
        ChapterContent content = new ChapterContent();
        content.setContentId(contentId);
        content.setContentStatus(0);
        content.setUpdateTime(new Date());
        return this.updateById(content);
    }
}