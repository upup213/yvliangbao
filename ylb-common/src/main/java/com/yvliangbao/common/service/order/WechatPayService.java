package com.yvliangbao.common.service.order;

import com.yvliangbao.common.pojo.dto.payment.WechatPayRequest;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import com.yvliangbao.common.pojo.vo.payment.WechatPayVO;

/**
 * 微信支付服务接口
 * 完整模拟微信支付流程
 *
 * 流程说明：
 * 1. createPayment() - 创建支付单，调用微信统一下单API
 * 2. 返回支付参数给前端
 * 3. 前端调用 wx.requestPayment() 唤起支付
 * 4. 微信支付成功后回调 handleNotify()
 * 5. 更新订单状态
 *
 * @author 余量宝
 */
public interface WechatPayService {

    /**
     * 创建微信支付订单（统一下单）
     * 步骤2：后端向微信支付统一下单接口请求支付参数
     *
     * @param request 支付请求
     * @return 返回给前端唤起支付的参数
     */
    WechatPayVO createPayment(WechatPayRequest request);

    /**
     * 处理微信支付回调通知
     * 步骤6-7：微信支付成功后回调后端，后端处理并响应
     *
     * @param notifyData 回调数据（JSON字符串）
     * @return 响应给微信的结果
     */
    String handleNotify(String notifyData);

    /**
     * 查询支付状态（主动查询）
     * 用于前端轮询或后台查询
     *
     * @param orderNo 订单号
     * @return 支付记录
     */
    PaymentRecord queryPayment(String orderNo);

    /**
     * 关闭支付订单
     * 用户取消支付或订单超时时调用
     *
     * @param orderNo 订单号
     * @return 是否成功
     */
    boolean closePayment(String orderNo);

    /**
     * 支付成功回调接口
     * 用于通知业务系统支付成功
     */
    interface PaySuccessCallback {
        /**
         * 支付成功回调
         *
         * @param orderNo       业务订单号
         * @param transactionId 微信交易号
         * @param openid        用户openid
         * @param amount        支付金额（分）
         */
        void onPaySuccess(String orderNo, String transactionId, String openid, Long amount);
    }

    /**
     * 注册支付成功回调
     */
    void registerPaySuccessCallback(PaySuccessCallback callback);
}
