package com.yvliangbao.common.pojo.vo.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 历史营收查询结果 VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "RevenueHistoryVO", description = "历史营收查询结果")
public class RevenueHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "营收金额（分）")
    private Long revenue;

    @ApiModelProperty(value = "订单数")
    private Integer orders;

    @ApiModelProperty(value = "退款金额（分）")
    private Long refund;

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "商户ID")
    private Long merchantId;

    @ApiModelProperty(value = "总营收（分）")
    private Long totalRevenue;

    @ApiModelProperty(value = "订单数")
    private Integer orderCount;

    @ApiModelProperty(value = "核销数")
    private Integer verifiedCount;

    @ApiModelProperty(value = "核销金额（分）")
    private Long verifiedRevenue;

    @ApiModelProperty(value = "退款金额（分）")
    private Long refundAmount;
}
