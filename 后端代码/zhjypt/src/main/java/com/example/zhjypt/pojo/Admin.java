package com.example.zhjypt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 管理员实体类，对应数据库 admin 表。
 * 存储系统管理员的基本信息，包括账户、密码、联系方式及头像等。
 * 管理员拥有最高权限，可管理教师、学生、课程及公告等全部资源。
 * </p>
 *
 * @author daswswsdgkj
 * @since 2025-12-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Admin对象", description="")
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "管理员ID")
    @TableId(value = "adm_id", type = IdType.AUTO)
    private Integer admId;

    @ApiModelProperty(value = "管理员名称")
    private String admName;

    @ApiModelProperty(value = "管理员账户")
    private String admAccount;

    @ApiModelProperty(value = "管理员密码")
    private String admPassword;

    @ApiModelProperty(value = "管理员电话")
    private String admPhone;

    @ApiModelProperty(value = "管理员邮箱")
    private String admEmail;

    @ApiModelProperty(value = "管理员地址")
    private String admAddress;

    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @ApiModelProperty(value = "逻辑删除，0未删除，1已删除")
    private Integer del;

}
