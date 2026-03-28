package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(value="Teacher对象", description="")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "教师ID")
    @TableId(value = "teach_id", type = IdType.AUTO)
    private Integer teachId;

    @ApiModelProperty(value = "教师名称")
    private String teachName;

    @ApiModelProperty(value = "教师账户")
    private String teachAccount;

    @ApiModelProperty(value = "教师密码")
    private String teachPassword;

    @ApiModelProperty(value = "教师电话")
    private String teachPhone;

    @ApiModelProperty(value = "教师邮箱")
    private String teachEmail;

    @ApiModelProperty(value = "教师地址")
    private String teachAddress;

    @ApiModelProperty(value = "教学科目")
    private String teachSubject;

    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    private Integer del;

    /**
     * JWT token（仅用于登录返回与请求头携带，不落库）
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "JWT token")
    private String token;


}
