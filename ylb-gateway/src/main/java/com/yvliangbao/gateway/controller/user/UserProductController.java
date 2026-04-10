package com.yvliangbao.gateway.controller.user;


import com.yvliangbao.common.pojo.entity.merchant.InventoryInfo;
import com.yvliangbao.common.pojo.entity.merchant.ProductInfo;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.merchant.InventoryInfoService;
import com.yvliangbao.common.service.merchant.ProductInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端商品控制器
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "用户端-商品接口")
@RestController
@RequestMapping("/product")
public class UserProductController {

    @Autowired
    private ProductInfoService productInfoService;

    @Autowired
    private InventoryInfoService inventoryInfoService;

    @ApiOperation("商品列表（用户端）")
    @GetMapping("/list")
    public Result<List<ProductInfo>> productList(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Integer status) {
        List<ProductInfo> products = productInfoService.getUserProducts(storeId, status);
        return Result.success(products);
    }

    @ApiOperation("商品详情")
    @GetMapping("/detail/{id}")
    public Result<ProductInfo> getProduct(@PathVariable Long id) {
        ProductInfo product = productInfoService.getById(id);
        if (product == null) {
            return Result.failed("商品不存在");
        }

        // 查询库存信息
        InventoryInfo inventory = inventoryInfoService.getByProductId(id);
        if (inventory != null) {
            product.setTotalStock(inventory.getTotalStock());
            product.setAvailableStock(inventory.getAvailableStock());
        } else {
            product.setTotalStock(0);
            product.setAvailableStock(0);
        }

        return Result.success(product);
    }
}
