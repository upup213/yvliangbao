package com.yvliangbao.common.service.impl.merchant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.exception.BusinessException;
import com.yvliangbao.common.mapper.merchant.StoreInfoMapper;
import com.yvliangbao.common.pojo.dto.store.NearbyStoreDTO;
import com.yvliangbao.common.pojo.dto.store.StoreCreateDTO;
import com.yvliangbao.common.pojo.dto.store.StoreUpdateDTO;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.entity.merchant.ProductInfo;
import com.yvliangbao.common.pojo.entity.merchant.StoreInfo;
import com.yvliangbao.common.service.common.CacheService;
import com.yvliangbao.common.service.merchant.MerchantInfoService;
import com.yvliangbao.common.service.merchant.ProductInfoService;
import com.yvliangbao.common.service.merchant.StoreInfoService;
import com.yvliangbao.common.util.GeoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 门店信息 Service 实现
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class StoreInfoServiceImpl extends ServiceImpl<StoreInfoMapper, StoreInfo> implements StoreInfoService {

    @Autowired
    private MerchantInfoService merchantInfoService;

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private CacheService cacheService;

    @Override
    public List<StoreInfo> listByMerchantId(Long merchantId) {
        // 尝试从Redis缓存获取
        List<Map<String, Object>> cachedList = cacheService.getMerchantStoreListCache(merchantId);
        if (cachedList != null && !cachedList.isEmpty()) {
            log.debug("从缓存获取商户门店列表: merchantId={}", merchantId);
            return convertToStoreList(cachedList);
        }

        // 查询数据库
        List<StoreInfo> stores = this.lambdaQuery()
                .eq(StoreInfo::getMerchantId, merchantId)
                .list();

        // 存入缓存
        if (!stores.isEmpty()) {
            List<Map<String, Object>> storeList = convertToMapList(stores);
            cacheService.setMerchantStoreListCache(merchantId, storeList);
        }

        return stores;
    }

    @Override
    public StoreInfo createStore(StoreCreateDTO dto, Long merchantId) {
        log.info("创建门店开始: merchantId={}, storeName={}", merchantId, dto.getStoreName());

        StoreInfo store = new StoreInfo();
        BeanUtils.copyProperties(dto, store);
        store.setMerchantId(merchantId);
        store.setStoreNo(generateStoreNo());
        store.setBusinessStatus(1);  // 默认营业中
        store.setStatus(1);          // 默认正常

        this.save(store);

        // 清除门店列表缓存
        cacheService.deleteMerchantStoreListCache(merchantId);

        log.info("创建门店成功: storeId={}, storeNo={}", store.getId(), store.getStoreNo());
        return store;
    }

    @Override
    public StoreInfo updateStore(StoreUpdateDTO dto, Long merchantId) {
        log.info("更新门店开始: storeId={}, merchantId={}", dto.getId(), merchantId);

        // 查询门店
        StoreInfo store = this.getById(dto.getId());
        if (store == null) {
            throw new BusinessException("门店不存在");
        }

        // 验证归属
        if (!store.getMerchantId().equals(merchantId)) {
            throw new BusinessException("无权修改此门店");
        }

        // 更新字段
        BeanUtils.copyProperties(dto, store, "id", "merchantId", "storeNo", "status", "deleted");

        this.updateById(store);

        // 清除门店列表缓存
        cacheService.deleteMerchantStoreListCache(merchantId);

        log.info("更新门店成功: storeId={}", store.getId());
        return store;
    }

    @Override
    public List<NearbyStoreDTO> listNearbyStores(double latitude, double longitude,
                                                 double radius, String sortBy, Integer storeType) {
        log.info("查询周边门店: lat={}, lon={}, radius={}m, sortBy={}, storeType={}", 
                latitude, longitude, radius, sortBy, storeType);

        // 1. 查询所有正常营业的门店
        List<StoreInfo> stores = this.lambdaQuery()
                .eq(StoreInfo::getStatus, 1)
                .eq(storeType != null, StoreInfo::getBusinessStatus, 1)
                .isNotNull(StoreInfo::getLatitude)
                .isNotNull(StoreInfo::getLongitude)
                .list();

        // 2. 获取商户信息（用于获取商户类型）
        List<Long> merchantIds = stores.stream()
                .map(StoreInfo::getMerchantId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, MerchantInfo> merchantMap = new HashMap<>();
        if (!merchantIds.isEmpty()) {
            List<MerchantInfo> merchants = merchantInfoService.listByIds(merchantIds);
            merchantMap = merchants.stream()
                    .collect(Collectors.toMap(MerchantInfo::getId, m -> m));
        }

        // 3. 按商户类型筛选
        if (storeType != null) {
            final Integer finalStoreType = storeType;
            Map<Long, MerchantInfo> finalMerchantMap = merchantMap;
            stores = stores.stream()
                    .filter(store -> {
                        MerchantInfo merchant = finalMerchantMap.get(store.getMerchantId());
                        return merchant != null && merchant.getMerchantType().equals(finalStoreType);
                    })
                    .collect(Collectors.toList());
        }

        // 4. 计算距离并筛选
        List<NearbyStoreDTO> nearbyStores = new ArrayList<>();
        for (StoreInfo store : stores) {
            double distance = GeoUtil.calculateDistance(
                    latitude, longitude,
                    store.getLatitude(), store.getLongitude()
            );

            // 超出范围则跳过
            if (distance > radius) {
                continue;
            }

            NearbyStoreDTO dto = new NearbyStoreDTO();
            BeanUtils.copyProperties(store, dto);
            dto.setDistance(distance);
            dto.setDistanceText(GeoUtil.formatDistance(distance));

            // 设置商户类型
            MerchantInfo merchant = merchantMap.get(store.getMerchantId());
            if (merchant != null) {
                dto.setMerchantType(merchant.getMerchantType());
            }

            // 查询在售商品数量
            List<ProductInfo> products = productInfoService.getUserProducts(store.getId(), 1);
            dto.setProductCount(products.size());

            nearbyStores.add(dto);
        }

        // 5. 排序
        if ("price".equals(sortBy)) {
            // 价格优先：按最低价格排序（暂时用距离排序替代，需要商品信息）
            nearbyStores.sort(Comparator.comparingDouble(NearbyStoreDTO::getDistance));
        } else {
            // 默认按距离排序
            nearbyStores.sort(Comparator.comparingDouble(NearbyStoreDTO::getDistance));
        }

        log.info("查询到周边门店 {} 家", nearbyStores.size());
        return nearbyStores;
    }

    @Override
    public NearbyStoreDTO getStoreDetail(Long storeId, double latitude, double longitude) {
        log.info("获取门店详情: storeId={}, lat={}, lon={}", storeId, latitude, longitude);

        StoreInfo store = this.getById(storeId);
        if (store == null) {
            throw new BusinessException("门店不存在");
        }

        NearbyStoreDTO dto = new NearbyStoreDTO();
        BeanUtils.copyProperties(store, dto);

        // 计算距离
        if (store.getLatitude() != null && store.getLongitude() != null) {
            double distance = GeoUtil.calculateDistance(
                    latitude, longitude,
                    store.getLatitude().doubleValue(), store.getLongitude().doubleValue()
            );
            dto.setDistance(distance);
            dto.setDistanceText(GeoUtil.formatDistance(distance));
        }

        // 获取商户类型
        MerchantInfo merchant = merchantInfoService.getById(store.getMerchantId());
        if (merchant != null) {
            dto.setMerchantType(merchant.getMerchantType());
        }

        // 查询在售商品数量
        List<ProductInfo> products = productInfoService.getUserProducts(storeId, 1);
        dto.setProductCount(products.size());

        return dto;
    }

    /**
     * 生成门店编号
     */
    private String generateStoreNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "STR" + timestamp + random;
    }

    // ==================== 缓存转换辅助方法 ====================

    /**
     * 将门店列表转换为Map列表（用于缓存）
     */
    private List<Map<String, Object>> convertToMapList(List<StoreInfo> stores) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (StoreInfo store : stores) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", store.getId());
            map.put("merchantId", store.getMerchantId());
            map.put("storeNo", store.getStoreNo());
            map.put("storeName", store.getStoreName());
            map.put("storeLogo", store.getStoreLogo());
            map.put("contactPhone", store.getContactPhone());
            map.put("province", store.getProvince());
            map.put("city", store.getCity());
            map.put("district", store.getDistrict());
            map.put("detailAddress", store.getDetailAddress());
            map.put("longitude", store.getLongitude());
            map.put("latitude", store.getLatitude());
            map.put("businessHoursStart", store.getBusinessHoursStart());
            map.put("businessHoursEnd", store.getBusinessHoursEnd());
            map.put("businessStatus", store.getBusinessStatus());
            map.put("status", store.getStatus());
            result.add(map);
        }
        return result;
    }

    /**
     * 将Map列表转换为门店列表（从缓存读取）
     */
    private List<StoreInfo> convertToStoreList(List<Map<String, Object>> mapList) {
        List<StoreInfo> result = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            StoreInfo store = new StoreInfo();
            store.setId(getLong(map, "id"));
            store.setMerchantId(getLong(map, "merchantId"));
            store.setStoreNo((String) map.get("storeNo"));
            store.setStoreName((String) map.get("storeName"));
            store.setStoreLogo((String) map.get("storeLogo"));
            store.setContactPhone((String) map.get("contactPhone"));
            store.setProvince((String) map.get("province"));
            store.setCity((String) map.get("city"));
            store.setDistrict((String) map.get("district"));
            store.setDetailAddress((String) map.get("detailAddress"));
            store.setLongitude(getBigDecimal(map, "longitude"));
            store.setLatitude(getBigDecimal(map, "latitude"));
            store.setBusinessHoursStart(getLocalTime(map, "businessHoursStart"));
            store.setBusinessHoursEnd(getLocalTime(map, "businessHoursEnd"));
            store.setBusinessStatus(getInteger(map, "businessStatus"));
            store.setStatus(getInteger(map, "status"));
            result.add(store);
        }
        return result;
    }

    private Long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    private java.math.BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return java.math.BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return null;
    }

    private java.time.LocalTime getLocalTime(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value != null) {
            return java.time.LocalTime.parse(value.toString());
        }
        return null;
    }
}
