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
 * 考试表
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("exam")
@ApiModel(value="Exam对象", description="考试表")
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "考试ID")
    @TableId(value = "exam_id", type = IdType.AUTO)
    private Integer examId;

    @ApiModelProperty(value = "课程ID")
    private Integer courseId;

    @ApiModelProperty(value = "考试标题")
    private String examTitle;

    @ApiModelProperty(value = "考试描述")
    private String examDesc;

    @ApiModelProperty(value = "考试类型：1练习，2考试")
    private Integer examType;

    @ApiModelProperty(value = "总分")
    private Integer totalScore;

    @ApiModelProperty(value = "及格分数")
    private Integer passScore;

    @ApiModelProperty(value = "考试时长（分钟），NULL表示不限时")
    private Integer timeLimit;

    @ApiModelProperty(value = "允许考试次数，NULL表示不限次数")
    private Integer attemptLimit;

    @ApiModelProperty(value = "开始时间，NULL表示随时可考")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "结束时间，NULL表示无截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "是否显示结果：0否，1是")
    private Integer showResult;

    @ApiModelProperty(value = "是否显示答案：0否，1是")
    private Integer showAnswer;

    @ApiModelProperty(value = "状态：0草稿，1发布")
    private Integer examStatus;

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