package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 学生选课表
 * </p>
 *
 * @author system
 * @since 2025-12-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("student_course")
@ApiModel(value="StudentCourse对象", description="学生选课表")
public class StudentCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学生ID")
    private Integer studentId;

    @ApiModelProperty(value = "课程ID")
    private Integer courseId;

    @ApiModelProperty(value = "选课时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date enrollTime;

    @ApiModelProperty(value = "学习进度百分比")
    @TableField(value = "progress")
    private BigDecimal progress = new BigDecimal("0.00");

    @ApiModelProperty(value = "最后学习时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastStudyTime;

    @ApiModelProperty(value = "状态：0退课，1正常")
    @TableField(value = "status")
    private Integer status = 1; // 默认为正常状态

    // 关联查询字段
    @ApiModelProperty(value = "课程标题")
    @TableField(exist = false)
    private String courseTitle;

    @ApiModelProperty(value = "课程描述")
    @TableField(exist = false)
    private String courseDesc;

    @ApiModelProperty(value = "课程封面")
    @TableField(exist = false)
    private String courseCover;

    @ApiModelProperty(value = "教师姓名")
    @TableField(exist = false)
    private String teacherName;

    @ApiModelProperty(value = "分类名称")
    @TableField(exist = false)
    private String categoryName;

    @ApiModelProperty(value = "课程价格")
    @TableField(exist = false)
    private BigDecimal coursePrice;

    @ApiModelProperty(value = "是否免费")
    @TableField(exist = false)
    private Integer isFree;

    @ApiModelProperty(value = "难度等级")
    @TableField(exist = false)
    private Integer difficultyLevel;

    @ApiModelProperty(value = "学生学号")
    @TableField(exist = false)
    private String stuNum;

    @ApiModelProperty(value = "学生姓名")
    @TableField(exist = false)
    private String stuName;

    @ApiModelProperty(value = "学生ID（用于详情查询）")
    @TableField(exist = false)
    private Integer stuId;

    @ApiModelProperty(value = "总章节数")
    @TableField(exist = false)
    private Integer totalChapters;

    @ApiModelProperty(value = "已完成章节数")
    @TableField(exist = false)
    private Integer completedChapters;

    @ApiModelProperty(value = "考试总数")
    @TableField(exist = false)
    private Integer examCount;

    @ApiModelProperty(value = "已完成考试数")
    @TableField(exist = false)
    private Integer completedExams;

    @ApiModelProperty(value = "平均考试分数")
    @TableField(exist = false)
    private Double avgScore;
}