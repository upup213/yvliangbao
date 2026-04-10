package com.yvliangbao.common.pojo.dto.balance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 充值请求DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "RechargeDTO", description = "充值请求")
public class RechargeDTO {

    @ApiModelProperty(value = "充值金额", required = true)
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额最少0.01元")
    private BigDecimal amount;

    @ApiModelProperty(value = "充值方式：1-微信支付，2-模拟充值（测试用）")
    private Integer payMethod = 1;
}
