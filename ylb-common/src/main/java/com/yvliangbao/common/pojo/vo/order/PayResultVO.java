package com.yvliangbao.common.pojo.vo.order;

import com.yuliangbao.common.pojo.entity.order.OrderInfo;
import lombok.Data;

import java.util.Map;

/**
 * 支付结果 VO
 *
 * @author 余量宝
 */
@Data
public class PayResultVO {
    /**
     * 订单信息
     */
    private OrderInfo order;
    
    /**
     * 微信支付参数（如果配置了微信支付）
     * 包含：timeStamp, nonceStr, package, signType, paySign
     */
    private Map<String, String> payParams;
    
    /**
     * 是否为模拟支付
     */
    private Boolean mockPay;
    
    public static PayResultVO mock(OrderInfo order) {
        PayResultVO vo = new PayResultVO();
        vo.setOrder(order);
        vo.setMockPay(true);
        return vo;
    }
    
    public static PayResultVO wechat(OrderInfo order, Map<String, String> payParams) {
        PayResultVO vo = new PayResultVO();
        vo.setOrder(order);
        vo.setPayParams(payParams);
        vo.setMockPay(false);
        return vo;
    }
}
