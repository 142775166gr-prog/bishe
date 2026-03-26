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
 * 课程评论表
 * </p>
 *
 * @author system
 * @since 2026-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course_comment")
@ApiModel(value="CourseComment对象", description="课程评论表")
public class CourseComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "评论ID")
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Integer commentId;

    @ApiModelProperty(value = "课程ID")
    private Integer courseId;

    @ApiModelProperty(value = "父评论ID，0表示顶级评论")
    private Integer parentId;

    @ApiModelProperty(value = "被回复的评论ID，0表示不是回复")
    private Integer replyToId;

    @ApiModelProperty(value = "评论用户ID")
    private Integer userId;

    @ApiModelProperty(value = "用户类型：student学生，teacher教师")
    private String userType;

    @ApiModelProperty(value = "用户姓名")
    private String userName;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "被回复用户的姓名")
    private String replyToName;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    @TableLogic
    private Integer del;

    @ApiModelProperty(value = "是否已读，0未读，1已读")
    private Integer isRead;

    @ApiModelProperty(value = "用户头像URL")
    @TableField(exist = false)
    private String userAvatar;

    @ApiModelProperty(value = "子评论列表（回复）")
    @TableField(exist = false)
    private List<CourseComment> replies;
}
