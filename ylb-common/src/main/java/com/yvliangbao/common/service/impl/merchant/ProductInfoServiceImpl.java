package com.yvliangbao.common.service.impl.merchant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.mapper.merchant.ProductInfoMapper;
import com.yvliangbao.common.pojo.dto.product.ProductCreateDTO;
import com.yvliangbao.common.pojo.dto.product.ProductUpdateDTO;
import com.yvliangbao.common.pojo.entity.merchant.InventoryInfo;
import com.yvliangbao.common.pojo.entity.merchant.ProductInfo;
import com.yvliangbao.common.pojo.entity.merchant.StoreInfo;
import com.yvliangbao.common.pojo.enums.ProductStatus;
import com.yvliangbao.common.service.common.CacheService;
import com.yvliangbao.common.service.merchant.InventoryInfoService;
import com.yvliangbao.common.service.merchant.ProductInfoService;
import com.yvliangbao.common.service.merchant.StoreInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品信息 Service 实现
 * 
 * 提供商品相关的业务逻辑处理，包括：
 * - 库存管理：通过 InventoryInfoService 管理库存
 * - 商品管理：发布、上架、下架
 * - 商品查询：商户端、用户端
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductInfoService {

    @Autowired
    private StoreInfoService storeInfoService;

    @Autowired
    private InventoryInfoService inventoryInfoService;

    @Autowired
    private CacheService cacheService;

    /**
     * 锁定库存（预占）
     *
     * 下单时调用，委托给 InventoryInfoService 处理
     *
     * @param productId 商品ID
     * @param quantity 锁定数量
     * @param orderNo 关联订单号
     * @return 更新行数（0=库存不足，1=成功）
     */
    @Override
    public int lockStock(Long productId, Integer quantity, String orderNo) {
        return inventoryInfoService.lockStock(productId, quantity, orderNo);
    }

    /**
     * 确认扣减库存
     *
     * 支付成功后调用，委托给 InventoryInfoService 处理
     *
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @param orderNo 关联订单号
     * @return 更新行数
     */
    @Override
    public int confirmDeduct(Long productId, Integer quantity, String orderNo) {
        return inventoryInfoService.confirmDeduct(productId, quantity, orderNo);
    }

    /**
     * 释放锁定库存
     *
     * 取消订单或订单超时时调用，委托给 InventoryInfoService 处理
     *
     * @param productId 商品ID
     * @param quantity 释放数量
     * @param orderNo 关联订单号
     * @return 更新行数
     */
    @Override
    public int releaseStock(Long productId, Integer quantity, String orderNo) {
        return inventoryInfoService.releaseStock(productId, quantity, orderNo);
    }

    /**
     * 增加库存（退款时使用）
     *
     * 退款成功后调用，委托给 InventoryInfoService 处理
     *
     * @param productId 商品ID
     * @param quantity 增加数量
     * @param orderNo 关联订单号
     * @return 更新行数
     */
    @Override
    public int increaseStock(Long productId, Integer quantity, String orderNo) {
        return inventoryInfoService.increaseStock(productId, quantity, orderNo);
    }

    /**
     * 原子扣减库存（已废弃）
     *
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 更新行数（0=库存不足，1=成功）
     * @deprecated 使用 lockStock + confirmDeduct 替代
     */
    @Override
    @Deprecated
    public int reduceStock(Long productId, Integer quantity) {
        return inventoryInfoService.reduceStock(productId, quantity);
    }

    /**
     * 恢复库存（已废弃）
     *
     * @param productId 商品ID
     * @param quantity 恢复数量
     * @return 更新行数
     * @deprecated 使用 releaseStock 替代
     */
    @Override
    @Deprecated
    public int restoreStock(Long productId, Integer quantity) {
        return inventoryInfoService.restoreStock(productId, quantity);
    }
    
    /**
     * 发布商品
     * 
     * 商户发布新的余量商品（盲盒/魔法袋）：
     * 1. 验证门店归属（确保商户只能在自己的门店发布商品）
     * 2. 创建商品记录，初始状态为上架
     * 3. 初始化库存信息
     * 
     * @param dto 商品创建信息
     * @param merchantId 商户ID
     * @return 创建的商品实体
     * @throws BusinessException 门店不存在或无权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductInfo createProduct(ProductCreateDTO dto, Long merchantId) {
        log.info("发布商品开始: merchantId={}, storeId={}", merchantId, dto.getStoreId());
        
        try {
            // 1. 验证门店归属
            StoreInfo store = storeInfoService.getById(dto.getStoreId());
            if (store == null || !store.getMerchantId().equals(merchantId)) {
                log.warn("发布商品失败: 门店不存在或无权限, merchantId={}, storeId={}", merchantId, dto.getStoreId());
                throw new BusinessException("门店不存在或无权限");
            }

            // 2. 创建商品
            ProductInfo product = new ProductInfo();
            BeanUtils.copyProperties(dto, product);
            product.setProductNo(generateProductNo());
            product.setStatus(ProductStatus.ONLINE.getCode());
            product.setShelfTime(LocalDateTime.now());
            product.setSoldCount(0);
            // 计算折扣率 = 清仓价/原价 * 100
            if (dto.getOriginalPrice() != null && dto.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountRate = dto.getSalePrice()
                        .divide(dto.getOriginalPrice(), 2, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                product.setDiscountRate(discountRate);
            }
            this.save(product);

            // 3. 初始化库存
            inventoryInfoService.initInventory(product.getId(), dto.getTotalStock());

            // 4. 清除商品列表缓存
            clearMerchantProductsCache(merchantId);

            log.info("发布商品成功: productId={}, productName={}", product.getId(), product.getProductName());
            return product;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发布商品异常: merchantId={}, storeId={}, error={}", merchantId, dto.getStoreId(), e.getMessage(), e);
            throw new BusinessException("发布商品失败：" + e.getMessage());
        }
    }

    /**
     * 生成商品编号
     */
    private String generateProductNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "P" + timestamp + random;
    }
    
    /**
     * 更新商品
     * 
     * 商户修改商品信息：
     * 1. 验证商品归属（确保商户只能修改自己的商品）
     * 2. 更新商品基本信息
     * 3. 如有库存变更，同步更新库存
     * 
     * @param dto 商品更新信息
     * @param merchantId 商户ID
     * @return 更新后的商品实体
     * @throws BusinessException 商品不存在或无权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductInfo updateProduct(ProductUpdateDTO dto, Long merchantId) {
        log.info("更新商品开始: productId={}, merchantId={}", dto.getId(), merchantId);
        
        try {
            // 1. 验证商品归属
            ProductInfo product = validateProductOwnership(dto.getId(), merchantId);
            
            // 2. 更新基本信息（只更新非空字段）
            if (dto.getProductName() != null) {
                product.setProductName(dto.getProductName());
            }
            if (dto.getProductDesc() != null) {
                product.setProductDesc(dto.getProductDesc());
            }
            if (dto.getProductImages() != null) {
                product.setProductImages(dto.getProductImages());
            }
            if (dto.getOriginalPrice() != null) {
                product.setOriginalPrice(dto.getOriginalPrice());
            }
            if (dto.getSalePrice() != null) {
                product.setSalePrice(dto.getSalePrice());
            }
            // 重新计算折扣率
            if (dto.getOriginalPrice() != null && dto.getSalePrice() != null 
                    && dto.getOriginalPrice().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountRate = dto.getSalePrice()
                        .divide(dto.getOriginalPrice(), 2, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                product.setDiscountRate(discountRate);
            }
            if (dto.getPickupTimeStart() != null) {
                product.setPickupTimeStart(dto.getPickupTimeStart());
            }
            if (dto.getPickupTimeEnd() != null) {
                product.setPickupTimeEnd(dto.getPickupTimeEnd());
            }
            if (dto.getPurchaseLimit() != null) {
                product.setPurchaseLimit(dto.getPurchaseLimit());
            }
            
            // 3. 更新商品
            this.updateById(product);
            
            // 4. 更新库存（如有变更）
            if (dto.getTotalStock() != null) {
                InventoryInfo inventory = inventoryInfoService.getByProductId(dto.getId());
                if (inventory != null) {
                    // 计算库存变化量
                    int stockDiff = dto.getTotalStock() - inventory.getTotalStock();
                    if (stockDiff != 0) {
                        inventoryInfoService.updateTotalStock(dto.getId(), dto.getTotalStock());
                        log.info("更新商品库存: productId={}, oldStock={}, newStock={}", 
                                dto.getId(), inventory.getTotalStock(), dto.getTotalStock());
                    }
                }
            }
            
            // 5. 清除商品列表缓存
            clearMerchantProductsCache(merchantId);
            
            log.info("更新商品成功: productId={}", dto.getId());
            return product;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新商品异常: productId={}, error={}", dto.getId(), e.getMessage(), e);
            throw new BusinessException("更新商品失败：" + e.getMessage());
        }
    }

    /**
     * 获取商户的商品列表
     *
     * 查询指定商户所有门店下的商品：
     * 1. 先查询商户的所有门店
     * 2. 再查询这些门店下的所有商品
     * 3. 关联库存信息
     *
     * 使用 CacheService 缓存，缓存时间 10 分钟
     *
     * @param merchantId 商户ID
     * @return 商品列表（按创建时间倒序）
     */
    @Override
    public List<ProductInfo> getMerchantProducts(Long merchantId) {
        log.debug("获取商户商品列表: merchantId={}", merchantId);

        // 尝试从缓存获取
        try {
            List<Map<String, Object>> cachedList = cacheService.getMerchantProductsCache(merchantId);
            if (cachedList != null && !cachedList.isEmpty()) {
                log.debug("从缓存获取商户商品列表: merchantId={}, count={}", merchantId, cachedList.size());
                return convertMapListToProductList(cachedList);
            }
        } catch (Exception e) {
            log.warn("读取商品列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
        }

        try {
            // 1. 查询商户的所有门店
            List<StoreInfo> stores = storeInfoService.listByMerchantId(merchantId);
            if (stores.isEmpty()) {
                log.debug("商户无门店: merchantId={}", merchantId);
                return Collections.emptyList();
            }

            // 2. 查询所有商品
            List<Long> storeIds = stores.stream().map(StoreInfo::getId).collect(Collectors.toList());
            List<ProductInfo> products = this.lambdaQuery()
                    .in(ProductInfo::getStoreId, storeIds)
                    .orderByDesc(ProductInfo::getCreateTime)
                    .list();

            // 3. 关联库存信息
            for (ProductInfo product : products) {
                InventoryInfo inventory = inventoryInfoService.getByProductId(product.getId());
                if (inventory != null) {
                    product.setTotalStock(inventory.getTotalStock());
                    product.setAvailableStock(inventory.getAvailableStock());
                } else {
                    product.setTotalStock(0);
                    product.setAvailableStock(0);
                }
            }

            // 4. 写入缓存
            try {
                List<Map<String, Object>> mapList = convertProductListToMapList(products);
                cacheService.setMerchantProductsCache(merchantId, mapList);
                log.debug("商品列表已缓存: merchantId={}, count={}", merchantId, products.size());
            } catch (Exception e) {
                log.warn("写入商品列表缓存失败: merchantId={}, error={}", merchantId, e.getMessage());
            }

            log.debug("获取商户商品列表完成: merchantId={}, count={}", merchantId, products.size());
            return products;
        } catch (Exception e) {
            log.error("获取商户商品列表异常: merchantId={}, error={}", merchantId, e.getMessage(), e);
            throw new BusinessException("获取商品列表失败：" + e.getMessage());
        }
    }

    /**
     * 将 ProductInfo 列表转换为 Map 列表（用于缓存）
     */
    private List<Map<String, Object>> convertProductListToMapList(List<ProductInfo> products) {
        return products.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getId());
            map.put("productNo", product.getProductNo());
            map.put("productName", product.getProductName());
            map.put("productDesc", product.getProductDesc());
            map.put("productImages", product.getProductImages());
            map.put("categoryId", product.getCategoryId());
            map.put("originalPrice", product.getOriginalPrice());
            map.put("salePrice", product.getSalePrice());
            map.put("discountRate", product.getDiscountRate());
            map.put("storeId", product.getStoreId());
            map.put("pickupTimeStart", product.getPickupTimeStart() != null ? product.getPickupTimeStart().toString() : null);
            map.put("pickupTimeEnd", product.getPickupTimeEnd() != null ? product.getPickupTimeEnd().toString() : null);
            map.put("purchaseLimit", product.getPurchaseLimit());
            map.put("status", product.getStatus());
            map.put("shelfTime", product.getShelfTime() != null ? product.getShelfTime().toString() : null);
            map.put("soldCount", product.getSoldCount());
            map.put("totalStock", product.getTotalStock());
            map.put("availableStock", product.getAvailableStock());
            map.put("createTime", product.getCreateTime() != null ? product.getCreateTime().toString() : null);
            map.put("updateTime", product.getUpdateTime() != null ? product.getUpdateTime().toString() : null);
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 将 Map 列表转换为 ProductInfo 列表（从缓存读取）
     */
    private List<ProductInfo> convertMapListToProductList(List<Map<String, Object>> mapList) {
        return mapList.stream().map(map -> {
            ProductInfo product = new ProductInfo();
            product.setId(getLong(map, "id"));
            product.setProductNo((String) map.get("productNo"));
            product.setProductName((String) map.get("productName"));
            product.setProductDesc((String) map.get("productDesc"));
            product.setProductImages((String) map.get("productImages"));
            product.setCategoryId(getLong(map, "categoryId"));
            product.setOriginalPrice(getBigDecimal(map, "originalPrice"));
            product.setSalePrice(getBigDecimal(map, "salePrice"));
            product.setDiscountRate(getBigDecimal(map, "discountRate"));
            product.setStoreId(getLong(map, "storeId"));
            product.setPickupTimeStart(parseLocalTime((String) map.get("pickupTimeStart")));
            product.setPickupTimeEnd(parseLocalTime((String) map.get("pickupTimeEnd")));
            product.setPurchaseLimit(getInteger(map, "purchaseLimit"));
            product.setStatus(getInteger(map, "status"));
            product.setShelfTime(parseDateTime((String) map.get("shelfTime")));
            product.setSoldCount(getInteger(map, "soldCount"));
            product.setTotalStock(getInteger(map, "totalStock"));
            product.setAvailableStock(getInteger(map, "availableStock"));
            product.setCreateTime(parseDateTime((String) map.get("createTime")));
            product.setUpdateTime(parseDateTime((String) map.get("updateTime")));
            return product;
        }).collect(Collectors.toList());
    }

    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        return Long.parseLong(value.toString());
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        return Integer.parseInt(value.toString());
    }

    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Double) return BigDecimal.valueOf((Double) value);
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        return new BigDecimal(value.toString());
    }

    private LocalDateTime parseDateTime(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            return LocalDateTime.parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalTime parseLocalTime(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            return LocalTime.parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户端商品列表
     * 
     * 用户浏览可购买的商品：
     * - 支持按门店筛选
     * - 支持按状态筛选（默认只显示上架商品）
     * - 只显示有库存的商品
     * 
     * @param storeId 门店ID（可选）
     * @param status 商品状态（可选，默认上架）
     * @return 商品列表（按创建时间倒序）
     */
    @Override
    public List<ProductInfo> getUserProducts(Long storeId, Integer status) {
        log.debug("获取用户端商品列表: storeId={}, status={}", storeId, status);
        
        try {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductInfo> wrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            
            // 按门店筛选
            if (storeId != null) {
                wrapper.eq(ProductInfo::getStoreId, storeId);
            }
            
            // 按状态筛选
            if (status != null) {
                wrapper.eq(ProductInfo::getStatus, status);
            } else {
                wrapper.eq(ProductInfo::getStatus, ProductStatus.ONLINE.getCode()); // 默认上架
            }
            
            wrapper.orderByDesc(ProductInfo::getCreateTime);

            List<ProductInfo> products = this.list(wrapper);
            
            // 过滤有库存的商品
            products = products.stream()
                    .filter(p -> {
                        InventoryInfo inventory = inventoryInfoService.getByProductId(p.getId());
                        return inventory != null && inventory.getAvailableStock() > 0;
                    })
                    .collect(Collectors.toList());
            
            log.debug("获取用户端商品列表完成: count={}", products.size());
            return products;
        } catch (Exception e) {
            log.error("获取用户端商品列表异常: storeId={}, status={}, error={}", storeId, status, e.getMessage(), e);
            throw new BusinessException("获取商品列表失败：" + e.getMessage());
        }
    }

    /**
     * 上架商品
     * 
     * 将已下架的商品重新上架，用户端可见可购买
     * 
     * @param productId 商品ID
     * @param merchantId 商户ID（用于权限验证）
     * @throws BusinessException 商品不存在或无权限
     */
    @Override
    public void onlineProduct(Long productId, Long merchantId) {
        log.info("上架商品: productId={}, merchantId={}", productId, merchantId);
        
        ProductInfo product = validateProductOwnership(productId, merchantId);
        product.setStatus(ProductStatus.ONLINE.getCode());
        product.setShelfTime(LocalDateTime.now());
        this.updateById(product);
        
        // 清除商品列表缓存
        clearMerchantProductsCache(merchantId);
        
        log.info("上架商品成功: productId={}", productId);
    }

    /**
     * 下架商品
     * 
     * 将商品下架，用户端不再展示
     * 
     * 注意：下架操作不会影响已有订单
     * - 已下单未支付的用户仍可支付（订单快照解耦）
     * - 已支付未核销的用户仍可核销
     * 
     * @param productId 商品ID
     * @param merchantId 商户ID（用于权限验证）
     * @throws BusinessException 商品不存在或无权限
     */
    @Override
    public void offlineProduct(Long productId, Long merchantId) {
        log.info("下架商品: productId={}, merchantId={}", productId, merchantId);
        
        ProductInfo product = validateProductOwnership(productId, merchantId);
        product.setStatus(ProductStatus.OFFLINE.getCode());
        this.updateById(product);
        
        // 清除商品列表缓存
        clearMerchantProductsCache(merchantId);
        
        log.info("下架商品成功: productId={}", productId);
    }

    /**
     * 验证商品归属
     * 
     * 通过商品关联的门店验证商户是否有权限操作该商品
     * 
     * @param productId 商品ID
     * @param merchantId 商户ID
     * @return 商品实体
     * @throws BusinessException 商品不存在或无权限
     */
    private ProductInfo validateProductOwnership(Long productId, Long merchantId) {
        // 1. 查询商品
        ProductInfo product = this.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 2. 通过门店验证归属
        StoreInfo store = storeInfoService.getById(product.getStoreId());
        if (store == null || !store.getMerchantId().equals(merchantId)) {
            log.warn("无权限操作商品: productId={}, merchantId={}", productId, merchantId);
            throw new BusinessException("无权限操作该商品");
        }
        
        return product;
    }
    
    /**
     * 清除商户商品列表缓存
     * 
     * 商品发生变更时调用，包括：
     * - 创建商品
     * - 更新商品
     * - 上架/下架商品
     * - 库存变更
     * 
     * @param merchantId 商户ID
     */
    private void clearMerchantProductsCache(Long merchantId) {
        cacheService.deleteMerchantProductsCache(merchantId);
    }

    @Override
    public int countByMerchantId(Long merchantId, Integer status) {
        log.debug("统计商户商品数量: merchantId={}, status={}", merchantId, status);
        
        // 1. 查询商户的所有门店
        List<StoreInfo> stores = storeInfoService.listByMerchantId(merchantId);
        if (stores.isEmpty()) {
            return 0;
        }
        
        // 2. 获取所有门店ID
        List<Long> storeIds = stores.stream().map(StoreInfo::getId).collect(Collectors.toList());
        
        // 3. 统计商品数量
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductInfo> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.in(ProductInfo::getStoreId, storeIds);
        
        if (status != null) {
            wrapper.eq(ProductInfo::getStatus, status);
        }
        
        return (int) this.count(wrapper);
    }
}
