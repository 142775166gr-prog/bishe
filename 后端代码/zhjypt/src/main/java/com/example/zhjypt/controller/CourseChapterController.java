package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.CourseChapter;
import com.example.zhjypt.service.CourseChapterService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程章节表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@RestController
@RequestMapping("/course-chapter")
@Api(tags = "课程章节管理")
@CrossOrigin
public class CourseChapterController {

    @Autowired
    private CourseChapterService chapterService;

    @ApiOperation("根据课程ID获取章节列表")
    @GetMapping("/list")
    public ResultVO getChaptersByCourseId(@RequestParam Integer courseId) {
        try {
            List<CourseChapter> chapters = chapterService.getChaptersByCourseId(courseId);
            return ResultVO.success("查询成功", chapters);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("创建章节")
    @GetMapping("/create")
    public ResultVO createChapter(
            @RequestParam Integer courseId,
            @RequestParam String chapterTitle,
            @RequestParam(required = false) String chapterDesc,
            @RequestParam(required = false) Integer sortOrder) {
        try {
            CourseChapter chapter = new CourseChapter();
            chapter.setCourseId(courseId);
            chapter.setChapterTitle(chapterTitle);
            chapter.setChapterDesc(chapterDesc);
            chapter.setSortOrder(sortOrder);
            
            boolean success = chapterService.createChapter(chapter);
            if (success) {
                return ResultVO.success("创建成功");
            } else {
                return ResultVO.fail("创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("创建失败");
        }
    }

    @ApiOperation("更新章节")
    @GetMapping("/update")
    public ResultVO updateChapter(
            @RequestParam Integer chapterId,
            @RequestParam String chapterTitle,
            @RequestParam(required = false) String chapterDesc,
            @RequestParam(required = false) Integer sortOrder) {
        try {
            CourseChapter chapter = new CourseChapter();
            chapter.setChapterId(chapterId);
            chapter.setChapterTitle(chapterTitle);
            chapter.setChapterDesc(chapterDesc);
            if (sortOrder != null) {
                chapter.setSortOrder(sortOrder);
            }
            
            boolean success = chapterService.updateChapter(chapter);
            if (success) {
                return ResultVO.success("更新成功");
            } else {
                return ResultVO.fail("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("更新失败");
        }
    }

    @ApiOperation("删除章节")
    @GetMapping("/delete")
    public ResultVO deleteChapter(
            @RequestParam Integer chapterId,
            @RequestParam Integer courseId) {
        try {
            boolean success = chapterService.deleteChapter(chapterId, courseId);
            if (success) {
                return ResultVO.success("删除成功");
            } else {
                return ResultVO.fail("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("删除失败");
        }
    }

    @ApiOperation("发布章节")
    @GetMapping("/publish")
    public ResultVO publishChapter(@RequestParam Integer chapterId) {
        try {
            boolean success = chapterService.publishChapter(chapterId);
            if (success) {
                return ResultVO.success("发布成功");
            } else {
                return ResultVO.fail("发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("发布失败");
        }
    }

    @ApiOperation("取消发布章节")
    @GetMapping("/unpublish")
    public ResultVO unpublishChapter(@RequestParam Integer chapterId) {
        try {
            boolean success = chapterService.unpublishChapter(chapterId);
            if (success) {
                return ResultVO.success("取消发布成功");
            } else {
                return ResultVO.fail("取消发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("取消发布失败");
        }
    }

    @ApiOperation("更新章节排序")
    @GetMapping("/sort")
    public ResultVO updateChapterSort(
            @RequestParam Integer chapterId,
            @RequestParam Integer sortOrder) {
        try {
            boolean success = chapterService.updateChapterSort(chapterId, sortOrder);
            if (success) {
                return ResultVO.success("排序更新成功");
            } else {
                return ResultVO.fail("排序更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("排序更新失败");
        }
    }
}