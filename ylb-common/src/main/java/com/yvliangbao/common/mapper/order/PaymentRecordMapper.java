package com.yvliangbao.common.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.order.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 支付记录 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    /**
     * 根据订单号查询支付记录
     *
     * @param orderNo 订单号
     * @return 支付记录
     */
    @Select("SELECT * FROM payment_record WHERE order_no = #{orderNo} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    PaymentRecord selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 原子更新支付状态为成功
     *
     * @param paymentNo 支付单号
     * @param mockPayCode 模拟支付码（校验）
     * @return 更新行数
     */
    @Update("UPDATE payment_record SET status = 1, pay_time = NOW(), update_time = NOW() " +
            "WHERE payment_no = #{paymentNo} AND mock_pay_code = #{mockPayCode} AND status = 0")
    int updateToPaid(@Param("paymentNo") String paymentNo, @Param("mockPayCode") String mockPayCode);

    /**
     * 原子更新通知状态
     *
     * @param paymentNo 支付单号
     * @return 更新行数
     */
    @Update("UPDATE payment_record SET notify_status = 1, notify_time = NOW(), update_time = NOW() " +
            "WHERE payment_no = #{paymentNo}")
    int updateNotifyStatus(@Param("paymentNo") String paymentNo);

    /**
     * 微信支付成功更新状态
     *
     * @param orderNo 商户订单号
     * @param transactionId 微信支付交易号
     * @param status 支付状态
     * @param payTime 支付时间
     * @return 更新行数
     */
    @Update("UPDATE payment_record SET status = #{status}, transaction_id = #{transactionId}, pay_time = #{payTime}, update_time = NOW() " +
            "WHERE order_no = #{orderNo} AND status = 0")
    int updateToPaidByWechat(@Param("orderNo") String orderNo,
                             @Param("transactionId") String transactionId,
                             @Param("status") Integer status,
                             @Param("payTime") java.time.LocalDateTime payTime);
}
