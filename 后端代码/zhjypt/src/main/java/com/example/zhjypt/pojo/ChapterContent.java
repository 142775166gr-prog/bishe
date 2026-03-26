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
 * 章节内容表
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("chapter_content")
@ApiModel(value="ChapterContent对象", description="章节内容表")
public class ChapterContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "内容ID")
    @TableId(value = "content_id", type = IdType.AUTO)
    private Integer contentId;

    @ApiModelProperty(value = "章节ID")
    private Integer chapterId;

    @ApiModelProperty(value = "内容标题")
    private String contentTitle;

    @ApiModelProperty(value = "内容类型：1视频，2文档，3音频，4图片")
    private Integer contentType;

    @ApiModelProperty(value = "内容URL")
    private String contentUrl;

    @ApiModelProperty(value = "内容时长（秒）")
    private Integer duration;

    @ApiModelProperty(value = "文件大小（字节）")
    private Long fileSize;

    @ApiModelProperty(value = "内容描述")
    private String contentDesc;

    @ApiModelProperty(value = "排序顺序")
    private Integer sortOrder;

    @ApiModelProperty(value = "进度权重（用于计算课程总进度）")
    private Integer progressWeight = 1;

    @ApiModelProperty(value = "内容状态：0草稿，1发布")
    private Integer contentStatus;

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