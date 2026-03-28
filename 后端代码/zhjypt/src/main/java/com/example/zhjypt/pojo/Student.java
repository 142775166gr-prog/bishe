package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 学生实体类，对应数据库 student 表。
 * 存储学生用户的基本信息，包括账户、密码、所在大学、联系方式及头像等。
 * 学生可选课学习、参加考试、查看公告及发起在线咨询。
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Student对象", description="")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "学生ID")
    @TableId(value = "stu_id", type = IdType.AUTO)
    private Integer stuId;

    @ApiModelProperty(value = "学生名称")
    private String stuName;

    @ApiModelProperty(value = "学生账户")
    private String stuAccount;

    @ApiModelProperty(value = "学生密码")
    private String stuPassword;

    @ApiModelProperty(value = "学生电话")
    private String stuPhone;

    @ApiModelProperty(value = "学生邮箱")
    private String stuEmail;

    @ApiModelProperty(value = "学生大学")
    private String stuUniversity;

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
