package com.yvliangbao.common.service.common;

import java.util.List;
import java.util.Map;

/**
 * 缓存服务接口
 *
 * 提供统一的缓存管理功能，包括：
 * - 用户信息缓存
 * - 商户信息缓存
 * - 商品信息缓存
 * - 门店信息缓存
 *
 * @author 余量宝
 */
public interface CacheService {

    // ==================== 用户缓存 ====================

    /**
     * 获取用户缓存
     *
     * @param userId 用户ID
     * @return 用户信息（Map格式），不存在返回null
     */
    Map<String, Object> getUserCache(Long userId);

    /**
     * 设置用户缓存
     *
     * @param userId 用户ID
     * @param userData 用户数据
     */
    void setUserCache(Long userId, Map<String, Object> userData);

    /**
     * 删除用户缓存
     *
     * @param userId 用户ID
     */
    void deleteUserCache(Long userId);

    /**
     * 批量获取用户缓存
     *
     * @param userIds 用户ID列表
     * @return 用户ID -> 用户数据的映射
     */
    Map<Long, Map<String, Object>> batchGetUserCache(List<Long> userIds);

    // ==================== 商户缓存 ====================

    /**
     * 获取商户缓存
     *
     * @param merchantId 商户ID
     * @return 商户信息（Map格式），不存在返回null
     */
    Map<String, Object> getMerchantCache(Long merchantId);

    /**
     * 设置商户缓存
     *
     * @param merchantId 商户ID
     * @param merchantData 商户数据
     */
    void setMerchantCache(Long merchantId, Map<String, Object> merchantData);

    /**
     * 删除商户缓存
     *
     * @param merchantId 商户ID
     */
    void deleteMerchantCache(Long merchantId);

    // ==================== 商品缓存 ====================

    /**
     * 获取商品缓存
     *
     * @param productId 商品ID
     * @return 商品信息（Map格式），不存在返回null
     */
    Map<String, Object> getProductCache(Long productId);

    /**
     * 设置商品缓存
     *
     * @param productId 商品ID
     * @param productData 商品数据
     */
    void setProductCache(Long productId, Map<String, Object> productData);

    /**
     * 删除商品缓存
     *
     * @param productId 商品ID
     */
    void deleteProductCache(Long productId);

    /**
     * 获取门店商品列表缓存
     *
     * @param storeId 门店ID
     * @return 商品ID列表
     */
    List<Long> getStoreProductsCache(Long storeId);

    /**
     * 设置门店商品列表缓存
     *
     * @param storeId 门店ID
     * @param productIds 商品ID列表
     */
    void setStoreProductsCache(Long storeId, List<Long> productIds);

    /**
     * 删除门店商品列表缓存
     *
     * @param storeId 门店ID
     */
    void deleteStoreProductsCache(Long storeId);

    // ==================== 商户商品列表缓存 ====================

    /**
     * 获取商户商品列表缓存
     *
     * @param merchantId 商户ID
     * @return 商品详情列表（Map格式），不存在返回null
     */
    List<Map<String, Object>> getMerchantProductsCache(Long merchantId);

    /**
     * 设置商户商品列表缓存
     *
     * @param merchantId 商户ID
     * @param productList 商品详情列表
     */
    void setMerchantProductsCache(Long merchantId, List<Map<String, Object>> productList);

    /**
     * 删除商户商品列表缓存
     *
     * @param merchantId 商户ID
     */
    void deleteMerchantProductsCache(Long merchantId);

    // ==================== 商品列表缓存 ====================

    /**
     * 获取上架商品ID列表缓存
     *
     * @return 商品ID列表
     */
    List<Long> getOnlineProductIdsCache();

    /**
     * 设置上架商品ID列表缓存
     *
     * @param productIds 商品ID列表
     */
    void setOnlineProductIdsCache(List<Long> productIds);

    /**
     * 删除上架商品ID列表缓存
     */
    void deleteOnlineProductIdsCache();

    // ==================== 库存缓存 ====================

    /**
     * 获取库存缓存
     *
     * @param productId 商品ID
     * @return 库存信息（Map格式），不存在返回null
     */
    Map<String, Object> getInventoryCache(Long productId);

    /**
     * 设置库存缓存
     *
     * @param productId 商品ID
     * @param inventoryData 库存数据
     */
    void setInventoryCache(Long productId, Map<String, Object> inventoryData);

