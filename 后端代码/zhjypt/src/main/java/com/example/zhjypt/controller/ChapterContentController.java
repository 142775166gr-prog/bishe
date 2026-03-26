package com.example.zhjypt.controller;

import com.example.zhjypt.pojo.ChapterContent;
import com.example.zhjypt.service.ChapterContentService;
import com.example.zhjypt.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 章节内容表 前端控制器
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@RestController
@RequestMapping("/chapter-content")
@Api(tags = "章节内容管理")
@CrossOrigin
public class ChapterContentController {

    @Autowired
    private ChapterContentService contentService;

    @ApiOperation("根据章节ID获取内容列表")
    @GetMapping("/list")
    public ResultVO getContentsByChapterId(@RequestParam Integer chapterId) {
        try {
            List<ChapterContent> contents = contentService.getContentsByChapterId(chapterId);
            return ResultVO.success("查询成功", contents);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVO.fail("查询失败");
        }
    }

    @ApiOperation("创建内容")
    @GetMapping("/create")
    public ResultVO createContent(
            @RequestParam Integer chapterId,
            @RequestParam String contentTitle,
            @RequestParam Integer contentType,
            @RequestParam String contentUrl,
            @RequestParam(required = false) String contentDesc,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Long fileSize,
            @RequestParam(defaultValue = "1") Integer progressWeight,
            @RequestParam(required = false) Integer sortOrder) {
        try {
            ChapterContent content = new ChapterContent();
            content.setChapterId(chapterId);
            content.setContentTitle(contentTitle);
            content.setContentType(contentType);
            content.setContentUrl(contentUrl);
            content.setContentDesc(contentDesc);
            content.setDuration(duration);
            content.setFileSize(fileSize);
            content.setProgressWeight(progressWeight);
            content.setSortOrder(sortOrder);
            
            boolean success = contentService.createContent(content);
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

    @ApiOperation("更新内容")
    @GetMapping("/update")
    public ResultVO updateContent(
            @RequestParam Integer contentId,
            @RequestParam String contentTitle,
            @RequestParam Integer contentType,
            @RequestParam String contentUrl,
            @RequestParam(required = false) String contentDesc,
            @RequestParam(required = false) Integer duration,
            @RequestParam(required = false) Long fileSize,
            @RequestParam(defaultValue = "1") Integer progressWeight,
            @RequestParam(required = false) Integer sortOrder) {
        try {
            ChapterContent content = new ChapterContent();
            content.setContentId(contentId);
            content.setContentTitle(contentTitle);
            content.setContentType(contentType);
            content.setContentUrl(contentUrl);
            content.setContentDesc(contentDesc);
            content.setDuration(duration);
            content.setFileSize(fileSize);
            content.setProgressWeight(progressWeight);
            if (sortOrder != null) {
                content.setSortOrder(sortOrder);
            }
            
            boolean success = contentService.updateContent(content);
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

    @ApiOperation("删除内容")
    @GetMapping("/delete")
    public ResultVO deleteContent(
            @RequestParam Integer contentId,
            @RequestParam Integer chapterId) {
        try {
            boolean success = contentService.deleteContent(contentId, chapterId);
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

    @ApiOperation("发布内容")
    @GetMapping("/publish")
    public ResultVO publishContent(@RequestParam Integer contentId) {
        try {
            boolean success = contentService.publishContent(contentId);
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

    @ApiOperation("取消发布内容")
    @GetMapping("/unpublish")
    public ResultVO unpublishContent(@RequestParam Integer contentId) {
        try {
            boolean success = contentService.unpublishContent(contentId);
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

    @ApiOperation("更新内容排序")
    @GetMapping("/sort")
    public ResultVO updateContentSort(
            @RequestParam Integer contentId,
            @RequestParam Integer sortOrder) {
        try {
            boolean success = contentService.updateContentSort(contentId, sortOrder);
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