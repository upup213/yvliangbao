package com.yvliangbao.common.service.merchant;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.entity.merchant.InventoryInfo;


/**
 * 库存信息 Service
 *
 * 提供库存管理功能：
 * 1. 库存预占机制（锁定 → 确认/释放）
 * 2. 库存变更日志记录
 * 3. 并发安全保障（数据库行锁）
 *
 * 业务流程：
 * - 下单：lockStock() 锁定库存
 * - 支付成功：confirmDeduct() 确认扣减
 * - 取消订单/超时：releaseStock() 释放库存
 *
 * @author 余量宝
 */
public interface InventoryInfoService extends IService<InventoryInfo> {

    /**
     * 锁定库存（预占）
     *
     * 下单时调用，将可用库存转移到锁定库存
     * 并发安全：利用数据库行锁保证原子性
     *
     * @param productId 商品ID
     * @param quantity 锁定数量
     * @param orderNo 关联订单号（用于日志）
     * @return 更新行数（0=库存不足，1=成功）
     */
    int lockStock(Long productId, Integer quantity, String orderNo);

    /**
     * 确认扣减库存
     *
     * 支付成功后调用，将锁定库存正式扣减
     *
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @param orderNo 关联订单号（用于日志）
     * @return 更新行数（0=失败，1=成功）
     */
    int confirmDeduct(Long productId, Integer quantity, String orderNo);

    /**
     * 释放锁定库存
     *
     * 取消订单或订单超时时调用
     *
     * @param productId 商品ID
     * @param quantity 释放数量
     * @param orderNo 关联订单号（用于日志）
     * @return 更新行数（0=失败，1=成功）
     */
    int releaseStock(Long productId, Integer quantity, String orderNo);

    /**
     * 原子扣减库存（已废弃，保留兼容）
     *
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 更新行数（0=库存不足，1=成功）
     * @deprecated 使用 lockStock + confirmDeduct 替代
     */
    @Deprecated
    int reduceStock(Long productId, Integer quantity);

    /**
     * 恢复库存（已废弃，保留兼容）
     *
     * @param productId 商品ID
     * @param quantity 恢复数量
     * @return 更新行数
     * @deprecated 使用 releaseStock 替代
     */
    @Deprecated
    int restoreStock(Long productId, Integer quantity);

    /**
     * 根据商品ID查询库存
     */
    InventoryInfo getByProductId(Long productId);

    /**
     * 初始化库存
     */
    void initInventory(Long productId, Integer totalStock);

    /**
     * 更新总库存
     */
    void updateTotalStock(Long productId, Integer totalStock);

    /**
     * 增加库存（退款时使用）
     *
     * 退款成功后调用，将库存返还到可用库存
     *
     * @param productId 商品ID
     * @param quantity 增加数量
     * @param orderNo 关联订单号（用于日志）
     * @return 更新行数
     */
    int increaseStock(Long productId, Integer quantity, String orderNo);
}
