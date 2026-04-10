package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存信息实体
 *
 * @author 余量宝
 */
@Data
@TableName("inventory_info")
@ApiModel(value = "InventoryInfo", description = "库存信息")
public class InventoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商品ID")
    @TableField("product_id")
    private Long productId;

    @ApiModelProperty(value = "总库存")
    @TableField("total_stock")
    private Integer totalStock;

    @ApiModelProperty(value = "可用库存")
    @TableField("available_stock")
    private Integer availableStock;

    @ApiModelProperty(value = "锁定库存")
    @TableField("locked_stock")
    private Integer lockedStock;

    @ApiModelProperty(value = "版本号（乐观锁）")
    @TableField("version")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
