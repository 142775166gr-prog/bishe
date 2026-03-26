package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 学生内容学习进度表
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("student_content_progress")
@ApiModel(value="StudentContentProgress对象", description="学生内容学习进度表")
public class StudentContentProgress implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学生ID")
    private Integer studentId;

    @ApiModelProperty(value = "课程ID")
    private Integer courseId;

    @ApiModelProperty(value = "章节ID")
    private Integer chapterId;

    @ApiModelProperty(value = "内容ID")
    private Integer contentId;

    @ApiModelProperty(value = "是否完成：0未完成，1已完成")
    private Integer isCompleted = 0;

    @ApiModelProperty(value = "观看进度百分比（0-100）")
    private Integer watchProgress = 0;

    @ApiModelProperty(value = "首次学习时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date firstStudyTime;

    @ApiModelProperty(value = "完成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date completedTime;

    @ApiModelProperty(value = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}