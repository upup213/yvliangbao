package com.yvliangbao.common.pojo.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体
 *
 * @author 余量宝
 */
@Data
@TableName("permission_info")
@ApiModel(value = "PermissionInfo", description = "权限信息")
public class PermissionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "权限编码")
    @TableField("permission_code")
    private String permissionCode;

    @ApiModelProperty(value = "权限名称")
    @TableField("permission_name")
    private String permissionName;

    @ApiModelProperty(value = "权限类型：1-菜单，2-按钮，3-接口")
    @TableField("permission_type")
    private Integer permissionType;

    @ApiModelProperty(value = "父权限ID")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "资源URL")
    @TableField("resource_url")
    private String resourceUrl;

    @ApiModelProperty(value = "菜单路径")
    @TableField("menu_path")
    private String menuPath;

    @ApiModelProperty(value = "菜单图标")
    @TableField("menu_icon")
    private String menuIcon;

    @ApiModelProperty(value = "排序")
    @TableField("sort_order")
    private Integer sortOrder;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    @TableField("status")
    private Integer status;

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
