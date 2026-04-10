package com.yvliangbao.common.pojo.vo.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 营收趋势数据 VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "RevenueTrendVO", description = "营收趋势数据")
public class RevenueTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日期列表")
    private List<String> dates;

    @ApiModelProperty(value = "营收数据（分）")
    private List<Long> revenues;

    @ApiModelProperty(value = "订单数列表")
    private List<Integer> orders;

    @ApiModelProperty(value = "退款数据（分）")
    private List<Long> refunds;

    @ApiModelProperty(value = "商户ID")
    private Long merchantId;

    @ApiModelProperty(value = "统计天数")
    private Integer days;

    @ApiModelProperty(value = "每日数据详情")
    private List<Object> dailyData;
}
