package com.yvliangbao.common.service.merchant;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.dto.store.NearbyStoreDTO;
import com.yvliangbao.common.pojo.dto.store.StoreCreateDTO;
import com.yvliangbao.common.pojo.dto.store.StoreUpdateDTO;
import com.yvliangbao.common.pojo.entity.merchant.StoreInfo;


import java.util.List;

/**
 * 门店信息 Service
 *
 * @author 余量宝
 */
public interface StoreInfoService extends IService<StoreInfo> {

    /**
     * 根据商户ID查询门店列表
     *
     * @param merchantId 商户ID
     * @return 门店列表
     */
    List<StoreInfo> listByMerchantId(Long merchantId);

    /**
     * 创建门店
     *
     * @param dto 门店创建信息
     * @param merchantId 商户ID
     * @return 创建的门店
     */
    StoreInfo createStore(StoreCreateDTO dto, Long merchantId);

    /**
     * 更新门店
     *
     * @param dto 门店更新信息
     * @param merchantId 商户ID
     * @return 更新后的门店
     */
    StoreInfo updateStore(StoreUpdateDTO dto, Long merchantId);

    /**
     * 查询周边门店
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param radius    半径（米）
     * @param sortBy    排序方式：distance-距离优先，price-价格优先
     * @param storeType 门店类型筛选（可选）
     * @return 周边门店列表
     */
    List<NearbyStoreDTO> listNearbyStores(double latitude, double longitude,
                                          double radius, String sortBy, Integer storeType);

    /**
     * 根据ID获取门店详情（包含距离）
     *
     * @param storeId   门店ID
     * @param latitude  用户纬度
     * @param longitude 用户经度
     * @return 门店详情
     */
    NearbyStoreDTO getStoreDetail(Long storeId, double latitude, double longitude);
}
