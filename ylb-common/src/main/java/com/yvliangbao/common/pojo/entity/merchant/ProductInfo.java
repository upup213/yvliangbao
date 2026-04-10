package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 商品信息实体（盲盒/魔法袋）
 *
 * @author 余量宝
 */
@Data
@TableName("product_info")
@ApiModel(value = "ProductInfo", description = "商品信息")
public class ProductInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "门店ID")
    @TableField("store_id")
    private Long storeId;

    @ApiModelProperty(value = "商品编号")
    @TableField("product_no")
    private String productNo;

    @ApiModelProperty(value = "商品名称")
    @TableField("product_name")
    private String productName;

    @ApiModelProperty(value = "商品描述")
    @TableField("product_desc")
    private String productDesc;

    @ApiModelProperty(value = "商品图片JSON数组")
    @TableField("product_images")
    private String productImages;

    @ApiModelProperty(value = "分类ID")
    @TableField("category_id")
    private Long categoryId;

    @ApiModelProperty(value = "原价（元）")
    @TableField("original_price")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "清仓价（元）")
    @TableField("sale_price")
    private BigDecimal salePrice;

    @ApiModelProperty(value = "折扣率（%）")
    @TableField("discount_rate")
    private BigDecimal discountRate;

    @ApiModelProperty(value = "取餐开始时间")
    @TableField("pickup_time_start")
    private LocalTime pickupTimeStart;

    @ApiModelProperty(value = "取餐结束时间")
    @TableField("pickup_time_end")
    private LocalTime pickupTimeEnd;

    @ApiModelProperty(value = "单人限购数量")
    @TableField("purchase_limit")
    private Integer purchaseLimit;

    @ApiModelProperty(value = "状态：0-已下架，1-在售，2-已售罄")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "上架时间")
    @TableField("shelf_time")
    private LocalDateTime shelfTime;

    @ApiModelProperty(value = "已售数量")
    @TableField("sold_count")
    private Integer soldCount;

    @ApiModelProperty(value = "总库存（非数据库字段，从库存表查询）")
    @TableField(exist = false)
    private Integer totalStock;

    @ApiModelProperty(value = "可用库存（非数据库字段，从库存表查询）")
    @TableField(exist = false)
    private Integer availableStock;

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
