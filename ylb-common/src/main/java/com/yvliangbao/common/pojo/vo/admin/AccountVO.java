package com.yvliangbao.common.pojo.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账号VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "AccountVO", description = "账号视图对象")
public class AccountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号ID")
    private Long id;

    @ApiModelProperty(value = "管理员编号")
    private String adminNo;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "角色ID列表（逗号分隔）")
    private String roleIds;

    @ApiModelProperty(value = "角色名称列表")
    private String roleNames;

    @ApiModelProperty(value = "角色列表")
    private List<RoleVO> roles;

    @ApiModelProperty(value = "数据权限范围:1全部,2本部门,3仅本人")
    private Integer dataScope;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    private Integer status;

    @ApiModelProperty(value = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "最后登录IP")
    private String lastLoginIp;

    @ApiModelProperty(value = "密码更新时间")
    private LocalDateTime passwordUpdateTime;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
