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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 课程表
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course")
@ApiModel(value="Course对象", description="课程表")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "课程ID")
    @TableId(value = "course_id", type = IdType.AUTO)
    private Integer courseId;

    @ApiModelProperty(value = "课程标题")
    private String courseTitle;

    @ApiModelProperty(value = "课程描述")
    private String courseDesc;

    @ApiModelProperty(value = "课程封面图片URL")
    private String courseCover;

    @ApiModelProperty(value = "课程分类ID")
    private Integer categoryId;

    @ApiModelProperty(value = "授课教师ID")
    private Integer teacherId;

    @ApiModelProperty(value = "授课教师姓名")
    private String teacherName;

    @ApiModelProperty(value = "课程分类名称")
    private String categoryName;

    @ApiModelProperty(value = "课程价格")
    private BigDecimal coursePrice;

    @ApiModelProperty(value = "课程时长（分钟）")
    private Integer courseDuration;

    @ApiModelProperty(value = "学生数量")
    private Integer studentCount;

    @ApiModelProperty(value = "课程状态：0草稿，1发布，2下架")
    private Integer courseStatus;

    @ApiModelProperty(value = "是否免费，0收费，1免费")
    private Integer isFree;

    @ApiModelProperty(value = "难度等级：1初级，2中级，3高级")
    private Integer difficultyLevel;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    private Integer del;
}