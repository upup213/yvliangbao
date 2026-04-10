package com.yvliangbao.gateway.controller.user;


import com.yvliangbao.common.pojo.dto.store.NearbyStoreDTO;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.merchant.StoreInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端门店控制器
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "用户端-门店接口")
@RestController
@RequestMapping("/store")
public class UserStoreController {

    @Autowired
    private StoreInfoService storeInfoService;

    @ApiOperation("周边门店列表（用户端）")
    @GetMapping("/nearby")
    public Result<List<NearbyStoreDTO>> nearbyStores(
            @ApiParam("纬度") @RequestParam Double latitude,
            @ApiParam("经度") @RequestParam Double longitude,
            @ApiParam("半径（米）") @RequestParam(defaultValue = "3000") Double radius,
            @ApiParam("排序方式：distance-距离优先，price-价格优先") @RequestParam(defaultValue = "distance") String sortBy,
            @ApiParam("门店类型：1-餐饮，2-烘焙，3-零售，4-其他") @RequestParam(required = false) Integer storeType) {

        List<NearbyStoreDTO> stores = storeInfoService.listNearbyStores(
                latitude, longitude, radius, sortBy, storeType);
        return Result.success(stores);
    }

    @ApiOperation("门店详情（用户端）")
    @GetMapping("/explore/{id}")
    public Result<NearbyStoreDTO> exploreStore(
            @PathVariable Long id,
            @ApiParam("用户纬度") @RequestParam Double latitude,
            @ApiParam("用户经度") @RequestParam Double longitude) {

        NearbyStoreDTO store = storeInfoService.getStoreDetail(id, latitude, longitude);
        return Result.success(store);
    }
}
