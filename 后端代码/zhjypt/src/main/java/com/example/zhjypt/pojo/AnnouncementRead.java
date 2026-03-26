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
 * 公告阅读记录表
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("announcement_read")
@ApiModel(value="AnnouncementRead对象", description="公告阅读记录表")
public class AnnouncementRead implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "read_id", type = IdType.AUTO)
    private Integer readId;

    @ApiModelProperty(value = "公告ID")
    private Integer announcementId;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "用户类型：admin/teacher/student")
    private String userType;

    @ApiModelProperty(value = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date readTime;
}