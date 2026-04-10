package com.yvliangbao.common.service.order;


import com.yvliangbao.common.pojo.dto.payment.MockPaymentNotifyDTO;
import com.yvliangbao.common.pojo.dto.payment.PaymentCreateRequest;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import com.yvliangbao.common.pojo.vo.payment.PaymentCreateVO;

/**
 * 支付服务接口
 * 支持微信支付、余额支付、模拟支付
 *
 * @author 余量宝
 */
public interface PaymentService {

    /**
     * 发起支付（创建支付单）
     *
     * @param request 支付请求
     * @return 支付参数
     */
    PaymentCreateVO createPayment(PaymentCreateRequest request);

    /**
     * 模拟支付回调通知
     *
     * @param dto 回调参数
     * @return 支付单号（成功时返回）
     */
    String mockPayNotify(MockPaymentNotifyDTO dto);

    /**
     * 查询支付状态
     *
     * @param paymentNo 支付单号
     * @return 支付记录
     */
    PaymentRecord queryPayment(String paymentNo);

    /**
     * 根据订单号查询支付记录
     *
     * @param orderNo 订单号
     * @return 支付记录
     */
    PaymentRecord queryByOrderNo(String orderNo);

    /**
     * 关闭支付单
     *
     * @param paymentNo 支付单号
     * @return 是否成功
     */
    boolean closePayment(String paymentNo);

    /**
     * 注册支付成功回调
     *
     * @param callback 回调函数
     */
    void registerPaySuccessCallback(PaySuccessCallback callback);

    /**
     * 支付成功回调接口
     */
    @FunctionalInterface
    interface PaySuccessCallback {
        /**
         * 支付成功回调
         *
         * @param paymentNo 支付单号
         * @param orderNo 订单号
         * @param userId 用户ID
         */
        void onPaySuccess(String paymentNo, String orderNo, Long userId);
    }
}
