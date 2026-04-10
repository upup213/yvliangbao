package com.yvliangbao.common.pojo.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "PermissionVO", description = "权限视图对象")
public class PermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "权限ID")
    private Long id;

    @ApiModelProperty(value = "权限编码")
    private String permissionCode;

    @ApiModelProperty(value = "权限名称")
    private String permissionName;

    @ApiModelProperty(value = "权限类型：1-菜单，2-按钮，3-接口")
    private Integer permissionType;

    @ApiModelProperty(value = "父权限ID")
    private Long parentId;

    @ApiModelProperty(value = "资源URL")
    private String resourceUrl;

    @ApiModelProperty(value = "菜单路径")
    private String menuPath;

    @ApiModelProperty(value = "菜单图标")
    private String menuIcon;

    @ApiModelProperty(value = "排序")
    private Integer sortOrder;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    private Integer status;

    @ApiModelProperty(value = "子权限列表")
    private List<PermissionVO> children;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
