package com.yvliangbao.common.pojo.vo.balance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 充值结果VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "RechargeVO", description = "充值结果")
public class RechargeVO {

    @ApiModelProperty(value = "流水号")
    private String logNo;

    @ApiModelProperty(value = "充值金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "充值后余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "充值方式描述")
    private String payMethodDesc;
}
