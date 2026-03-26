package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 选择题选项表
 * </p>
 *
 * @author system
 * @since 2025-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("question_option")
@ApiModel(value="QuestionOption对象", description="选择题选项表")
public class QuestionOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "选项ID")
    @TableId(value = "option_id", type = IdType.AUTO)
    private Integer optionId;

    @ApiModelProperty(value = "题目ID")
    private Integer questionId;

    @ApiModelProperty(value = "选项标签（A、B、C、D）")
    private String optionLabel;

    @ApiModelProperty(value = "选项内容")
    private String optionContent;

    @ApiModelProperty(value = "选项图片URL")
    private String optionImage;

    @ApiModelProperty(value = "是否正确答案：0否，1是")
    private Integer isCorrect;

    @ApiModelProperty(value = "排序")
    private Integer sortOrder;
}