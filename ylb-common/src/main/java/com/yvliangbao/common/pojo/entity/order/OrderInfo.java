package com.yvliangbao.common.pojo.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 订单信息实体
 *
 * @author 余量宝
 */
@Data
@TableName("order_info")
@ApiModel(value = "OrderInfo", description = "订单信息")
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "商户ID")
    @TableField("merchant_id")
    private Long merchantId;

    @ApiModelProperty(value = "门店ID")
    @TableField("store_id")
    private Long storeId;

    @ApiModelProperty(value = "门店名称（快照）")
    @TableField("store_name")
    private String storeName;

    @ApiModelProperty(value = "商品ID")
    @TableField("product_id")
    private Long productId;

    @ApiModelProperty(value = "商品名称（快照）")
    @TableField("product_name")
    private String productName;

    @ApiModelProperty(value = "商品图片（快照）")
    @TableField("product_images")
    private String productImages;

    @ApiModelProperty(value = "购买数量")
    @TableField("quantity")
    private Integer quantity;

    @ApiModelProperty(value = "商品原价（元）")
    @TableField("original_price")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "商品清仓价（元）")
    @TableField("sale_price")
    private BigDecimal salePrice;

    @ApiModelProperty(value = "订单总金额（元）")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "实付金额（元）")
    @TableField("pay_amount")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "余额支付金额（元）")
    @TableField("balance_amount")
    private BigDecimal balanceAmount;

    @ApiModelProperty(value = "取餐开始时间")
    @TableField("pickup_time_start")
    private LocalTime pickupTimeStart;

    @ApiModelProperty(value = "取餐结束时间")
    @TableField("pickup_time_end")
    private LocalTime pickupTimeEnd;

    @ApiModelProperty(value = "备餐完成时间")
    @TableField("ready_time")
    private LocalDateTime readyTime;

    @ApiModelProperty(value = "提货码（6位数字）")
    @TableField("pickup_code")
    private String pickupCode;

    @ApiModelProperty(value = "订单状态：0-待支付，1-已支付，2-待取餐，3-已完成，4-已取消，5-已退款，6-退款中")
    @TableField("order_status")
    private Integer orderStatus;

    @ApiModelProperty(value = "支付状态：0-未支付，1-已支付，2-部分退款，3-全额退款")
    @TableField("pay_status")
    private Integer payStatus;

    @ApiModelProperty(value = "支付时间")
    @TableField("pay_time")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "取消时间")
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    @ApiModelProperty(value = "取消原因")
    @TableField("cancel_reason")
    private String cancelReason;

    @ApiModelProperty(value = "完成时间")
    @TableField("finish_time")
    private LocalDateTime finishTime;

    @ApiModelProperty(value = "退款申请时间")
    @TableField("refund_apply_time")
    private LocalDateTime refundApplyTime;

    @ApiModelProperty(value = "退款时间")
    @TableField("refund_time")
    private LocalDateTime refundTime;

    @ApiModelProperty(value = "退款原因")
    @TableField("refund_reason")
    private String refundReason;

    @ApiModelProperty(value = "过期时间（支付超时）")
    @TableField("expire_time")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "支付中状态：0-正常，1-支付中")
    @TableField("paying_status")
    private Integer payingStatus;

    @ApiModelProperty(value = "支付开始时间")
    @TableField("paying_start_time")
    private LocalDateTime payingStartTime;

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
