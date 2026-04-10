package com.yvliangbao.common.pojo.dto.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 发起支付请求DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "PaymentCreateRequest", description = "发起支付请求")
public class PaymentCreateRequest {

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号", required = true)
    private String orderNo;

    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @NotNull(message = "商户ID不能为空")
    @ApiModelProperty(value = "商户ID", required = true)
    private Long merchantId;

    @NotNull(message = "支付金额不能为空")
    @ApiModelProperty(value = "支付金额（元）", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "支付方式：1-微信支付，2-余额支付，3-模拟支付（默认）")
    private Integer payType = 3;

    @ApiModelProperty(value = "支付渠道：APP、H5、JSAPI、NATIVE")
    private String channel;

    @ApiModelProperty(value = "客户端IP")
    private String clientIp;

    @ApiModelProperty(value = "备注")
    private String remark;
}
