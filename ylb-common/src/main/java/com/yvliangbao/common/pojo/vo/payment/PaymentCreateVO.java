package com.yvliangbao.common.pojo.vo.payment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发起支付返回VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "PaymentCreateVO", description = "发起支付返回")
public class PaymentCreateVO {

    @ApiModelProperty(value = "支付单号")
    private String paymentNo;

    @ApiModelProperty(value = "商户订单号")
    private String orderNo;

    @ApiModelProperty(value = "支付金额（元）")
    private BigDecimal amount;

    @ApiModelProperty(value = "支付状态：0-待支付，1-支付成功")
    private Integer status;

    @ApiModelProperty(value = "过期时间（时间戳，毫秒）")
    private Long expireTime;

    @ApiModelProperty(value = "模拟支付码（用于模拟支付确认）")
    private String mockPayCode;

    @ApiModelProperty(value = "模拟支付确认URL")
    private String mockPayUrl;

    @ApiModelProperty(value = "支付方式：1-微信支付，2-余额支付，3-模拟支付")
    private Integer payType;

    @ApiModelProperty(value = "微信支付参数（JSAPI方式）")
    private WechatPayParams wechatPayParams;

    @ApiModelProperty(value = "支付是否成功")
    private Boolean success;

    @ApiModelProperty(value = "提示消息")
    private String message;

    /**
     * 构建模拟支付返回
     */
    public static PaymentCreateVO mock(String paymentNo, String orderNo, BigDecimal amount,
                                        String mockPayCode, Long expireTime) {
        PaymentCreateVO vo = new PaymentCreateVO();
        vo.setPaymentNo(paymentNo);
        vo.setOrderNo(orderNo);
        vo.setAmount(amount);
        vo.setStatus(0);
        vo.setExpireTime(expireTime);
        vo.setMockPayCode(mockPayCode);
        vo.setMockPayUrl("/mock-payment/pay?paymentNo=" + paymentNo);
        vo.setPayType(3); // 模拟支付
        return vo;
    }

    /**
     * 构建微信支付返回
     */
    public static PaymentCreateVO wechat(String paymentNo, String orderNo, BigDecimal amount,
                                          WechatPayParams params) {
        PaymentCreateVO vo = new PaymentCreateVO();
        vo.setPaymentNo(paymentNo);
        vo.setOrderNo(orderNo);
        vo.setAmount(amount);
        vo.setStatus(0);
        vo.setPayType(1); // 微信支付
        vo.setWechatPayParams(params);
        return vo;
    }

    /**
     * 微信支付参数
     */
    @Data
    @ApiModel(value = "WechatPayParams", description = "微信支付参数")
    public static class WechatPayParams {
        @ApiModelProperty(value = "时间戳")
        private String timeStamp;

        @ApiModelProperty(value = "随机字符串")
        private String nonceStr;

        @ApiModelProperty(value = "预支付交易会话标识")
        private String prepayId;

        @ApiModelProperty(value = "签名方式")
        private String signType;

        @ApiModelProperty(value = "签名")
        private String paySign;
    }
}
