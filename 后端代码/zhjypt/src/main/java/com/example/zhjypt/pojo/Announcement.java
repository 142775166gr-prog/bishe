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
 * 公告表
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("announcement")
@ApiModel(value="Announcement对象", description="公告表")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "公告ID")
    @TableId(value = "announcement_id", type = IdType.AUTO)
    private Integer announcementId;

    @ApiModelProperty(value = "公告标题")
    private String title;

    @ApiModelProperty(value = "公告内容")
    private String content;

    @ApiModelProperty(value = "发布人ID")
    private Integer publisherId;

    @ApiModelProperty(value = "发布人姓名")
    private String publisherName;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    @ApiModelProperty(value = "是否启用，0禁用，1启用")
    private Integer status;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    private Integer del;
}