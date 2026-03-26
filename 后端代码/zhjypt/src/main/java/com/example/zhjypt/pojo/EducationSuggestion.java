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
 * 教育建议表
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("education_suggestion")
@ApiModel(value="EducationSuggestion对象", description="教育建议表")
public class EducationSuggestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "建议ID")
    @TableId(value = "suggestion_id", type = IdType.AUTO)
    private Integer suggestionId;

    @ApiModelProperty(value = "教师ID")
    private Integer teacherId;

    @ApiModelProperty(value = "教师姓名")
    private String teacherName;

    @ApiModelProperty(value = "学生ID")
    private Integer studentId;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "课程ID")
    private Integer courseId;

    @ApiModelProperty(value = "课程标题")
    private String courseTitle;

    @ApiModelProperty(value = "建议类型：1学习方法，2课程推荐，3学习计划，4其他")
    private Integer suggestionType;

    @ApiModelProperty(value = "建议标题")
    private String suggestionTitle;

    @ApiModelProperty(value = "建议内容")
    private String suggestionContent;

    @ApiModelProperty(value = "是否已读：0未读，1已读")
    private Integer isRead;

    @ApiModelProperty(value = "是否收藏：0未收藏，1已收藏")
    private Integer isFavorite;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    @TableLogic
    private Integer del;
}
