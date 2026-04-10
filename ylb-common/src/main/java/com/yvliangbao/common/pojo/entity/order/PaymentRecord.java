package com.yvliangbao.common.pojo.entity.order;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 * 用于模拟微信支付流程
 *
 * @author 余量宝
 */
@Data
@TableName("payment_record")
@ApiModel(value = "PaymentRecord", description = "支付记录")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "支付单号（模拟微信支付订单号）")
    @TableField("payment_no")
    private String paymentNo;

    @ApiModelProperty(value = "商户订单号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "商户ID")
    @TableField("merchant_id")
    private Long merchantId;

    @ApiModelProperty(value = "支付金额（分）")
    @TableField("amount")
    private Long amount;

    @ApiModelProperty(value = "支付状态：0-待支付，1-支付成功，2-支付失败，3-已关闭")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "支付方式：1-微信支付，2-余额支付，3-模拟支付")
    @TableField("pay_type")
    private Integer payType;

    @ApiModelProperty(value = "支付时间")
    @TableField("pay_time")
    private LocalDateTime payTime;

    @ApiModelProperty(value = "过期时间")
    @TableField("expire_time")
    private LocalDateTime expireTime;

    @ApiModelProperty(value = "回调通知状态：0-未通知，1-已通知")
    @TableField("notify_status")
    private Integer notifyStatus;

    @ApiModelProperty(value = "回调通知时间")
    @TableField("notify_time")
    private LocalDateTime notifyTime;

    @ApiModelProperty(value = "模拟支付码（用户输入此码确认支付）")
    @TableField("mock_pay_code")
    private String mockPayCode;

    @ApiModelProperty(value = "用户OpenID")
    @TableField("openid")
    private String openid;

    @ApiModelProperty(value = "预支付ID")
    @TableField("prepay_id")
    private String prepayId;

    @ApiModelProperty(value = "交易类型")
    @TableField("trade_type")
    private String tradeType;

    @ApiModelProperty(value = "回调通知URL")
    @TableField("notify_url")
    private String notifyUrl;

    @ApiModelProperty(value = "回调通知次数")
    @TableField("notify_count")
    private Integer notifyCount;

    @ApiModelProperty(value = "第三方交易号（微信支付交易号）")
    @TableField("transaction_id")
    private String transactionId;

    @ApiModelProperty(value = "支付渠道")
    @TableField("channel")
    private String channel;

    @ApiModelProperty(value = "备注")
    @TableField("remark")
    private String remark;

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
