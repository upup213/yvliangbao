package com.yvliangbao.common.pojo.dto.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 模拟支付回调通知DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MockPaymentNotifyDTO", description = "模拟支付回调通知")
public class MockPaymentNotifyDTO {

    @NotBlank(message = "支付单号不能为空")
    @ApiModelProperty(value = "支付单号", required = true)
    private String paymentNo;

    @NotBlank(message = "商户订单号不能为空")
    @ApiModelProperty(value = "商户订单号", required = true)
    private String orderNo;

    @NotBlank(message = "模拟支付码不能为空")
    @ApiModelProperty(value = "模拟支付码", required = true)
    private String mockPayCode;

    @NotBlank(message = "支付结果不能为空")
    @ApiModelProperty(value = "支付结果：success-成功，fail-失败", required = true)
    private String result;
}
