package com.yvliangbao.common.service.order;

import com.yvliangbao.common.pojo.dto.payment.MockPaymentNotifyDTO;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.pojo.vo.payment.PaymentCreateVO;

/**
 * 模拟支付服务接口
 * 模拟真实微信支付流程
 *
 * @author 余量宝
 */
public interface MockPaymentService {

    /**
     * 发起支付（创建支付单）
     *
     * @param order 订单信息
     * @return 支付参数
     */
    PaymentCreateVO createPayment(OrderInfo order);

    /**
     * 模拟支付回调通知
     *
     * @param dto 回调参数
     * @return 是否成功
     */
    boolean mockPayNotify(MockPaymentNotifyDTO dto);

    /**
     * 查询支付状态
     *
     * @param paymentNo 支付单号
     * @return 支付记录
     */
    com.yvliangbao.common.pojo.entity.order.PaymentRecord queryPayment(String paymentNo);

    /**
     * 关闭支付单
     *
     * @param paymentNo 支付单号
     * @return 是否成功
     */
    boolean closePayment(String paymentNo);
}
