package com.yvliangbao.common.pojo.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "RoleVO", description = "角色视图对象")
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色ID")
    private Long id;

    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ApiModelProperty(value = "角色描述")
    private String roleDesc;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    private Integer status;

    @ApiModelProperty(value = "是否预设角色:1是,0否")
    private Integer isPreset;

    @ApiModelProperty(value = "权限列表")
    private List<PermissionVO> permissions;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
