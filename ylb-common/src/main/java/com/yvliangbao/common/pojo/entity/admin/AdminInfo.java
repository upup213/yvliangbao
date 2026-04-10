package com.yvliangbao.common.pojo.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 平台管理员实体
 *
 * @author 余量宝
 */
@Data
@TableName("admin_info")
@ApiModel(value = "AdminInfo", description = "平台管理员")
public class AdminInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "管理员编号")
    @TableField("admin_no")
    private String adminNo;

    @ApiModelProperty(value = "用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "真实姓名")
    @TableField("real_name")
    private String realName;

    @ApiModelProperty(value = "手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    private String email;

    @ApiModelProperty(value = "角色ID")
    @TableField("role_id")
    private Long roleId;

    @ApiModelProperty(value = "角色ID列表（多角色支持，逗号分隔）")
    @TableField("role_ids")
    private String roleIds;

    @ApiModelProperty(value = "数据权限范围:1全部,2本部门,3仅本人")
    @TableField("data_scope")
    private Integer dataScope;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "最后登录IP")
    @TableField("last_login_ip")
    private String lastLoginIp;

    @ApiModelProperty(value = "密码更新时间")
    @TableField("password_update_time")
    private LocalDateTime passwordUpdateTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除：0-未删除，1-已删除")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
