package com.yvliangbao.common.pojo.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信支付回调通知DTO
 * 微信支付成功后会向notify_url发送POST请求
 *
 * 回调数据格式（V3版本）：
 * {
 *   "id": "EV-2018022511223320873",
 *   "createTime": "2015-05-20T13:29:35+08:00",
 *   "resourceType": "encrypt-resource",
 *   "eventType": "TRANSACTION.SUCCESS",
 *   "summary": "支付成功",
 *   "resource": {
 *     "originalType": "transaction",
 *     "algorithm": "AEAD_AES_256_GCM",
 *     "ciphertext": "...",
 *     "associatedData": "transaction",
 *     "nonce": "...",
 *     "original_type": "transaction"
 *   }
 * }
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "WechatPayNotifyDTO", description = "微信支付回调通知")
public class WechatPayNotifyDTO {

    @ApiModelProperty(value = "通知ID")
    @JsonProperty("id")
    private String id;

    @ApiModelProperty(value = "通知创建时间")
    @JsonProperty("create_time")
    private String createTime;

    @ApiModelProperty(value = "通知类型")
    @JsonProperty("event_type")
    private String eventType;

    @ApiModelProperty(value = "通知摘要")
    private String summary;

    @ApiModelProperty(value = "回调数据")
    private Resource resource;

    /**
     * 加密资源
     */
    @Data
    public static class Resource {
        @ApiModelProperty(value = "加密算法")
        private String algorithm;

        @ApiModelProperty(value = "数据密文")
        private String ciphertext;

        @ApiModelProperty(value = "附加数据")
        @JsonProperty("associated_data")
        private String associatedData;

        @ApiModelProperty(value = "随机串")
        private String nonce;
    }

    /**
     * 解密后的支付结果数据
     */
    @Data
    @ApiModel(value = "Transaction", description = "支付交易信息")
    public static class Transaction {
        @ApiModelProperty(value = "微信支付订单号")
        @JsonProperty("transaction_id")
        private String transactionId;

        @ApiModelProperty(value = "商户订单号")
        @JsonProperty("out_trade_no")
        private String outTradeNo;

        @ApiModelProperty(value = "交易类型")
        @JsonProperty("trade_type")
        private String tradeType;

        @ApiModelProperty(value = "交易状态")
        @JsonProperty("trade_state")
        private String tradeState;

        @ApiModelProperty(value = "交易状态描述")
        @JsonProperty("trade_state_desc")
        private String tradeStateDesc;

        @ApiModelProperty(value = "付款银行")
        @JsonProperty("bank_type")
        private String bankType;

        @ApiModelProperty(value = "支付完成时间")
        @JsonProperty("success_time")
        private String successTime;

        @ApiModelProperty(value = "用户标识")
        private Payer payer;

        @ApiModelProperty(value = "订单金额")
        private Amount amount;
    }

    @Data
    public static class Payer {
        @ApiModelProperty(value = "用户openid")
        private String openid;
    }

    @Data
    public static class Amount {
        @ApiModelProperty(value = "总金额（分）")
        private Integer total;

        @ApiModelProperty(value = "货币类型")
        private String currency;
    }
}
