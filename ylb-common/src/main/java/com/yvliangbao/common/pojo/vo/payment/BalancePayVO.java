package com.yvliangbao.common.pojo.vo.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 余额支付返回VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "BalancePayVO", description = "余额支付返回")
public class BalancePayVO {

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "支付金额（元）")
    private BigDecimal amount;

    @ApiModelProperty(value = "支付时间戳")
    private Long payTime;

    @ApiModelProperty(value = "消息")
    private String message;

    public static BalancePayVO success(String orderNo, BigDecimal amount) {
        BalancePayVO vo = new BalancePayVO();
        vo.setSuccess(true);
        vo.setOrderNo(orderNo);
        vo.setAmount(amount);
        vo.setPayTime(System.currentTimeMillis());
        vo.setMessage("余额支付成功");
        return vo;
    }
}