    /**
     * 删除库存缓存
     *
     * @param productId 商品ID
     */
    void deleteInventoryCache(Long productId);

    // ==================== 门店缓存 ====================

    /**
     * 获取门店缓存
     *
     * @param storeId 门店ID
     * @return 门店信息（Map格式），不存在返回null
     */
    Map<String, Object> getStoreCache(Long storeId);

    /**
     * 设置门店缓存
     *
     * @param storeId 门店ID
     * @param storeData 门店数据
     */
    void setStoreCache(Long storeId, Map<String, Object> storeData);

    /**
     * 删除门店缓存
     *
     * @param storeId 门店ID
     */
    void deleteStoreCache(Long storeId);

    /**
     * 获取商户门店列表缓存
     *
     * @param merchantId 商户ID
     * @return 门店ID列表
     */
    List<Long> getMerchantStoresCache(Long merchantId);

    /**
     * 设置商户门店列表缓存
     *
     * @param merchantId 商户ID
     * @param storeIds 门店ID列表
     */
    void setMerchantStoresCache(Long merchantId, List<Long> storeIds);

    /**
     * 删除商户门店列表缓存
     *
     * @param merchantId 商户ID
     */
    void deleteMerchantStoresCache(Long merchantId);

    /**
     * 获取商户门店详情列表缓存（完整数据）
     *
     * @param merchantId 商户ID
     * @return 门店详情列表（Map格式），不存在返回null
     */
    List<Map<String, Object>> getMerchantStoreListCache(Long merchantId);

    /**
     * 设置商户门店详情列表缓存
     *
     * @param merchantId 商户ID
     * @param storeList 门店详情列表
     */
    void setMerchantStoreListCache(Long merchantId, List<Map<String, Object>> storeList);

    /**
     * 删除商户门店详情列表缓存
     *
     * @param merchantId 商户ID
     */
    void deleteMerchantStoreListCache(Long merchantId);

    // ==================== 通用操作 ====================

    /**
     * 清除所有缓存
     */
    void clearAllCache();

    /**
     * 清除用户相关缓存
     */
    void clearUserRelatedCache(Long userId);

    /**
     * 清除商户相关缓存
     */
    void clearMerchantRelatedCache(Long merchantId);

    /**
     * 清除商品相关缓存
     */
    void clearProductRelatedCache(Long productId);

    /**
     * 清除门店相关缓存
     */
    void clearStoreRelatedCache(Long storeId);

    // ==================== 商户统计缓存 ====================

    /**
     * 获取商户统计缓存
     *
     * @param merchantId 商户ID
     * @return 统计数据（Map格式），不存在返回null
     */
    Map<String, Object> getMerchantStatsCache(Long merchantId);

    /**
     * 设置商户统计缓存
     *
     * @param merchantId 商户ID
     * @param statsData 统计数据
     */
    void setMerchantStatsCache(Long merchantId, Map<String, Object> statsData);

    /**
     * 删除商户统计缓存
     */
    void deleteMerchantStatsCache(Long merchantId);

    /**
     * 获取商户营收趋势缓存
     *
     * @param merchantId 商户ID
     * @return 营收趋势数据（Map格式），不存在返回null
     */
    Map<String, Object> getMerchantRevenueTrendCache(Long merchantId, Integer days);

    /**
     * 设置商户营收趋势缓存
     *
     * @param merchantId 商户ID
     * @param days 天数
     * @param trendData 趋势数据
     */
    void setMerchantRevenueTrendCache(Long merchantId, Integer days, Map<String, Object> trendData);

    /**
     * 删除商户营收趋势缓存
     */
    void deleteMerchantRevenueTrendCache(Long merchantId);

    /**
     * 删除商户结算概览缓存
     */
    void deleteMerchantSettlementOverviewCache(Long merchantId);

    /**
     * 获取商户结算概览缓存
     *
     * @param merchantId 商户ID
     * @return 结算概览数据（Map格式），不存在返回null
     */
    Map<String, Object> getMerchantSettlementOverviewCache(Long merchantId);

    /**
     * 设置商户结算概览缓存
     *
     * @param merchantId 商户ID
     * @param overviewData 结算概览数据
     */
    void setMerchantSettlementOverviewCache(Long merchantId, Map<String, Object> overviewData);
}
