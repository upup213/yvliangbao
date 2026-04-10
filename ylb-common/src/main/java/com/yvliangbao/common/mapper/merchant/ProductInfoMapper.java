package com.yvliangbao.common.mapper.merchant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.merchant.ProductInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品信息 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface ProductInfoMapper extends BaseMapper<ProductInfo> {
    
    /**
     * 原子扣减库存
     * 
     * 利用数据库行锁保证原子性：
     * - 只有库存充足时才会更新成功
     * - 返回更新行数，0表示库存不足
     * 
     * @param productId 商品ID
     * @param quantity 购买数量
     * @return 更新行数（0表示库存不足，1表示成功）
     */
    @Update("UPDATE product_info " +
            "SET remaining_stock = remaining_stock - #{quantity}, " +
            "    update_time = NOW() " +
            "WHERE id = #{productId} " +
            "  AND remaining_stock >= #{quantity} " +
            "  AND status = 1 " +   // ProductStatus.ONLINE
            "  AND deleted = 0")
    int reduceStock(@Param("productId") Long productId, 
                    @Param("quantity") Integer quantity);
    
    /**
     * 恢复库存（取消订单时调用）
     * 
     * @param productId 商品ID
     * @param quantity 恢复数量
     * @return 更新行数
     */
    @Update("UPDATE product_info " +
            "SET remaining_stock = remaining_stock + #{quantity}, " +
            "    update_time = NOW() " +
            "WHERE id = #{productId} " +
            "  AND deleted = 0")
    int restoreStock(@Param("productId") Long productId, 
                     @Param("quantity") Integer quantity);
}
