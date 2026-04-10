package com.yvliangbao.common.service.impl.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yvliangbao.common.service.common.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务实现
 *
 * Redis Key命名规范：
 * - 用户信息: user:info:{userId}
 * - 商户信息: merchant:info:{merchantId}
 * - 商品信息: product:info:{productId}
 * - 门店商品列表: store:products:{storeId}
 * - 门店信息: store:info:{storeId}
 * - 商户门店列表: merchant:stores:{merchantId}
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== 缓存Key前缀 ====================
    private static final String USER_INFO_PREFIX = "user:info:";
    private static final String MERCHANT_INFO_PREFIX = "merchant:info:";
    private static final String PRODUCT_INFO_PREFIX = "product:info:";
    private static final String STORE_PRODUCTS_PREFIX = "store:products:";
    private static final String STORE_INFO_PREFIX = "store:info:";
    private static final String MERCHANT_STORES_PREFIX = "merchant:stores:";
    private static final String ONLINE_PRODUCT_IDS_KEY = "product:list:online";
    private static final String INVENTORY_INFO_PREFIX = "inventory:info:";

    // ==================== 缓存过期时间（秒） ====================
    private static final long USER_CACHE_TTL = 24 * 60 * 60;        // 24小时
    private static final long MERCHANT_CACHE_TTL = 24 * 60 * 60;    // 24小时
    private static final long PRODUCT_CACHE_TTL = 30 * 60;          // 30分钟（商品变化频繁）
    private static final long STORE_PRODUCTS_CACHE_TTL = 10 * 60;   // 10分钟
    private static final long STORE_CACHE_TTL = 24 * 60 * 60;       // 24小时
    private static final long MERCHANT_STORES_CACHE_TTL = 60 * 60;  // 1小时
    private static final long ONLINE_PRODUCTS_TTL = 5 * 60;         // 5分钟
    private static final long INVENTORY_CACHE_TTL = 2 * 60;         // 2分钟（库存变化最频繁）
    private static final long MERCHANT_STATS_CACHE_TTL = 5 * 60;     // 5分钟（统计数据变化相对频繁）
    private static final long MERCHANT_PRODUCTS_CACHE_TTL = 10 * 60;  // 10分钟
    private static final long MERCHANT_STORE_LIST_CACHE_TTL = 30 * 60; // 30分钟

    // 随机波动因子（防止缓存雪崩）
    private static final double TTL_VARIANCE = 0.1;  // ±10%

    // 缓存锁过期时间
    private static final long CACHE_LOCK_TTL = 10;  // 10秒

    // ==================== 工具方法 ====================

    /**
     * 获取带随机波动的过期时间（防止缓存雪崩）
     */
    private long getRandomTTL(long baseTTL) {
        long variance = (long) (baseTTL * TTL_VARIANCE);
        long random = (long) (Math.random() * variance * 2 - variance);
        return Math.max(baseTTL + random, 60);  // 最小60秒
    }

    // 缓存锁前缀
    private static final String CACHE_LOCK_PREFIX = "lock:cache:";

    // ==================== 用户缓存 ====================

    @Override
    public Map<String, Object> getUserCache(Long userId) {
        String key = USER_INFO_PREFIX + userId;
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("获取用户缓存失败: userId={}, error={}", userId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setUserCache(Long userId, Map<String, Object> userData) {
        String key = USER_INFO_PREFIX + userId;
        try {
            String json = objectMapper.writeValueAsString(userData);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(USER_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置用户缓存成功: userId={}", userId);
        } catch (Exception e) {
            log.warn("设置用户缓存失败: userId={}, error={}", userId, e.getMessage());
        }
    }

    @Override
    public void deleteUserCache(Long userId) {
        String key = USER_INFO_PREFIX + userId;
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除用户缓存成功: userId={}", userId);
        } catch (Exception e) {
            log.warn("删除用户缓存失败: userId={}, error={}", userId, e.getMessage());
        }
    }

    @Override
    public Map<Long, Map<String, Object>> batchGetUserCache(List<Long> userIds) {
        Map<Long, Map<String, Object>> result = new HashMap<>();
        for (Long userId : userIds) {
            Map<String, Object> userCache = getUserCache(userId);
            if (userCache != null) {
                result.put(userId, userCache);
            }
        }
        return result;
    }

    // ==================== 商户缓存 ====================

    @Override
    public Map<String, Object> getMerchantCache(Long merchantId) {
        String key = MERCHANT_INFO_PREFIX + merchantId;
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("获取商户缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setMerchantCache(Long merchantId, Map<String, Object> merchantData) {
        String key = MERCHANT_INFO_PREFIX + merchantId;
        try {
            String json = objectMapper.writeValueAsString(merchantData);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(MERCHANT_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置商户缓存成功: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("设置商户缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public void deleteMerchantCache(Long merchantId) {
        String key = MERCHANT_INFO_PREFIX + merchantId;
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除商户缓存成功: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("删除商户缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    // ==================== 商品缓存 ====================

    @Override
    public Map<String, Object> getProductCache(Long productId) {
        String key = PRODUCT_INFO_PREFIX + productId;
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("获取商品缓存失败: productId={}, error={}", productId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setProductCache(Long productId, Map<String, Object> productData) {
        String key = PRODUCT_INFO_PREFIX + productId;
        try {
            String json = objectMapper.writeValueAsString(productData);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(PRODUCT_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置商品缓存成功: productId={}", productId);
        } catch (Exception e) {
            log.warn("设置商品缓存失败: productId={}, error={}", productId, e.getMessage());
        }
    }

    @Override
    public void deleteProductCache(Long productId) {
        String key = PRODUCT_INFO_PREFIX + productId;
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除商品缓存成功: productId={}", productId);
        } catch (Exception e) {
            log.warn("删除商品缓存失败: productId={}, error={}", productId, e.getMessage());
        }
    }

    @Override
    public List<Long> getStoreProductsCache(Long storeId) {
        String key = STORE_PRODUCTS_PREFIX + storeId;
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.warn("获取门店商品列表缓存失败: storeId={}, error={}", storeId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setStoreProductsCache(Long storeId, List<Long> productIds) {
        String key = STORE_PRODUCTS_PREFIX + storeId;
        try {
            String json = objectMapper.writeValueAsString(productIds);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(STORE_PRODUCTS_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置门店商品列表缓存成功: storeId={}, count={}", storeId, productIds.size());
        } catch (Exception e) {
            log.warn("设置门店商品列表缓存失败: storeId={}, error={}", storeId, e.getMessage());
        }
    }

    @Override
    public void deleteStoreProductsCache(Long storeId) {
        String key = STORE_PRODUCTS_PREFIX + storeId;
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除门店商品列表缓存成功: storeId={}", storeId);
        } catch (Exception e) {
            log.warn("删除门店商品列表缓存失败: storeId={}, error={}", storeId, e.getMessage());
        }
    }

    // ==================== 商户商品列表缓存 ====================

    @Override
    public List<Map<String, Object>> getMerchantProductsCache(Long merchantId) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":products";
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            log.debug("从Redis获取商户商品列表缓存: merchantId={}", merchantId);
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.warn("获取商户商品列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setMerchantProductsCache(Long merchantId, List<Map<String, Object>> productList) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":products";
        try {
            String json = objectMapper.writeValueAsString(productList);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(MERCHANT_PRODUCTS_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置商户商品列表缓存成功: merchantId={}, count={}", merchantId, productList.size());
        } catch (Exception e) {
            log.warn("设置商户商品列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public void deleteMerchantProductsCache(Long merchantId) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":products";
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除商户商品列表缓存成功: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("删除商户商品列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    // ==================== 商品列表缓存 ====================

    @Override
    public List<Long> getOnlineProductIdsCache() {
        try {
            String json = stringRedisTemplate.opsForValue().get(ONLINE_PRODUCT_IDS_KEY);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.warn("获取上架商品ID列表缓存失败: error={}", e.getMessage());
            return null;
        }
    }

    @Override
    public void setOnlineProductIdsCache(List<Long> productIds) {
        try {
            String json = objectMapper.writeValueAsString(productIds);
            stringRedisTemplate.opsForValue().set(ONLINE_PRODUCT_IDS_KEY, json, getRandomTTL(ONLINE_PRODUCTS_TTL), TimeUnit.SECONDS);
            log.debug("设置上架商品ID列表缓存成功: count={}", productIds.size());
        } catch (Exception e) {
            log.warn("设置上架商品ID列表缓存失败: error={}", e.getMessage());
        }
    }

    @Override
    public void deleteOnlineProductIdsCache() {
        try {
            stringRedisTemplate.delete(ONLINE_PRODUCT_IDS_KEY);
            log.debug("删除上架商品ID列表缓存成功");
        } catch (Exception e) {
            log.warn("删除上架商品ID列表缓存失败: error={}", e.getMessage());
        }
    }

    // ==================== 库存缓存 ====================

    @Override
    public Map<String, Object> getInventoryCache(Long productId) {
        String key = INVENTORY_INFO_PREFIX + productId;
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("获取库存缓存失败: productId={}, error={}", productId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setInventoryCache(Long productId, Map<String, Object> inventoryData) {
        String key = INVENTORY_INFO_PREFIX + productId;
        try {
            String json = objectMapper.writeValueAsString(inventoryData);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(INVENTORY_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置库存缓存成功: productId={}", productId);
        } catch (Exception e) {
            log.warn("设置库存缓存失败: productId={}, error={}", productId, e.getMessage());
        }
    }

    @Override
    public void deleteInventoryCache(Long productId) {
        String key = INVENTORY_INFO_PREFIX + productId;
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除库存缓存成功: productId={}", productId);
        } catch (Exception e) {
            log.warn("删除库存缓存失败: productId={}, error={}", productId, e.getMessage());
        }
    }

    // ==================== 门店缓存 ====================

    @Override
    public Map<String, Object> getStoreCache(Long storeId) {
        String key = STORE_INFO_PREFIX + storeId;
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("获取门店缓存失败: storeId={}, error={}", storeId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setStoreCache(Long storeId, Map<String, Object> storeData) {
        String key = STORE_INFO_PREFIX + storeId;
        try {
            String json = objectMapper.writeValueAsString(storeData);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(STORE_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置门店缓存成功: storeId={}", storeId);
        } catch (Exception e) {
            log.warn("设置门店缓存失败: storeId={}, error={}", storeId, e.getMessage());
        }
    }

    @Override
    public void deleteStoreCache(Long storeId) {
        String key = STORE_INFO_PREFIX + storeId;
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除门店缓存成功: storeId={}", storeId);
        } catch (Exception e) {
            log.warn("删除门店缓存失败: storeId={}, error={}", storeId, e.getMessage());
        }
    }

    @Override
    public List<Long> getMerchantStoresCache(Long merchantId) {
        String key = MERCHANT_STORES_PREFIX + merchantId;
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(json, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            log.warn("获取商户门店列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setMerchantStoresCache(Long merchantId, List<Long> storeIds) {
        String key = MERCHANT_STORES_PREFIX + merchantId;
        try {
            String json = objectMapper.writeValueAsString(storeIds);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(MERCHANT_STORES_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置商户门店列表缓存成功: merchantId={}, count={}", merchantId, storeIds.size());
        } catch (Exception e) {
            log.warn("设置商户门店列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public void deleteMerchantStoresCache(Long merchantId) {
        String key = MERCHANT_STORES_PREFIX + merchantId;
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除商户门店列表缓存成功: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("删除商户门店列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    // ==================== 商户门店详情列表缓存 ====================

    @Override
    public List<Map<String, Object>> getMerchantStoreListCache(Long merchantId) {
        String key = MERCHANT_STORES_PREFIX + merchantId + ":detail";
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            log.debug("从Redis获取商户门店详情列表缓存: merchantId={}", merchantId);
            return objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            log.warn("获取商户门店详情列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setMerchantStoreListCache(Long merchantId, List<Map<String, Object>> storeList) {
        String key = MERCHANT_STORES_PREFIX + merchantId + ":detail";
        try {
            String json = objectMapper.writeValueAsString(storeList);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(MERCHANT_STORE_LIST_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置商户门店详情列表缓存成功: merchantId={}, count={}", merchantId, storeList.size());
        } catch (Exception e) {
            log.warn("设置商户门店详情列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public void deleteMerchantStoreListCache(Long merchantId) {
        String key = MERCHANT_STORES_PREFIX + merchantId + ":detail";
        try {
            stringRedisTemplate.delete(key);
            log.debug("删除商户门店详情列表缓存成功: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("删除商户门店详情列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    // ==================== 通用操作 ====================

    @Override
    public void clearAllCache() {
        log.info("清除所有缓存开始");
        try {
            Set<String> keys = stringRedisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
                log.info("清除所有缓存完成: count={}", keys.size());
            }
        } catch (Exception e) {
            log.error("清除所有缓存失败: error={}", e.getMessage());
        }
    }

    @Override
    public void clearUserRelatedCache(Long userId) {
        log.info("清除用户相关缓存: userId={}", userId);
        deleteUserCache(userId);
    }

    @Override
    public void clearMerchantRelatedCache(Long merchantId) {
        log.info("清除商户相关缓存: merchantId={}", merchantId);
        deleteMerchantCache(merchantId);
        deleteMerchantStoresCache(merchantId);
        deleteMerchantStoreListCache(merchantId);
        deleteMerchantStatsCache(merchantId);
        deleteMerchantRevenueTrendCache(merchantId);
    }

    @Override
    public void clearProductRelatedCache(Long productId) {
        log.info("清除商品相关缓存: productId={}", productId);
        deleteProductCache(productId);
        // 同时清除门店商品列表缓存（需要知道商品所属门店，这里简化处理）
    }

    @Override
    public void clearStoreRelatedCache(Long storeId) {
        log.info("清除门店相关缓存: storeId={}", storeId);
        deleteStoreCache(storeId);
        deleteStoreProductsCache(storeId);
    }

    // ==================== 商户统计缓存 ====================
    
    @Override
    public Map<String, Object> getMerchantStatsCache(Long merchantId) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":stats";
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            log.debug("从Redis获取商户统计缓存: merchantId={}", merchantId);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("获取商户统计缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setMerchantStatsCache(Long merchantId, Map<String, Object> statsData) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":stats";
        try {
            String json = objectMapper.writeValueAsString(statsData);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(MERCHANT_STATS_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置商户统计缓存成功: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("设置商户统计缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public void deleteMerchantStatsCache(Long merchantId) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":stats";
        try {
            stringRedisTemplate.delete(key);
            log.info("删除商户统计缓存: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("删除商户统计缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getMerchantRevenueTrendCache(Long merchantId, Integer days) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":revenue:trend:" + days;
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            log.debug("从Redis获取商户营收趋势缓存: merchantId={}, days={}", merchantId, days);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("获取商户营收趋势缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setMerchantRevenueTrendCache(Long merchantId, Integer days, Map<String, Object> trendData) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":revenue:trend:" + days;
        try {
            String json = objectMapper.writeValueAsString(trendData);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(MERCHANT_STATS_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置商户营收趋势缓存成功: merchantId={}, days={}", merchantId, days);
        } catch (Exception e) {
            log.warn("设置商户营收趋势缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public void deleteMerchantRevenueTrendCache(Long merchantId) {
        try {
            // 删除所有天数的趋势缓存
            Set<String> keys = stringRedisTemplate.keys(MERCHANT_INFO_PREFIX + merchantId + ":revenue:trend:*");
            if (keys != null && !keys.isEmpty()) {
                stringRedisTemplate.delete(keys);
                log.info("删除商户营收趋势缓存: merchantId={}, count={}", merchantId, keys.size());
            }
        } catch (Exception e) {
            log.warn("删除商户营收趋势缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public void deleteMerchantSettlementOverviewCache(Long merchantId) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":settlement:overview";
        try {
            stringRedisTemplate.delete(key);
            log.info("删除商户结算概览缓存: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("删除商户结算概览缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getMerchantSettlementOverviewCache(Long merchantId) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":settlement:overview";
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }
            log.debug("从Redis获取商户结算概览缓存: merchantId={}", merchantId);
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("获取商户结算概览缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
            return null;
        }
    }

    @Override
    public void setMerchantSettlementOverviewCache(Long merchantId, Map<String, Object> overviewData) {
        String key = MERCHANT_INFO_PREFIX + merchantId + ":settlement:overview";
        try {
            String json = objectMapper.writeValueAsString(overviewData);
            stringRedisTemplate.opsForValue().set(key, json, getRandomTTL(MERCHANT_STATS_CACHE_TTL), TimeUnit.SECONDS);
            log.debug("设置商户结算概览缓存成功: merchantId={}", merchantId);
        } catch (Exception e) {
            log.warn("设置商户结算概览缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }
    }
}
