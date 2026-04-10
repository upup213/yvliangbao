package com.yvliangbao.common.service.merchant;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.dto.product.ProductCreateDTO;
import com.yvliangbao.common.pojo.dto.product.ProductUpdateDTO;
import com.yvliangbao.common.pojo.entity.merchant.ProductInfo;


import java.util.List;

/**
 * 商品信息 Service 接口
 *
 * 库存操作说明：
 * - lockStock: 下单时锁定库存
 * - confirmDeduct: 支付成功后确认扣减
 * - releaseStock: 取消订单/超时时释放库存
 *
 * @author 余量宝
 */
public interface ProductInfoService extends IService<ProductInfo> {
    
    /**
     * 锁定库存（预占）
     *
     * 下单时调用，将可用库存转移到锁定库存
     *
     * @param productId 商品ID
     * @param quantity 锁定数量
     * @param orderNo 关联订单号
     * @return 更新行数（0=库存不足，1=成功）
     */
    int lockStock(Long productId, Integer quantity, String orderNo);
    
    /**
     * 确认扣减库存
     *
     * 支付成功后调用
     *
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @param orderNo 关联订单号
     * @return 更新行数
     */
    int confirmDeduct(Long productId, Integer quantity, String orderNo);
    
    /**
     * 释放锁定库存
     *
     * 取消订单或订单超时时调用
     *
     * @param productId 商品ID
     * @param quantity 释放数量
     * @param orderNo 关联订单号
     * @return 更新行数
     */
    int releaseStock(Long productId, Integer quantity, String orderNo);
    
    /**
     * 增加库存（退款时使用）
     *
     * 退款成功后调用，将库存返还到可用库存
     *
     * @param productId 商品ID
     * @param quantity 增加数量
     * @param orderNo 关联订单号
     * @return 更新行数
     */
    int increaseStock(Long productId, Integer quantity, String orderNo);
    
    /**
     * 原子扣减库存（已废弃）
     * 
     * @param productId 商品ID
     * @param quantity 购买数量
     * @return 更新行数（0表示库存不足）
     * @deprecated 使用 lockStock + confirmDeduct 替代
     */
    @Deprecated
    int reduceStock(Long productId, Integer quantity);
    
    /**
     * 恢复库存（已废弃）
     * 
     * @param productId 商品ID
     * @param quantity 恢复数量
     * @return 更新行数
     * @deprecated 使用 releaseStock 替代
     */
    @Deprecated
    int restoreStock(Long productId, Integer quantity);
    
    /**
     * 发布商品
     * 
     * @param dto 商品创建信息
     * @param merchantId 商户ID
     * @return 创建的商品
     */
    ProductInfo createProduct(ProductCreateDTO dto, Long merchantId);
    
    /**
     * 更新商品
     * 
     * @param dto 商品更新信息
     * @param merchantId 商户ID
     * @return 更新后的商品
     */
    ProductInfo updateProduct(ProductUpdateDTO dto, Long merchantId);
    
    /**
     * 获取商户商品列表
     * 
     * @param merchantId 商户ID
     * @return 商品列表
     */
    List<ProductInfo> getMerchantProducts(Long merchantId);
    
    /**
     * 获取用户端商品列表
     * 
     * @param storeId 门店ID
     * @param status 状态
     * @return 商品列表
     */
    List<ProductInfo> getUserProducts(Long storeId, Integer status);
    
    /**
     * 上架商品
     * 
     * @param productId 商品ID
     * @param merchantId 商户ID
     */
    void onlineProduct(Long productId, Long merchantId);
    
    /**
     * 下架商品
     * 
     * @param productId 商品ID
     * @param merchantId 商户ID
     */
    void offlineProduct(Long productId, Long merchantId);

    /**
     * 统计商户商品数量
     *
     * @param merchantId 商户ID
     * @param status 商品状态（可选，null表示全部）
     * @return 商品数量
     */
    int countByMerchantId(Long merchantId, Integer status);
}
