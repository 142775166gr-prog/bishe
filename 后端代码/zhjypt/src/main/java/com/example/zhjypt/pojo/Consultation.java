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
 * 在线咨询表
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("consultation")
@ApiModel(value="Consultation对象", description="在线咨询表")
public class Consultation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "咨询ID")
    @TableId(value = "consultation_id", type = IdType.AUTO)
    private Integer consultationId;

    @ApiModelProperty(value = "学生ID")
    private Integer studentId;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "教师ID")
    private Integer teacherId;

    @ApiModelProperty(value = "教师姓名")
    private String teacherName;

    @ApiModelProperty(value = "课程ID")
    private Integer courseId;

    @ApiModelProperty(value = "课程标题")
    private String courseTitle;

    @ApiModelProperty(value = "问题标题")
    private String questionTitle;

    @ApiModelProperty(value = "问题详细描述")
    private String questionContent;

    @ApiModelProperty(value = "问题图片URL")
    private String questionImage;

    @ApiModelProperty(value = "教师回复内容")
    private String replyContent;

    @ApiModelProperty(value = "回复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replyTime;

    @ApiModelProperty(value = "状态：0待回复，1已回复，2已关闭")
    private Integer status;

    @ApiModelProperty(value = "优先级：0普通，1紧急")
    private Integer priority;

    @ApiModelProperty(value = "学生是否已读回复：0未读，1已读")
    private Integer isRead;

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
