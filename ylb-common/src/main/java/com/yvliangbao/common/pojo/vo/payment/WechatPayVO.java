package com.yvliangbao.common.pojo.vo.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信支付返回参数
 * 这是返回给小程序前端用于唤起支付的参数
 * 前端使用这些参数调用 wx.requestPayment()
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "WechatPayVO", description = "微信支付参数（返回前端唤起支付）")
public class WechatPayVO {

    @ApiModelProperty(value = "小程序AppID")
    @JsonProperty("appId")
    private String appId;

    @ApiModelProperty(value = "时间戳（秒级）")
    @JsonProperty("timeStamp")
    private String timeStamp;

    @ApiModelProperty(value = "随机字符串")
    @JsonProperty("nonceStr")
    private String nonceStr;

    @ApiModelProperty(value = "订单详情扩展字符串，格式：prepay_id=xxx")
    @JsonProperty("package")
    private String packageStr;

    @ApiModelProperty(value = "签名类型，固定值RSA")
    @JsonProperty("signType")
    private String signType;

    @ApiModelProperty(value = "签名（关键参数，微信会验证）")
    @JsonProperty("paySign")
    private String paySign;

    // ========== 业务辅助字段 ==========

    @ApiModelProperty(value = "支付单号（商户侧）")
    private String paymentNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "支付金额（元）")
    private String amount;

    @ApiModelProperty(value = "过期时间戳（毫秒）")
    private Long expireTime;

    /**
     * 创建微信支付参数
     */
    public static WechatPayVO create(String appId, String prepayId, String paySign, String nonceStr, String timeStamp) {
        WechatPayVO vo = new WechatPayVO();
        vo.setAppId(appId);
        vo.setTimeStamp(timeStamp);
        vo.setNonceStr(nonceStr);
        vo.setPackageStr("prepay_id=" + prepayId);
        vo.setSignType("RSA");
        vo.setPaySign(paySign);
        return vo;
    }
}
