package com.yvliangbao.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置
 * 生产环境中这些配置应该从配置中心或数据库读取
 *
 * @author 余量宝
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WechatPayConfig {

    /**
     * 小程序AppID
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户API密钥（用于签名）
     */
    private String apiKey;

    /**
     * 商户API证书序列号（V3版本）
     */
    private String merchantSerialNumber;

    /**
     * 商户私钥文件路径（V3版本）
     */
    private String privateKeyPath;

    /**
     * 回调通知URL（必须是公网可访问的地址）
     */
    private String notifyUrl;

    /**
     * 微信支付统一下单API地址
     */
    private String unifiedOrderUrl = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";

    /**
     * 微信支付查询订单API地址
     */
    private String queryOrderUrl = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/";

    /**
     * 是否启用模拟模式（开发测试用）
     * true: 使用模拟支付，不调用真实微信API
     * false: 调用真实微信支付API
     */
    private boolean mockMode = true;
}
