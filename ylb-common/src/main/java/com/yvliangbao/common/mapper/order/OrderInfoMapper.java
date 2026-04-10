package com.yvliangbao.common.mapper.order;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.pojo.vo.order.OrderAdminVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息 Mapper
 * 
 * SQL 定义在 resources/mapper/OrderInfoMapper.xml 文件中
 *
 * @author 余量宝
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    
    // ========== 订单状态更新方法 ==========
    
    int updateStatus(@Param("orderNo") String orderNo,
                     @Param("userId") Long userId,
                     @Param("oldStatus") Integer oldStatus,
                     @Param("newStatus") Integer newStatus,
                     @Param("payTime") LocalDateTime payTime);
    
    int updateStatusByPickupCode(@Param("pickupCode") String pickupCode,
                                  @Param("merchantId") Long merchantId,
                                  @Param("newStatus") Integer newStatus,
                                  @Param("finishTime") LocalDateTime finishTime);

    int updateStatusToReady(@Param("orderNo") String orderNo,
                            @Param("merchantId") Long merchantId,
                            @Param("oldStatus") Integer oldStatus,
                            @Param("newStatus") Integer newStatus,
                            @Param("readyTime") LocalDateTime readyTime);

    int updateStatusToRefunded(@Param("orderNo") String orderNo,
                               @Param("merchantId") Long merchantId,
                               @Param("newStatus") Integer newStatus,
                               @Param("refundTime") LocalDateTime refundTime);

    int updateStatusToRefunding(@Param("orderNo") String orderNo,
                                @Param("userId") Long userId,
                                @Param("refundReason") String refundReason,
                                @Param("refundApplyTime") LocalDateTime refundApplyTime);

    int autoRefund(@Param("orderNo") String orderNo,
                   @Param("newStatus") Integer newStatus,
                   @Param("refundTime") LocalDateTime refundTime);

    int updateStatusRejectRefund(@Param("orderNo") String orderNo,
                                 @Param("merchantId") Long merchantId,
                                 @Param("newStatus") Integer newStatus);
    
    int cancelTimeoutOrder(@Param("orderNo") String orderNo,
                           @Param("cancelReason") String cancelReason,
                           @Param("cancelTime") LocalDateTime cancelTime);

    // ========== 查询方法 ==========
    
    List<OrderInfo> selectTimeoutRefundingOrders(@Param("timeoutHours") int timeoutHours);
    
    List<OrderInfo> selectTodayVerifiedOrders(@Param("storeIds") List<Long> storeIds);

    List<OrderInfo> selectTimeoutOrders(@Param("expireTime") LocalDateTime expireTime,
                                        @Param("limit") int limit);

    // ========== 用户统计方法 ==========
    
    int countCompletedOrders(@Param("userId") Long userId);

    Long sumSavedAmount(@Param("userId") Long userId);

    int countByStatus(@Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);

    int countWaitingOrders(@Param("userId") Long userId);

    Long sumTotalSpent(@Param("userId") Long userId);

    Long sumMonthSpent(@Param("userId") Long userId);

    Long sumMonthSaved(@Param("userId") Long userId);

    // ========== 商户统计方法 ==========
    
    int countMerchantTodayOrders(@Param("merchantId") Long merchantId);

    Long sumMerchantTodayRevenue(@Param("merchantId") Long merchantId);

    int countMerchantTodayVerified(@Param("merchantId") Long merchantId);

    Long sumMerchantTodayRefund(@Param("merchantId") Long merchantId);

    int countMerchantTotalOrders(@Param("merchantId") Long merchantId);

    Long sumMerchantTotalRevenue(@Param("merchantId") Long merchantId);

    Long sumMerchantTotalRefund(@Param("merchantId") Long merchantId);

    Long sumMerchantTotalSaved(@Param("merchantId") Long merchantId);

    int countMerchantPendingVerify(@Param("merchantId") Long merchantId);

    int countMerchantRefunding(@Param("merchantId") Long merchantId);

    Long sumMerchantRevenueByRange(@Param("merchantId") Long merchantId,
                                   @Param("startDate") String startDate,
                                   @Param("endDate") String endDate);

    int countMerchantOrdersByRange(@Param("merchantId") Long merchantId,
                                   @Param("startDate") String startDate,
                                   @Param("endDate") String endDate);

    // ========== 昨日统计 ==========
    
    int countMerchantYesterdayOrders(@Param("merchantId") Long merchantId);

    Long sumMerchantYesterdayRevenue(@Param("merchantId") Long merchantId);

    int countMerchantYesterdayVerified(@Param("merchantId") Long merchantId);

    // ========== 本月统计 ==========
    
    int countMerchantMonthOrders(@Param("merchantId") Long merchantId);

    Long sumMerchantMonthRevenue(@Param("merchantId") Long merchantId);

    int countMerchantMonthVerified(@Param("merchantId") Long merchantId);

    Long sumMerchantMonthRefund(@Param("merchantId") Long merchantId);

    Long sumMerchantRefundByRange(@Param("merchantId") Long merchantId,
                                  @Param("startDate") String startDate,
                                  @Param("endDate") String endDate);

    // ========== 以下方法XML中没有，使用注解定义 ==========
    
    /**
     * 统计商户指定日期范围的核销数
     */
    @Select("SELECT COUNT(*) FROM order_info " +
            "WHERE merchant_id = #{merchantId} " +
            "  AND order_status = 3 " +
            "  AND DATE(finish_time) BETWEEN #{startDate} AND #{endDate} " +
            "  AND deleted = 0")
    int countMerchantVerifiedByRange(@Param("merchantId") Long merchantId,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate);

    /**
     * 统计商户指定日期范围的核销金额
     */
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM order_info " +
            "WHERE merchant_id = #{merchantId} " +
            "  AND order_status = 3 " +
            "  AND DATE(finish_time) BETWEEN #{startDate} AND #{endDate} " +
            "  AND deleted = 0")
    Long sumMerchantVerifiedByRange(@Param("merchantId") Long merchantId,
                                    @Param("startDate") String startDate,
                                    @Param("endDate") String endDate);

    // ========== 平台统计方法 ==========
    
    java.math.BigDecimal selectSumPayAmount();

    /**
     * 统计条件下的支付金额
     */
    default java.math.BigDecimal selectSumPayAmountByWrapper(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderInfo> wrapper) {
        wrapper.select(OrderInfo::getPayAmount);
        java.math.BigDecimal sum = selectList(wrapper).stream()
                .map(o -> o.getPayAmount() != null ? new java.math.BigDecimal(o.getPayAmount().toString()) : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return sum;
    }

    long selectActiveUserCount(@Param("since") LocalDateTime since);

    long selectActiveMerchantCount(@Param("since") LocalDateTime since);

    // ========== 平台端订单管理 ==========
    
    IPage<OrderAdminVO> selectAdminOrderList(Page<OrderAdminVO> page,
                                             @Param("orderNo") String orderNo,
                                             @Param("phone") String phone,
                                             @Param("merchantId") Long merchantId,
                                             @Param("status") Integer status,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate);

    OrderAdminVO selectAdminOrderDetail(@Param("orderNo") String orderNo);

    // ========== 管理员操作 ==========
    
    int adminForceRefund(@Param("orderNo") String orderNo, @Param("reason") String reason);

    int adminForceCancel(@Param("orderNo") String orderNo, @Param("reason") String reason);

    // ========== 营收趋势 ==========

    /**
     * 按天统计营收趋势
     */
    @Select("SELECT DATE(finish_time) as date, " +
            "       COUNT(*) as order_count, " +
            "       COALESCE(SUM(pay_amount), 0) as revenue " +
            "FROM order_info " +
            "WHERE merchant_id = #{merchantId} " +
            "  AND order_status = 3 " +
            "  AND DATE(finish_time) BETWEEN DATE_SUB(CURDATE(), INTERVAL #{days} DAY) AND CURDATE() " +
            "  AND deleted = 0 " +
            "GROUP BY DATE(finish_time) " +
            "ORDER BY DATE(finish_time)")
    List<java.util.Map<String, Object>> selectRevenueTrend(@Param("merchantId") Long merchantId, @Param("days") int days);

    /**
     * 按天统计退款趋势
     */
    @Select("SELECT DATE(refund_time) as date, " +
            "       COUNT(*) as refund_count, " +
            "       COALESCE(SUM(pay_amount), 0) as refund_amount " +
            "FROM order_info " +
            "WHERE merchant_id = #{merchantId} " +
            "  AND order_status = 4 " +
            "  AND DATE(refund_time) BETWEEN DATE_SUB(CURDATE(), INTERVAL #{days} DAY) AND CURDATE() " +
            "  AND deleted = 0 " +
            "GROUP BY DATE(refund_time) " +
            "ORDER BY DATE(refund_time)")
    List<java.util.Map<String, Object>> selectRefundTrend(@Param("merchantId") Long merchantId, @Param("days") int days);

    // ========== 区域统计 ==========

    /**
     * 按区域统计订单数和交易额 TOP5
     */
    @Select("SELECT s.province as name, " +
            "       COUNT(o.id) as orderCount, " +
            "       COALESCE(SUM(o.pay_amount), 0) as amount " +
            "FROM order_info o " +
            "JOIN store_info s ON o.store_id = s.id " +
            "WHERE o.deleted = 0 AND o.pay_status = 1 " +
            "  AND s.deleted = 0 " +
            "GROUP BY s.province " +
            "ORDER BY orderCount DESC " +
            "LIMIT 5")
    List<java.util.Map<String, Object>> selectRegionOrderStats();
}
