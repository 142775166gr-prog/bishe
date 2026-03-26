package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
import java.util.List;

/**
 * <p>
 * 题目表
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("question")
@ApiModel(value="Question对象", description="题目表")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "题目ID")
    @TableId(value = "question_id", type = IdType.AUTO)
    private Integer questionId;

    @ApiModelProperty(value = "课程ID")
    private Integer courseId;

    @ApiModelProperty(value = "题目类型：1单选，2多选，3判断，4填空，5简答")
    private Integer questionType;

    @ApiModelProperty(value = "题目内容")
    private String questionContent;

    @ApiModelProperty(value = "题目图片URL")
    private String questionImage;

    @ApiModelProperty(value = "正确答案（JSON格式）")
    private String correctAnswer;

    @ApiModelProperty(value = "题目解析")
    private String questionAnalysis;

    @ApiModelProperty(value = "难度等级：1简单，2中等，3困难")
    private Integer difficultyLevel;

    @ApiModelProperty(value = "题目分值")
    private Integer questionScore;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    @TableLogic
    private Integer del;

    @ApiModelProperty(value = "题目选项（选择题使用）")
    @TableField(exist = false)
    private List<QuestionOption> options;
}