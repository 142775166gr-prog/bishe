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
 * 课程分类表
 * </p>
 *
 * @author system
 * @since 2025-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("course_category")
@ApiModel(value="CourseCategory对象", description="课程分类表")
public class CourseCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类ID")
    @TableId(value = "category_id", type = IdType.AUTO)
    private Integer categoryId;

    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    @ApiModelProperty(value = "分类描述")
    private String categoryDesc;

    @ApiModelProperty(value = "排序顺序")
    private Integer sortOrder;

    @ApiModelProperty(value = "状态，0禁用，1启用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    private Integer del;
}