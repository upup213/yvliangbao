package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商户每日统计实体
 *
 * @author 余量宝
 */
@Data
@TableName("merchant_daily_stats")
@ApiModel(value = "MerchantDailyStats", description = "商户每日统计")
public class MerchantDailyStats implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商户ID")
    @TableField("merchant_id")
    private Long merchantId;

    @ApiModelProperty(value = "统计日期")
    @TableField("stat_date")
    private LocalDate statDate;

    @ApiModelProperty(value = "营收总额（分）")
    @TableField("revenue")
    private Long revenue;

    @ApiModelProperty(value = "订单数量")
    @TableField("order_count")
    private Integer orderCount;

    @ApiModelProperty(value = "退款总额（分）")
    @TableField("refund_amount")
    private Long refundAmount;

    @ApiModelProperty(value = "核销订单数")
    @TableField("verified_count")
    private Integer verifiedCount;

    @ApiModelProperty(value = "核销金额（分）")
    @TableField("verified_amount")
    private Long verifiedAmount;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
