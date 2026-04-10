package com.yvliangbao.common.service.impl.order;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.mapper.merchant.CapitalFlowMapper;
import com.yvliangbao.common.mapper.order.OrderInfoMapper;
import com.yvliangbao.common.pojo.dto.order.OrderCreateDTO;
import com.yvliangbao.common.pojo.entity.merchant.ProductInfo;
import com.yvliangbao.common.pojo.entity.merchant.StoreInfo;
import com.yvliangbao.common.pojo.entity.order.OrderInfo;
import com.yvliangbao.common.pojo.enums.OrderStatus;
import com.yvliangbao.common.pojo.enums.ProductStatus;
import com.yvliangbao.common.service.common.CacheService;
import com.yvliangbao.common.service.common.SettlementService;
import com.yvliangbao.common.service.merchant.InventoryInfoService;
import com.yvliangbao.common.service.merchant.ProductInfoService;
import com.yvliangbao.common.service.merchant.StoreInfoService;
import com.yvliangbao.common.service.message.SettlementProducer;
import com.yvliangbao.common.service.order.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订单信息 Service 实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private StoreInfoService storeInfoService;

    @Autowired
    private InventoryInfoService inventoryInfoService;

    @Autowired
    private CapitalFlowMapper capitalFlowMapper;

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private SettlementProducer settlementProducer;

    @Override
    public OrderInfo getByOrderNo(String orderNo) {
        log.debug("根据订单号查询订单: orderNo={}", orderNo);
        return this.lambdaQuery()
                .eq(OrderInfo::getOrderNo, orderNo)
                .one();
    }

    @Override
    public OrderInfo getByOrderNo(String orderNo, Long userId) {
        log.debug("根据订单号查询订单（带用户验证）: orderNo={}, userId={}", orderNo, userId);
        OrderInfo order = this.lambdaQuery()
                .eq(OrderInfo::getOrderNo, orderNo)
                .one();
        
        if (order != null && !userId.equals(order.getUserId())) {
            log.warn("无权查看该订单: orderNo={}, userId={}, orderUserId={}", 
                    orderNo, userId, order.getUserId());
            return null;
        }
        
        return order;
    }

    @Override
    public OrderInfo getByPickupCode(String pickupCode) {
        log.debug("根据提货码查询订单: pickupCode={}", pickupCode);
        return this.lambdaQuery()
                .eq(OrderInfo::getPickupCode, pickupCode)
                .one();
    }

    @Override
    public int updateStatus(String orderNo, Long userId, Integer oldStatus, Integer newStatus) {
        log.debug("原子更新订单状态: orderNo={}, userId={}, oldStatus={}, newStatus={}", 
                orderNo, userId, oldStatus, newStatus);
        
        int updatedRows = baseMapper.updateStatus(orderNo, userId, oldStatus, newStatus, LocalDateTime.now());
        
        if (updatedRows > 0) {
            log.info("订单状态更新成功: orderNo={}, oldStatus={}, newStatus={}", 
                    orderNo, oldStatus, newStatus);
        } else {
            log.warn("订单状态更新失败: orderNo={}, oldStatus={}, newStatus={}", 
                    orderNo, oldStatus, newStatus);
        }
        
        return updatedRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo createOrder(OrderCreateDTO dto, Long userId) {
        log.info("创建订单开始: userId={}, productId={}, quantity={}", 
                userId, dto.getProductId(), dto.getQuantity());
        
        try {
            // 1. 查询商品信息（验证商品状态）
            ProductInfo product = productInfoService.getById(dto.getProductId());
            
            if (product == null) {
                log.warn("创建订单失败: 商品不存在, productId={}", dto.getProductId());
                throw new BusinessException("商品不存在");
            }
            
            if (!ProductStatus.ONLINE.getCode().equals(product.getStatus())) {
                log.warn("创建订单失败: 商品已下架, productId={}, status={}", 
                        dto.getProductId(), product.getStatus());
                throw new BusinessException("商品已下架");
            }
            
            // 2. 生成订单号（先于库存锁定，用于日志关联）
            String orderNo = generateOrderNo();
            
            // 3. 锁定库存（预占机制）
            // 利用数据库行锁保证原子性：只有库存充足时才会锁定成功
            int lockedRows = productInfoService.lockStock(dto.getProductId(), dto.getQuantity(), orderNo);
            
            if (lockedRows == 0) {
                log.warn("创建订单失败: 库存不足, productId={}, quantity={}", 
                        dto.getProductId(), dto.getQuantity());
                throw new BusinessException("库存不足");
            }
            
            log.info("库存锁定成功: productId={}, quantity={}, orderNo={}", 
                    dto.getProductId(), dto.getQuantity(), orderNo);

            // 4. 查询门店信息（获取门店名称）
            StoreInfo store = storeInfoService.getById(product.getStoreId());
            if (store == null) {
                log.warn("创建订单失败: 门店不存在, storeId={}", product.getStoreId());
                throw new BusinessException("门店不存在");
            }

            // 5. 创建订单（复用已查询的商品信息）
            OrderInfo order = new OrderInfo();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setMerchantId(store.getMerchantId()); // 从门店获取商户ID
            order.setStoreId(product.getStoreId());
            order.setStoreName(store.getStoreName()); // 保存门店名称快照
            order.setProductId(product.getId());
            order.setProductName(product.getProductName());
            order.setProductImages(product.getProductImages());
            order.setQuantity(dto.getQuantity());
            order.setOriginalPrice(product.getOriginalPrice());
            order.setSalePrice(product.getSalePrice());
            order.setTotalAmount(product.getSalePrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
            order.setPayAmount(order.getTotalAmount());
            order.setPickupCode(generatePickupCode());
            order.setPickupTimeStart(product.getPickupTimeStart());
            order.setPickupTimeEnd(product.getPickupTimeEnd());
            order.setOrderStatus(OrderStatus.PENDING_PAYMENT.getCode()); // 待支付
            order.setPayStatus(0); // 未支付
            order.setExpireTime(LocalDateTime.now().plusMinutes(15)); // 15分钟后过期
            this.save(order);

            log.info("创建订单成功: orderNo={}, userId={}, amount={}", 
                    order.getOrderNo(), userId, order.getTotalAmount());
            return order;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建订单异常: userId={}, productId={}, error={}", 
                    userId, dto.getProductId(), e.getMessage(), e);
            throw new BusinessException("创建订单失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo payOrder(String orderNo, Long userId) {
        log.info("支付订单开始: orderNo={}, userId={}", orderNo, userId);
        
        try {
            // 1. 查询订单（验证订单存在）
            OrderInfo order = this.lambdaQuery()
                    .eq(OrderInfo::getOrderNo, orderNo)
                    .eq(OrderInfo::getUserId, userId)
                    .one();

            if (order == null) {
                log.warn("支付订单失败: 订单不存在, orderNo={}, userId={}", orderNo, userId);
                throw new BusinessException("订单不存在");
            }

            // 2. 原子更新订单状态（解决并发重复支付问题）
            // 利用数据库行锁保证原子性：只有订单状态为"待支付"时才会更新成功
            int updatedRows = this.updateStatus(orderNo, userId, 
                    OrderStatus.PENDING_PAYMENT.getCode(), OrderStatus.PAID.getCode());
            
            if (updatedRows == 0) {
                log.warn("支付订单失败: 订单状态异常或已支付, orderNo={}, currentStatus={}", 
                        orderNo, order.getOrderStatus());
                throw new BusinessException("订单状态异常或已支付");
            }

            // 3. 确认扣减库存（将锁定库存转为正式扣减）
            productInfoService.confirmDeduct(order.getProductId(), order.getQuantity(), orderNo);
            log.info("确认扣减库存成功: productId={}, quantity={}", order.getProductId(), order.getQuantity());

            log.info("支付订单成功: orderNo={}, userId={}", orderNo, userId);
            
            // 4. 返回更新后的订单
            order.setOrderStatus(OrderStatus.PAID.getCode());
            order.setPayStatus(1); // 已支付
            order.setPayTime(LocalDateTime.now());
            return order;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("支付订单异常: orderNo={}, userId={}, error={}", orderNo, userId, e.getMessage(), e);
            throw new BusinessException("支付订单失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo verifyOrder(String pickupCode, Long merchantId) {
        log.info("核销订单开始: pickupCode={}, merchantId={}", pickupCode, merchantId);
        
        try {
            // 1. 查询订单（验证订单存在）
            OrderInfo order = this.lambdaQuery()
                    .eq(OrderInfo::getPickupCode, pickupCode)
                    .one();

            if (order == null) {
                log.warn("核销订单失败: 提货码无效, pickupCode={}", pickupCode);
                throw new BusinessException("提货码无效");
            }

            // 2. 验证订单归属（防止跨商户核销）
            if (!merchantId.equals(order.getMerchantId())) {
                log.warn("核销订单失败: 无权核销其他商户的订单, pickupCode={}, merchantId={}, orderMerchantId={}", 
                        pickupCode, merchantId, order.getMerchantId());
                throw new BusinessException("无权核销该订单");
            }

            // 3. 原子更新订单状态（解决并发重复核销问题）
            // 支持两种场景：已支付(1)→已完成(3) 或 待取餐(2)→已完成(3)
            int updatedRows = baseMapper.updateStatusByPickupCode(
                    pickupCode, merchantId, OrderStatus.COMPLETED.getCode(), LocalDateTime.now());
            
            if (updatedRows == 0) {
                log.warn("核销订单失败: 订单状态异常或已核销, pickupCode={}, currentStatus={}", 
                        pickupCode, order.getOrderStatus());
                throw new BusinessException("订单状态异常或已核销");
            }

            log.info("核销订单成功: orderNo={}, pickupCode={}", order.getOrderNo(), pickupCode);

            // 异步发送分账消息，不阻塞核销主流程
            try {
                settlementProducer.sendSettlementMessage(
                        order.getId(),
                        order.getOrderNo(),
                        order.getMerchantId(),
                        order.getTotalAmount(),
                        order.getProductName() != null ? order.getProductName() : "商品",
                        order.getQuantity()
                );
                log.info("分账消息已发送: orderNo={}", order.getOrderNo());
            } catch (Exception e) {
                log.error("分账消息发送失败: orderNo={}, error={}", order.getOrderNo(), e.getMessage(), e);
            }

            // 清除缓存
            cacheService.deleteMerchantStatsCache(order.getMerchantId());

            // 6. 返回更新后的订单
            order.setOrderStatus(OrderStatus.COMPLETED.getCode());
            order.setFinishTime(LocalDateTime.now());
            return order;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("核销订单异常: pickupCode={}, merchantId={}, error={}", pickupCode, merchantId, e.getMessage(), e);
            throw new BusinessException("核销订单失败：" + e.getMessage());
        }
    }

    @Override
    public List<OrderInfo> getMyOrders(Long userId) {
        log.debug("获取用户订单列表: userId={}", userId);
        
        List<OrderInfo> orders = this.lambdaQuery()
                .eq(OrderInfo::getUserId, userId)
                .orderByDesc(OrderInfo::getCreateTime)
                .list();
        
        log.debug("获取用户订单列表完成: userId={}, count={}", userId, orders.size());
        
        return orders;
    }
    
    @Override
    public List<OrderInfo> getMyOrders(Long userId, Integer status, String keyword, Integer page, Integer size) {
        log.debug("获取用户订单列表（带筛选）: userId={}, status={}, keyword={}", userId, status, keyword);
        
        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderInfo> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        wrapper.eq(OrderInfo::getUserId, userId);
        
        // 按状态筛选（-1表示全部，-2表示待取货=已支付+待取餐）
        if (status != null && status >= 0) {
            wrapper.eq(OrderInfo::getOrderStatus, status);
        } else if (status != null && status == -2) {
            // 待取货：已支付(1) 或 待取餐(2)
            wrapper.in(OrderInfo::getOrderStatus, 1, 2);
        }
        
        // 按关键词搜索（支持订单号、店铺名或商品名）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w
                    .like(OrderInfo::getOrderNo, kw)
                    .or()
                    .like(OrderInfo::getStoreName, kw)
                    .or()
                    .like(OrderInfo::getProductName, kw)
            );
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        
        // 分页
        int offset = (page - 1) * size;
        wrapper.last("LIMIT " + size + " OFFSET " + offset);
        
        List<OrderInfo> orders = this.list(wrapper);
        
        log.debug("获取用户订单列表完成: userId={}, count={}", userId, orders.size());
        return orders;
    }
    
    @Override
    public long countMyOrders(Long userId, Integer status, String keyword) {
        log.debug("统计用户订单数量: userId={}, status={}, keyword={}", userId, status, keyword);
        
        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderInfo> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        wrapper.eq(OrderInfo::getUserId, userId);
        
        // 按状态筛选（-1表示全部，-2表示待取货=已支付+待取餐）
        if (status != null && status >= 0) {
            wrapper.eq(OrderInfo::getOrderStatus, status);
        } else if (status != null && status == -2) {
            // 待取货：已支付(1) 或 待取餐(2)
            wrapper.in(OrderInfo::getOrderStatus, 1, 2);
        }
        
        // 按关键词搜索（支持订单号、店铺名或商品名）
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w
                    .like(OrderInfo::getOrderNo, kw)
                    .or()
                    .like(OrderInfo::getStoreName, kw)
                    .or()
                    .like(OrderInfo::getProductName, kw)
            );
        }
        
        return this.count(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo cancelOrder(String orderNo, Long userId) {
        log.info("取消订单开始: orderNo={}, userId={}", orderNo, userId);
        
        try {
            // 1. 查询订单（验证订单存在及归属）
            OrderInfo order = this.lambdaQuery()
                    .eq(OrderInfo::getOrderNo, orderNo)
                    .eq(OrderInfo::getUserId, userId)
                    .one();

            if (order == null) {
                log.warn("取消订单失败: 订单不存在, orderNo={}, userId={}", orderNo, userId);
                throw new BusinessException("订单不存在");
            }

            // 2. 验证订单状态（只有待支付状态才能取消）
            if (!OrderStatus.PENDING_PAYMENT.getCode().equals(order.getOrderStatus())) {
                log.warn("取消订单失败: 订单状态不允许取消, orderNo={}, status={}", 
                        orderNo, order.getOrderStatus());
                throw new BusinessException("当前订单状态不允许取消");
            }

            // 3. 原子更新订单状态为已取消
            int updatedRows = this.updateStatus(orderNo, userId,
                    OrderStatus.PENDING_PAYMENT.getCode(), OrderStatus.CANCELED.getCode());
            
            if (updatedRows == 0) {
                log.warn("取消订单失败: 订单状态已变更, orderNo={}", orderNo);
                throw new BusinessException("订单状态已变更，请刷新后重试");
            }

            // 4. 释放锁定的库存（幂等性由 releaseStock 方法保证）
            productInfoService.releaseStock(order.getProductId(), order.getQuantity(), orderNo);
            log.info("库存释放成功: productId={}, quantity={}", order.getProductId(), order.getQuantity());

            log.info("取消订单成功: orderNo={}, userId={}", orderNo, userId);
            
            // 5. 返回更新后的订单
            order.setOrderStatus(OrderStatus.CANCELED.getCode());
            order.setCancelTime(LocalDateTime.now());
            order.setCancelReason("用户取消");
            return order;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("取消订单异常: orderNo={}, userId={}, error={}", orderNo, userId, e.getMessage(), e);
            throw new BusinessException("取消订单失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo markReady(String orderNo, Long merchantId) {
        log.info("备餐完成开始: orderNo={}, merchantId={}", orderNo, merchantId);
        
        try {
            // 1. 查询订单（验证订单存在）
            OrderInfo order = this.lambdaQuery()
                    .eq(OrderInfo::getOrderNo, orderNo)
                    .one();

            if (order == null) {
                log.warn("备餐完成失败: 订单不存在, orderNo={}", orderNo);
                throw new BusinessException("订单不存在");
            }

            // 2. 验证订单归属（只有订单所属商户才能操作）
            if (!merchantId.equals(order.getMerchantId())) {
                log.warn("备餐完成失败: 无权操作其他商户的订单, orderNo={}, merchantId={}, orderMerchantId={}", 
                        orderNo, merchantId, order.getMerchantId());
                throw new BusinessException("无权操作该订单");
            }

            // 3. 原子更新订单状态（核心：解决并发问题）
            // 只有状态为"已支付(1)"时才能更新为"待取餐(2)"
            // 如果用户同时退款（状态变为5），则更新返回0
            int updatedRows = baseMapper.updateStatusToReady(
                    orderNo, merchantId,
                    OrderStatus.PAID.getCode(),      // 原状态：已支付
                    OrderStatus.PENDING_PICKUP.getCode(), // 新状态：待取餐
                    LocalDateTime.now());

            if (updatedRows == 0) {
                // 更新失败，说明订单状态已变更（可能用户已退款）
                log.warn("备餐完成失败: 订单状态已变更, orderNo={}, currentStatus={}", 
                        orderNo, order.getOrderStatus());
                throw new BusinessException("订单状态已变更，请刷新后重试");
            }

            log.info("备餐完成成功: orderNo={}, merchantId={}", orderNo, merchantId);
            
            // 4. 返回更新后的订单
            order.setOrderStatus(OrderStatus.PENDING_PICKUP.getCode());
            return order;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("备餐完成异常: orderNo={}, merchantId={}, error={}", orderNo, merchantId, e.getMessage(), e);
            throw new BusinessException("备餐完成失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo applyRefund(String orderNo, Long userId, String refundReason) {
        log.info("用户申请退款开始: orderNo={}, userId={}", orderNo, userId);
        
        try {
            // 1. 查询订单（验证订单存在及归属）
            OrderInfo order = this.lambdaQuery()
                    .eq(OrderInfo::getOrderNo, orderNo)
                    .eq(OrderInfo::getUserId, userId)
                    .one();

            if (order == null) {
                log.warn("申请退款失败: 订单不存在, orderNo={}, userId={}", orderNo, userId);
                throw new BusinessException("订单不存在");
            }

            // 2. 验证订单状态（只有已支付或待取餐状态才能申请退款）
            if (!OrderStatus.PAID.getCode().equals(order.getOrderStatus()) 
                    && !OrderStatus.PENDING_PICKUP.getCode().equals(order.getOrderStatus())) {
                log.warn("申请退款失败: 订单状态不允许退款, orderNo={}, status={}", 
                        orderNo, order.getOrderStatus());
                throw new BusinessException("当前订单状态不允许退款");
            }

            // 3. 原子更新订单状态为退款中（记录退款申请时间）
            LocalDateTime refundApplyTime = LocalDateTime.now();
            int updatedRows = baseMapper.updateStatusToRefunding(orderNo, userId, refundReason, refundApplyTime);

            if (updatedRows == 0) {
                log.warn("申请退款失败: 订单状态已变更, orderNo={}, currentStatus={}", 
                        orderNo, order.getOrderStatus());
                throw new BusinessException("订单状态已变更，请刷新后重试");
            }

            log.info("用户申请退款成功: orderNo={}, userId={}, 等待商户审核", orderNo, userId);
            
            // 4. 返回更新后的订单
            order.setOrderStatus(OrderStatus.REFUNDING.getCode());
            order.setRefundReason(refundReason);
            order.setRefundApplyTime(refundApplyTime);
            return order;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("申请退款异常: orderNo={}, userId={}, error={}", orderNo, userId, e.getMessage(), e);
            throw new BusinessException("申请退款失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo approveRefund(String orderNo, Long merchantId) {
        log.info("商户同意退款开始: orderNo={}, merchantId={}", orderNo, merchantId);
        
        try {
            // 1. 查询订单
            OrderInfo order = this.lambdaQuery()
                    .eq(OrderInfo::getOrderNo, orderNo)
                    .one();

            if (order == null) {
                log.warn("商户同意退款失败: 订单不存在, orderNo={}", orderNo);
                throw new BusinessException("订单不存在");
            }

            // 2. 验证订单归属
            if (!merchantId.equals(order.getMerchantId())) {
                log.warn("商户同意退款失败: 无权操作其他商户的订单, orderNo={}, merchantId={}", orderNo, merchantId);
                throw new BusinessException("无权操作该订单");
            }

            // 3. 验证订单状态（只有退款中状态才能同意退款）
            if (!OrderStatus.REFUNDING.getCode().equals(order.getOrderStatus())) {
                log.warn("商户同意退款失败: 订单状态异常, orderNo={}, status={}", orderNo, order.getOrderStatus());
                throw new BusinessException("订单状态异常");
            }

            // 4. 原子更新订单状态为已退款
            int updatedRows = baseMapper.updateStatusToRefunded(
                    orderNo, merchantId, OrderStatus.REFUNDED.getCode(), LocalDateTime.now());

            if (updatedRows == 0) {
                log.warn("商户同意退款失败: 订单状态已变更, orderNo={}", orderNo);
                throw new BusinessException("订单状态已变更，请刷新后重试");
            }

            // 5. 增加库存（退款恢复）
            productInfoService.increaseStock(order.getProductId(), order.getQuantity(), orderNo);
            log.info("库存恢复成功: productId={}, quantity={}", order.getProductId(), order.getQuantity());

            // 注：退款由支付机构直接退给用户，不涉及商户余额变动
            // 资金在核销时才从支付机构分账到商户账户

            log.info("商户同意退款成功: orderNo={}, merchantId={}", orderNo, merchantId);
            
            // 6. 返回更新后的订单
            order.setOrderStatus(OrderStatus.REFUNDED.getCode());
            order.setPayStatus(3); // 全额退款
            order.setRefundTime(LocalDateTime.now());
            return order;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("商户同意退款异常: orderNo={}, merchantId={}, error={}", orderNo, merchantId, e.getMessage(), e);
            throw new BusinessException("同意退款失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo rejectRefund(String orderNo, Long merchantId, String rejectReason) {
        log.info("商户拒绝退款开始: orderNo={}, merchantId={}", orderNo, merchantId);
        
        try {
            // 1. 查询订单
            OrderInfo order = this.lambdaQuery()
                    .eq(OrderInfo::getOrderNo, orderNo)
                    .one();

            if (order == null) {
                log.warn("商户拒绝退款失败: 订单不存在, orderNo={}", orderNo);
                throw new BusinessException("订单不存在");
            }

            // 2. 验证订单归属
            if (!merchantId.equals(order.getMerchantId())) {
                log.warn("商户拒绝退款失败: 无权操作其他商户的订单, orderNo={}, merchantId={}", orderNo, merchantId);
                throw new BusinessException("无权操作该订单");
            }

            // 3. 验证订单状态（只有退款中状态才能拒绝退款）
            if (!OrderStatus.REFUNDING.getCode().equals(order.getOrderStatus())) {
                log.warn("商户拒绝退款失败: 订单状态异常, orderNo={}, status={}", orderNo, order.getOrderStatus());
                throw new BusinessException("订单状态异常");
            }

            // 4. 恢复到已支付状态（无法判断原状态，统一恢复到已支付）
            Integer originalStatus = OrderStatus.PAID.getCode();
            
            int updatedRows = baseMapper.updateStatusRejectRefund(orderNo, merchantId, originalStatus);

            if (updatedRows == 0) {
                log.warn("商户拒绝退款失败: 订单状态已变更, orderNo={}", orderNo);
                throw new BusinessException("订单状态已变更，请刷新后重试");
            }

            log.info("商户拒绝退款成功: orderNo={}, merchantId={}, 恢复状态={}", orderNo, merchantId, originalStatus);
            
            // 5. 返回更新后的订单
            order.setOrderStatus(originalStatus);
            order.setRefundReason(null);
            return order;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("商户拒绝退款异常: orderNo={}, merchantId={}, error={}", orderNo, merchantId, e.getMessage(), e);
            throw new BusinessException("拒绝退款失败：" + e.getMessage());
        }
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        return "YL" + timestamp + random;
    }

    /**
     * 生成提货码
     */
    private String generatePickupCode() {
        return String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }
    
    @Override
    public List<OrderInfo> getMerchantOrders(Long merchantId, Integer status, String orderNo, Integer page, Integer size) {
        log.debug("获取商户订单列表: merchantId={}, status={}, orderNo={}", merchantId, status, orderNo);
        
        try {
            List<Long> storeIds = getStoreIdsByMerchantId(merchantId);
            if (storeIds.isEmpty()) {
                log.debug("商户无门店: merchantId={}", merchantId);
                return Collections.emptyList();
            }
            
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderInfo> wrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            
            wrapper.in(OrderInfo::getStoreId, storeIds);
            
            if (status != null) {
                wrapper.eq(OrderInfo::getOrderStatus, status);
            }
            
            if (orderNo != null && !orderNo.trim().isEmpty()) {
                wrapper.like(OrderInfo::getOrderNo, orderNo.trim());
            }
            
            wrapper.orderByDesc(OrderInfo::getCreateTime);
            
            int offset = (page - 1) * size;
            wrapper.last("LIMIT " + size + " OFFSET " + offset);
            
            List<OrderInfo> orders = this.list(wrapper);
            
            log.debug("获取商户订单列表完成: merchantId={}, count={}", merchantId, orders.size());
            return orders;
            
        } catch (Exception e) {
            log.error("获取商户订单列表异常: merchantId={}, error={}", merchantId, e.getMessage(), e);
            throw new BusinessException("获取订单列表失败：" + e.getMessage());
        }
    }
    
    @Override
    public long countMerchantOrders(Long merchantId, Integer status, String orderNo) {
        log.debug("统计商户订单数量: merchantId={}, status={}, orderNo={}", merchantId, status, orderNo);
        
        try {
            List<Long> storeIds = getStoreIdsByMerchantId(merchantId);
            if (storeIds.isEmpty()) {
                return 0;
            }
            
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderInfo> wrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            
            wrapper.in(OrderInfo::getStoreId, storeIds);
            
            if (status != null) {
                wrapper.eq(OrderInfo::getOrderStatus, status);
            }
            
            if (orderNo != null && !orderNo.trim().isEmpty()) {
                wrapper.like(OrderInfo::getOrderNo, orderNo.trim());
            }
            
            return this.count(wrapper);
            
        } catch (Exception e) {
            log.error("统计商户订单数量异常: merchantId={}, error={}", merchantId, e.getMessage(), e);
            return 0;
        }
    }
    
    private List<Long> getStoreIdsByMerchantId(Long merchantId) {
        List<StoreInfo> stores = storeInfoService.listByMerchantId(merchantId);
        if (stores.isEmpty()) {
            return Collections.emptyList();
        }
        return stores.stream().map(StoreInfo::getId).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int processTimeoutRefunds() {
        log.info("开始处理超时退款申请");
        
        try {
            // 1. 查询超过24小时未处理的退款申请
            List<OrderInfo> timeoutOrders = baseMapper.selectTimeoutRefundingOrders(24);
            
            if (timeoutOrders.isEmpty()) {
                log.debug("没有超时的退款申请");
                return 0;
            }
            
            log.info("发现{}笔超时退款申请", timeoutOrders.size());
            
            int successCount = 0;
            
            // 2. 逐笔处理自动退款
            for (OrderInfo order : timeoutOrders) {
                try {
                    // 原子更新订单状态为已退款
                    int updatedRows = baseMapper.autoRefund(
                            order.getOrderNo(), 
                            OrderStatus.REFUNDED.getCode(), 
                            LocalDateTime.now());
                    
                    if (updatedRows > 0) {
                        // 增加库存（退款恢复）
                        productInfoService.increaseStock(order.getProductId(), order.getQuantity(), order.getOrderNo());
                        log.info("自动退款成功: orderNo={}, productId={}, quantity={}", 
                                order.getOrderNo(), order.getProductId(), order.getQuantity());
                        successCount++;
                    } else {
                        log.warn("自动退款失败（状态已变更）: orderNo={}", order.getOrderNo());
                    }
                } catch (Exception e) {
                    log.error("自动退款异常: orderNo={}, error={}", order.getOrderNo(), e.getMessage(), e);
                }
            }
            
            log.info("超时退款处理完成: 总数={}, 成功={}", timeoutOrders.size(), successCount);
            return successCount;
            
        } catch (Exception e) {
            log.error("处理超时退款申请异常: error={}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public List<OrderInfo> getTodayVerifiedOrders(Long merchantId) {
        log.debug("获取商户今日已核销订单列表: merchantId={}", merchantId);
        
        try {
            List<Long> storeIds = getStoreIdsByMerchantId(merchantId);
            if (storeIds.isEmpty()) {
                log.debug("商户无门店: merchantId={}", merchantId);
                return Collections.emptyList();
            }
            
            List<OrderInfo> orders = baseMapper.selectTodayVerifiedOrders(storeIds);
            
            log.debug("获取商户今日已核销订单完成: merchantId={}, count={}", merchantId, orders.size());
            return orders;
            
        } catch (Exception e) {
            log.error("获取商户今日已核销订单异常: merchantId={}, error={}", merchantId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public com.yvliangbao.common.pojo.vo.merchant.MerchantStatsVO getMerchantStats(Long merchantId, Integer storeCount, Integer productCount) {
        log.debug("获取商户统计数据: merchantId={}", merchantId);
        
        com.yvliangbao.common.pojo.vo.merchant.MerchantStatsVO stats = new com.yvliangbao.common.pojo.vo.merchant.MerchantStatsVO();
        
        // 今日数据
        stats.setTodayOrders(baseMapper.countMerchantTodayOrders(merchantId));
        stats.setTodayRevenue(baseMapper.sumMerchantTodayRevenue(merchantId));
        stats.setTodayVerified(baseMapper.countMerchantTodayVerified(merchantId));
        stats.setTodayRefund(baseMapper.sumMerchantTodayRefund(merchantId));
        
        // 昨日数据
        stats.setYesterdayOrders(baseMapper.countMerchantYesterdayOrders(merchantId));
        stats.setYesterdayRevenue(baseMapper.sumMerchantYesterdayRevenue(merchantId));
        stats.setYesterdayVerified(baseMapper.countMerchantYesterdayVerified(merchantId));
        
        // 本月数据
        stats.setMonthOrders(baseMapper.countMerchantMonthOrders(merchantId));
        stats.setMonthRevenue(baseMapper.sumMerchantMonthRevenue(merchantId));
        stats.setMonthVerified(baseMapper.countMerchantMonthVerified(merchantId));
        stats.setMonthRefund(baseMapper.sumMerchantMonthRefund(merchantId));
        
        // 累计数据
        stats.setTotalOrders(baseMapper.countMerchantTotalOrders(merchantId));
        stats.setTotalRevenue(baseMapper.sumMerchantTotalRevenue(merchantId));
        stats.setTotalRefund(baseMapper.sumMerchantTotalRefund(merchantId));
        stats.setTotalSaved(baseMapper.sumMerchantTotalSaved(merchantId));
        
        // 待处理
        stats.setPendingVerify(baseMapper.countMerchantPendingVerify(merchantId));
        stats.setRefunding(baseMapper.countMerchantRefunding(merchantId));
        
        // 门店统计
        stats.setStoreCount(storeCount);
        stats.setProductCount(productCount);
        
        // 计算环比增长率
        stats.setOrderGrowthRate(calculateGrowthRate(stats.getTodayOrders(), stats.getYesterdayOrders()));
        stats.setRevenueGrowthRate(calculateGrowthRate(stats.getTodayRevenue(), stats.getYesterdayRevenue()));
        stats.setVerifiedGrowthRate(calculateGrowthRate(stats.getTodayVerified(), stats.getYesterdayVerified()));
        
        return stats;
    }
    
    /**
     * 计算环比增长率
     * @param today 今日值
     * @param yesterday 昨日值
     * @return 增长率百分比（正数表示增长，负数表示下降）
     */
    private BigDecimal calculateGrowthRate(Number today, Number yesterday) {
        if (today == null || yesterday == null) {
            return BigDecimal.ZERO;
        }
        
        double todayVal = today.doubleValue();
        double yesterdayVal = yesterday.doubleValue();
        
        if (yesterdayVal == 0) {
            return todayVal > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
        }
        
        double rate = ((todayVal - yesterdayVal) / yesterdayVal) * 100;
        return BigDecimal.valueOf(rate).setScale(1, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public com.yvliangbao.common.pojo.vo.merchant.RevenueTrendVO getMerchantRevenueTrend(Long merchantId, Integer days) {
        log.debug("获取商户营收趋势: merchantId={}, days={}", merchantId, days);
        
        if (days == null || days <= 0) {
            days = 7;
        }
        
        com.yvliangbao.common.pojo.vo.merchant.RevenueTrendVO trend = new com.yvliangbao.common.pojo.vo.merchant.RevenueTrendVO();
        
        List<String> dates = new java.util.ArrayList<>();
        List<Long> revenues = new java.util.ArrayList<>();
        List<Integer> orders = new java.util.ArrayList<>();
        List<Long> refunds = new java.util.ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = days - 1; i >= 0; i--) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            String dateStr = date.format(formatter);
            String sqlDate = date.toString();
            
            dates.add(dateStr);
            
            // 查询当日营收
            Long revenue = baseMapper.sumMerchantRevenueByRange(merchantId, sqlDate, sqlDate);
            revenues.add(revenue != null ? revenue : 0L);
            
            // 查询当日订单数
            int orderCount = baseMapper.countMerchantOrdersByRange(merchantId, sqlDate, sqlDate);
            orders.add(orderCount);
            
            // 查询当日退款
            Long refund = baseMapper.sumMerchantRefundByRange(merchantId, sqlDate, sqlDate);
            refunds.add(refund != null ? refund : 0L);
        }
        
        trend.setDates(dates);
        trend.setRevenues(revenues);
        trend.setOrders(orders);
        trend.setRefunds(refunds);
        
        return trend;
    }

    @Override
    public Long getMerchantRevenueByRange(Long merchantId, String startDate, String endDate) {
        log.debug("获取商户历史营收: merchantId={}, startDate={}, endDate={}", merchantId, startDate, endDate);
        return baseMapper.sumMerchantRevenueByRange(merchantId, startDate, endDate);
    }

    @Override
    public com.yvliangbao.common.pojo.vo.merchant.RevenueHistoryVO getMerchantRevenueHistory(Long merchantId, String startDate, String endDate) {
        log.debug("获取商户历史营收详情: merchantId={}, startDate={}, endDate={}", merchantId, startDate, endDate);
        
        com.yvliangbao.common.pojo.vo.merchant.RevenueHistoryVO history = new com.yvliangbao.common.pojo.vo.merchant.RevenueHistoryVO();
        
        // 查询营收
        Long revenue = baseMapper.sumMerchantRevenueByRange(merchantId, startDate, endDate);
        history.setRevenue(revenue != null ? revenue : 0L);
        
        // 查询订单数
        int orders = baseMapper.countMerchantOrdersByRange(merchantId, startDate, endDate);
        history.setOrders(orders);
        
        // 查询退款
        Long refund = baseMapper.sumMerchantRefundByRange(merchantId, startDate, endDate);
        history.setRefund(refund != null ? refund : 0L);
        
        // 设置日期
        history.setStartDate(startDate);
        history.setEndDate(endDate);
        
        return history;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo adminForceRefund(String orderNo, String reason) {
        log.info("管理员强制退款: orderNo={}, reason={}", orderNo, reason);
        
        OrderInfo order = this.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 执行退款
        int rows = baseMapper.adminForceRefund(orderNo, reason);
        if (rows <= 0) {
            throw new BusinessException("退款失败，订单状态不允许退款");
        }
        
        // 恢复库存
        if (order.getProductId() != null && order.getQuantity() != null) {
            inventoryInfoService.increaseStock(order.getProductId(), order.getQuantity(), orderNo);
        }
        
        log.info("管理员强制退款成功: orderNo={}", orderNo);
        return this.getByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo adminForceCancel(String orderNo, String reason) {
        log.info("管理员强制取消: orderNo={}, reason={}", orderNo, reason);
        
        OrderInfo order = this.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        // 执行取消
        int rows = baseMapper.adminForceCancel(orderNo, reason);
        if (rows <= 0) {
            throw new BusinessException("取消失败，订单状态不允许取消");
        }
        
        log.info("管理员强制取消成功: orderNo={}", orderNo);
        return this.getByOrderNo(orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatusToPaid(String orderNo, Long userId) {
        log.info("支付成功回调：更新订单状态为已支付: orderNo={}, userId={}", orderNo, userId);
        
        try {
            // 1. 原子更新订单状态（待支付 -> 已支付）
            int updatedRows = this.updateStatus(orderNo, userId,
                    OrderStatus.PENDING_PAYMENT.getCode(), OrderStatus.PAID.getCode());
            
            if (updatedRows == 0) {
                log.warn("订单状态更新失败（可能已支付或已取消）: orderNo={}", orderNo);
                return false;
            }
            
            // 2. 查询更新后的订单
            OrderInfo order = this.getByOrderNo(orderNo);
            if (order == null) {
                log.error("订单不存在: orderNo={}", orderNo);
                return false;
            }
            
            // 3. 生成取货码
            String pickupCode = generatePickupCode();
            order.setPickupCode(pickupCode);
            order.setPayTime(LocalDateTime.now());
            this.updateById(order);
            
            log.info("订单支付成功: orderNo={}, pickupCode={}", orderNo, pickupCode);
            return true;
            
        } catch (Exception e) {
            log.error("更新订单状态异常: orderNo={}, error={}", orderNo, e.getMessage(), e);
            return false;
        }
    }
}
