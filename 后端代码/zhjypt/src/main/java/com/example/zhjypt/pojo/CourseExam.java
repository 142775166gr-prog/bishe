package com.example.zhjypt.pojo;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="CourseExam对象", description="")
public class CourseExam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "课程习题试卷ID")
    private Integer courseExamId;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    private Integer del;


}
