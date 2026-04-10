package com.yvliangbao.common.service.order;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.dto.order.OrderCreateDTO;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;


import java.util.List;

/**
 * 订单信息 Service 接口
 *
 * @author 余量宝
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    OrderInfo getByOrderNo(String orderNo);

    /**
     * 根据订单号查询订单（带用户验证）
     *
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 订单信息
     */
    OrderInfo getByOrderNo(String orderNo, Long userId);

    /**
     * 根据提货码查询订单
     *
     * @param pickupCode 提货码
     * @return 订单信息
     */
    OrderInfo getByPickupCode(String pickupCode);
    
    /**
     * 原子更新订单状态
     * 
     * @param orderNo 订单号
     * @param userId 用户ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @return 更新行数（0表示状态不匹配或订单不存在）
     */
    int updateStatus(String orderNo, Long userId, Integer oldStatus, Integer newStatus);
    
    /**
     * 创建订单
     *
     * @param dto 订单创建信息
     * @param userId 用户ID
     * @return 创建的订单
     */
    OrderInfo createOrder(OrderCreateDTO dto, Long userId);
    
    /**
     * 支付订单
     *
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 支付后的订单
     */
    OrderInfo payOrder(String orderNo, Long userId);
    
    /**
     * 核销订单
     *
     * @param pickupCode 提货码
     * @param merchantId 商户ID
     * @return 核销后的订单
     */
    OrderInfo verifyOrder(String pickupCode, Long merchantId);
    
    /**
     * 获取用户订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderInfo> getMyOrders(Long userId);
    
    /**
     * 获取用户订单列表（带筛选）
     *
     * @param userId 用户ID
     * @param status 订单状态（可选，-1表示全部，-2表示待取货）
     * @param keyword 搜索关键词（可选，支持订单号、店铺名或商品名模糊搜索）
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    List<OrderInfo> getMyOrders(Long userId, Integer status, String keyword, Integer page, Integer size);
    
    /**
     * 统计用户订单数量
     *
     * @param userId 用户ID
     * @param status 订单状态（可选，-1表示全部，-2表示待取货）
     * @param keyword 搜索关键词（可选）
     * @return 订单数量
     */
    long countMyOrders(Long userId, Integer status, String keyword);
    
    /**
     * 取消订单
     *
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 取消后的订单
     */
    OrderInfo cancelOrder(String orderNo, Long userId);

    /**
     * 备餐完成（已支付 → 待取餐）
     * 
     * 使用原子更新保证并发安全：
     * - 只有订单状态为"已支付"时才能更新
     * - 如果用户同时退款，更新会失败
     *
     * @param orderNo 订单号
     * @param merchantId 商户ID
     * @return 更新后的订单
     */
    OrderInfo markReady(String orderNo, Long merchantId);

    /**
     * 申请退款（已支付/待取餐 → 退款中）
     * 
     * 用户申请退款后，订单状态变为"退款中"，等待商户审核
     *
     * @param orderNo 订单号
     * @param userId 用户ID
     * @param refundReason 退款原因
     * @return 更新后的订单
     */
    OrderInfo applyRefund(String orderNo, Long userId, String refundReason);
    
    /**
     * 商户同意退款（退款中 → 已退款）
     * 
     * 商户审核通过后，订单状态变为"已退款"，并恢复库存
     *
     * @param orderNo 订单号
     * @param merchantId 商户ID
     * @return 更新后的订单
     */
    OrderInfo approveRefund(String orderNo, Long merchantId);
    
    /**
     * 商户拒绝退款（退款中 → 恢复原状态）
     * 
     * 商户审核拒绝后，订单状态恢复到原状态
     *
     * @param orderNo 订单号
     * @param merchantId 商户ID
     * @param rejectReason 拒绝原因
     * @return 更新后的订单
     */
    OrderInfo rejectRefund(String orderNo, Long merchantId, String rejectReason);
    
    /**
     * 处理超时退款申请
     * 
     * 系统自动批准超过24小时未处理的退款申请
     *
     * @return 处理的退款订单数量
     */
    int processTimeoutRefunds();
    
    /**
     * 获取商户订单列表
     *
     * @param merchantId 商户ID
     * @param status 订单状态（可选）
     * @param orderNo 订单号（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    List<OrderInfo> getMerchantOrders(Long merchantId, Integer status, String orderNo, Integer page, Integer size);
    
    /**
     * 获取商户订单总数
     *
     * @param merchantId 商户ID
     * @param status 订单状态（可选）
     * @param orderNo 订单号（可选）
     * @return 订单总数
     */
    long countMerchantOrders(Long merchantId, Integer status, String orderNo);
    
    /**
     * 获取商户今日已核销订单列表（核销流水）
     *
     * @param merchantId 商户ID
     * @return 今日已核销的订单列表（按完成时间倒序）
     */
    List<OrderInfo> getTodayVerifiedOrders(Long merchantId);

    /**
     * 获取商户统计数据
     *
     * @param merchantId 商户ID
     * @param storeCount 门店数量
     * @param productCount 在售商品数量
     * @return 商户统计数据
     */
    com.yvliangbao.common.pojo.vo.merchant.MerchantStatsVO getMerchantStats(Long merchantId, Integer storeCount, Integer productCount);

    /**
     * 获取商户营收趋势
     *
     * @param merchantId 商户ID
     * @param days 天数（默认7天）
     * @return 营收趋势数据
     */
    com.yvliangbao.common.pojo.vo.merchant.RevenueTrendVO getMerchantRevenueTrend(Long merchantId, Integer days);

    /**
     * 获取商户历史营收统计
     *
     * @param merchantId 商户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 营收数据
     */
    Long getMerchantRevenueByRange(Long merchantId, String startDate, String endDate);

    /**
     * 获取商户历史营收详情（包含订单数、退款等）
     *
     * @param merchantId 商户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 历史营收详情
     */
    com.yvliangbao.common.pojo.vo.merchant.RevenueHistoryVO getMerchantRevenueHistory(Long merchantId, String startDate, String endDate);

    /**
     * 管理员强制退款
     *
     * @param orderNo 订单号
     * @param reason 退款原因
     * @return 退款后的订单
     */
    OrderInfo adminForceRefund(String orderNo, String reason);

    /**
     * 管理员强制取消
     *
     * @param orderNo 订单号
     * @param reason 取消原因
     * @return 取消后的订单
     */
    OrderInfo adminForceCancel(String orderNo, String reason);

    /**
     * 更新订单状态为已支付
     * 供支付回调使用
     *
     * @param orderNo 订单号
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean updateOrderStatusToPaid(String orderNo, Long userId);
}
