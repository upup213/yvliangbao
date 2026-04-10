package com.yvliangbao.common.pojo.dto.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 微信支付请求DTO
 * 对应微信支付统一下单API的请求参数
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "WechatPayRequest", description = "微信支付请求")
public class WechatPayRequest {

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "商户订单号（业务订单号）", required = true)
    private String orderNo;

    @NotNull(message = "支付金额不能为空")
    @ApiModelProperty(value = "支付金额（单位：元）", required = true, example = "0.01")
    private BigDecimal amount;

    @NotBlank(message = "用户openid不能为空")
    @ApiModelProperty(value = "用户openid（小程序支付必需）", required = true)
    private String openid;

    @ApiModelProperty(value = "商品描述", example = "余量宝商品购买")
    private String description;

    @ApiModelProperty(value = "附加数据（回调时原样返回）")
    private String attach;

    @ApiModelProperty(value = "用户ID（业务系统使用）")
    private Long userId;

    @ApiModelProperty(value = "商户ID（业务系统使用）")
    private Long merchantId;
}
