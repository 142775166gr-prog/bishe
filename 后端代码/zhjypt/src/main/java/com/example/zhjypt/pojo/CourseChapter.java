package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 课程章节表
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course_chapter")
@ApiModel(value="CourseChapter对象", description="课程章节表")
public class CourseChapter implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "章节ID")
    @TableId(value = "chapter_id", type = IdType.AUTO)
    private Integer chapterId;

    @ApiModelProperty(value = "课程ID")
    private Integer courseId;

    @ApiModelProperty(value = "章节标题")
    private String chapterTitle;

    @ApiModelProperty(value = "章节描述")
    private String chapterDesc;

    @ApiModelProperty(value = "章节排序")
    private Integer sortOrder;

    @ApiModelProperty(value = "章节状态：0草稿，1发布")
    private Integer chapterStatus;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    @TableLogic
    private Integer del;
}