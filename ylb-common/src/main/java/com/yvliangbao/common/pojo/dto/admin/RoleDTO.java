package com.yvliangbao.common.pojo.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 角色DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "RoleDTO", description = "角色传输对象")
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色ID（更新时必填）")
    private Long id;

    @ApiModelProperty(value = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称最长50个字符")
    private String roleName;

    @ApiModelProperty(value = "角色编码", required = true)
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码最长50个字符")
    private String roleCode;

    @ApiModelProperty(value = "角色描述")
    @Size(max = 255, message = "角色描述最长255个字符")
    private String roleDesc;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    private Integer status;

    @ApiModelProperty(value = "权限ID列表")
    private java.util.List<Long> permissionIds;
}
