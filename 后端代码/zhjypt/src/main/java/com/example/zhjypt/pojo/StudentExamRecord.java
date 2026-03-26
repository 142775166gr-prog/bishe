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
 * 学生考试记录表
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("student_exam_record")
@ApiModel(value="StudentExamRecord对象", description="学生考试记录表")
public class StudentExamRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;

    @ApiModelProperty(value = "学生ID")
    private Integer studentId;

    @ApiModelProperty(value = "考试ID")
    private Integer examId;

    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime;

    @ApiModelProperty(value = "总分")
    private Integer totalScore;

    @ApiModelProperty(value = "学生得分")
    private BigDecimal studentScore;

    @ApiModelProperty(value = "通过状态：0未通过，1通过")
    private Integer passStatus;

    @ApiModelProperty(value = "考试状态：0进行中，1已提交，2已评分")
    private Integer examStatus;

    @ApiModelProperty(value = "考试次数")
    private Integer attemptNumber;

    @ApiModelProperty(value = "用时（分钟）")
    private Integer timeUsed;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}