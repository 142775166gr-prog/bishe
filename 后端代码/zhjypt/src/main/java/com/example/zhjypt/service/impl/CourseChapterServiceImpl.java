package com.example.zhjypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.zhjypt.mapper.CourseChapterMapper;
import com.example.zhjypt.pojo.CourseChapter;
import com.example.zhjypt.service.CourseChapterService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 课程章节表 服务实现类
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter> implements CourseChapterService {

    @Override
    public List<CourseChapter> getChaptersByCourseId(Integer courseId) {
        QueryWrapper<CourseChapter> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId)
                .eq("del", 0)
                .orderByAsc("sort_order", "create_time");
        return this.list(wrapper);
    }

    @Override
    public boolean createChapter(CourseChapter chapter) {
        // 设置默认值
        chapter.setChapterStatus(0); // 草稿状态
        chapter.setCreateTime(new Date());
        chapter.setUpdateTime(new Date());
        chapter.setDel(0);
        
        // 如果没有设置排序，自动设置为最后
        if (chapter.getSortOrder() == null) {
            QueryWrapper<CourseChapter> wrapper = new QueryWrapper<>();
            wrapper.eq("course_id", chapter.getCourseId())
                    .eq("del", 0)
                    .orderByDesc("sort_order")
                    .last("LIMIT 1");
            CourseChapter lastChapter = this.getOne(wrapper);
            chapter.setSortOrder(lastChapter != null ? lastChapter.getSortOrder() + 1 : 1);
        }
        
        return this.save(chapter);
    }

    @Override
    public boolean updateChapter(CourseChapter chapter) {
        chapter.setUpdateTime(new Date());
        return this.updateById(chapter);
    }

    @Override
    public boolean deleteChapter(Integer chapterId, Integer courseId) {
        // 使用MyBatis Plus的逻辑删除，直接调用removeById即可
        return this.removeById(chapterId);
    }

    @Override
    public boolean updateChapterSort(Integer chapterId, Integer sortOrder) {
        CourseChapter chapter = new CourseChapter();
        chapter.setChapterId(chapterId);
        chapter.setSortOrder(sortOrder);
        chapter.setUpdateTime(new Date());
        return this.updateById(chapter);
    }

    @Override
    public boolean publishChapter(Integer chapterId) {
        CourseChapter chapter = new CourseChapter();
        chapter.setChapterId(chapterId);
        chapter.setChapterStatus(1);
        chapter.setUpdateTime(new Date());
        return this.updateById(chapter);
    }

    @Override
    public boolean unpublishChapter(Integer chapterId) {
        CourseChapter chapter = new CourseChapter();
        chapter.setChapterId(chapterId);
        chapter.setChapterStatus(0);
        chapter.setUpdateTime(new Date());
        return this.updateById(chapter);
    }
}