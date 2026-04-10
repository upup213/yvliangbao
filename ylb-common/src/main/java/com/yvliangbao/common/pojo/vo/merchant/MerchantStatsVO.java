package com.yvliangbao.common.pojo.vo.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商户统计数据 VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MerchantStatsVO", description = "商户统计数据")
public class MerchantStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== 今日数据 ==========

    @ApiModelProperty(value = "今日订单数")
    private Integer todayOrders;

    @ApiModelProperty(value = "今日营收（分）")
    private Long todayRevenue;

    @ApiModelProperty(value = "今日核销数")
    private Integer todayVerified;

    @ApiModelProperty(value = "今日退款金额（分）")
    private Long todayRefund;

    // ========== 昨日数据（用于环比） ==========

    @ApiModelProperty(value = "昨日订单数")
    private Integer yesterdayOrders;

    @ApiModelProperty(value = "昨日营收（分）")
    private Long yesterdayRevenue;

    @ApiModelProperty(value = "昨日核销数")
    private Integer yesterdayVerified;

    // ========== 本月数据 ==========

    @ApiModelProperty(value = "本月订单数")
    private Integer monthOrders;

    @ApiModelProperty(value = "本月营收（分）")
    private Long monthRevenue;

    @ApiModelProperty(value = "本月核销数")
    private Integer monthVerified;

    @ApiModelProperty(value = "本月退款金额（分）")
    private Long monthRefund;

    // ========== 累计数据 ==========

    @ApiModelProperty(value = "累计订单数")
    private Integer totalOrders;

    @ApiModelProperty(value = "累计营收（分）")
    private Long totalRevenue;

    @ApiModelProperty(value = "累计退款金额（分）")
    private Long totalRefund;

    @ApiModelProperty(value = "累计节省金额（分）")
    private Long totalSaved;

    // ========== 待处理 ==========

    @ApiModelProperty(value = "待核销订单数")
    private Integer pendingVerify;

    @ApiModelProperty(value = "退款中订单数")
    private Integer refunding;

    // ========== 门店统计 ==========

    @ApiModelProperty(value = "门店数量")
    private Integer storeCount;

    @ApiModelProperty(value = "在售商品数")
    private Integer productCount;

    // ========== 环比增长率 ==========

    @ApiModelProperty(value = "订单环比增长率（百分比，如 15.5 表示增长15.5%）")
    private BigDecimal orderGrowthRate;

    @ApiModelProperty(value = "营收环比增长率（百分比）")
    private BigDecimal revenueGrowthRate;

    @ApiModelProperty(value = "核销环比增长率（百分比）")
    private BigDecimal verifiedGrowthRate;

    @ApiModelProperty(value = "商户ID")
    private Long merchantId;
}
