package com.yvliangbao.common.pojo.vo.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 每日统计VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "DailyStatsVO", description = "每日统计数据")
public class DailyStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "统计日期")
    private String date;

    @ApiModelProperty(value = "营收金额（分）")
    private Long revenue;

    @ApiModelProperty(value = "订单数量")
    private Integer orderCount;

    @ApiModelProperty(value = "退款金额（分）")
    private Long refundAmount;

    @ApiModelProperty(value = "核销订单数")
    private Integer verifiedCount;

    @ApiModelProperty(value = "核销金额（分）")
    private Long verifiedAmount;
}
