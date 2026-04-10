package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.InventoryInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 库存信息 Mapper
 *
 * 提供库存的原子操作：
 * - lockStock: 锁定库存（预占）
 * - confirmDeduct: 确认扣减（支付成功后）
 * - releaseStock: 释放库存（取消订单/超时）
 * - reduceStock: 直接扣减（已废弃，保留兼容）
 *
 * TODO: 当前 version 字段仅作审计用途（记录更新次数），未用于乐观锁校验。
 *       未来如需提升高并发性能，可改为真正的乐观锁实现：
 *       WHERE ... AND version = #{oldVersion}，配合重试机制使用。
 *
 * @author 余量宝
 */
@Mapper
public interface InventoryInfoMapper extends BaseMapper<InventoryInfo> {
    
    /**
     * 锁定库存（预占）
     * 
     * 下单时调用，将可用库存转移到锁定库存
     * 利用数据库行锁保证原子性：
     * - 只有可用库存充足时才会锁定成功
     * - 返回更新行数，0表示库存不足
     * 
     * @param productId 商品ID
     * @param quantity 锁定数量
     * @return 更新行数（0=库存不足，1=成功）
     */
    @Update("UPDATE inventory_info " +
            "SET available_stock = available_stock - #{quantity}, " +
            "    locked_stock = locked_stock + #{quantity}, " +
            "    version = version + 1, " +
            "    update_time = NOW() " +
            "WHERE product_id = #{productId} " +
            "  AND available_stock >= #{quantity}")
    int lockStock(@Param("productId") Long productId, 
                  @Param("quantity") Integer quantity);
    
    /**
     * 确认扣减库存
     * 
     * 支付成功后调用，将锁定库存正式扣减
     * 利用数据库行锁保证原子性
     * 
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 更新行数（0=锁定库存不足，1=成功）
     */
    @Update("UPDATE inventory_info " +
            "SET locked_stock = locked_stock - #{quantity}, " +
            "    version = version + 1, " +
            "    update_time = NOW() " +
            "WHERE product_id = #{productId} " +
            "  AND locked_stock >= #{quantity}")
    int confirmDeduct(@Param("productId") Long productId, 
                      @Param("quantity") Integer quantity);
    
    /**
     * 释放锁定库存
     * 
     * 取消订单或订单超时时调用，将锁定库存返还到可用库存
     * 利用数据库行锁保证原子性
     * 
     * @param productId 商品ID
     * @param quantity 释放数量
     * @return 更新行数（0=锁定库存不足，1=成功）
     */
    @Update("UPDATE inventory_info " +
            "SET locked_stock = locked_stock - #{quantity}, " +
            "    available_stock = available_stock + #{quantity}, " +
            "    version = version + 1, " +
            "    update_time = NOW() " +
            "WHERE product_id = #{productId} " +
            "  AND locked_stock >= #{quantity}")
    int releaseStock(@Param("productId") Long productId, 
                     @Param("quantity") Integer quantity);
    
    /**
     * 直接扣减库存（已废弃，保留兼容旧代码）
     * 
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 更新行数（0表示库存不足）
     * @deprecated 使用 lockStock + confirmDeduct 替代
     */
    @Deprecated
    @Update("UPDATE inventory_info " +
            "SET available_stock = available_stock - #{quantity}, " +
            "    version = version + 1, " +
            "    update_time = NOW() " +
            "WHERE product_id = #{productId} " +
            "  AND available_stock >= #{quantity}")
    int reduceStock(@Param("productId") Long productId, 
                    @Param("quantity") Integer quantity);
    
    /**
     * 直接恢复库存（已废弃，保留兼容旧代码）
     * 
     * @param productId 商品ID
     * @param quantity 恢复数量
     * @return 更新行数
     * @deprecated 使用 releaseStock 替代
     */
    @Deprecated
    @Update("UPDATE inventory_info " +
            "SET available_stock = available_stock + #{quantity}, " +
            "    version = version + 1, " +
            "    update_time = NOW() " +
            "WHERE product_id = #{productId}")
    int restoreStock(@Param("productId") Long productId, 
                     @Param("quantity") Integer quantity);
    
    /**
     * 查询商品库存（用于日志记录）
     */
    @Select("SELECT * FROM inventory_info WHERE product_id = #{productId}")
    InventoryInfo selectByProductId(@Param("productId") Long productId);
    
    /**
     * 增加库存（退款时使用）
     * 
     * 退款成功后调用，将库存返还到可用库存
     * 
     * @param productId 商品ID
     * @param quantity 增加数量
     * @return 更新行数
     */
    @Update("UPDATE inventory_info " +
            "SET available_stock = available_stock + #{quantity}, " +
            "    version = version + 1, " +
            "    update_time = NOW() " +
            "WHERE product_id = #{productId}")
    int increaseStock(@Param("productId") Long productId, 
                      @Param("quantity") Integer quantity);
}
