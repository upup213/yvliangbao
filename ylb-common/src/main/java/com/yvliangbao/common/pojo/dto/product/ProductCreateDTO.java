package com.yvliangbao.common.pojo.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@ApiModel(value = "ProductCreateDTO", description = "创建商品请求")
public class ProductCreateDTO implements Serializable {
    
    @ApiModelProperty(value = "门店ID", required = true)
    @NotNull(message = "门店ID不能为空")
    private Long storeId;
    
    @ApiModelProperty(value = "商品名称", required = true)
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    
    @ApiModelProperty(value = "商品描述")
    private String productDesc;
    
    @ApiModelProperty(value = "商品图片JSON数组")
    private String productImages;
    
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;
    
    @ApiModelProperty(value = "原价（分）", required = true)
    @NotNull(message = "原价不能为空")
    private BigDecimal originalPrice;
    
    @ApiModelProperty(value = "清仓价（分）", required = true)
    @NotNull(message = "清仓价不能为空")
    private BigDecimal salePrice;
    
    @ApiModelProperty(value = "总库存", required = true)
    @NotNull(message = "库存不能为空")
    private Integer totalStock;
    
    @ApiModelProperty(value = "取餐开始时间", required = true)
    @NotNull(message = "取餐开始时间不能为空")
    private LocalTime pickupTimeStart;
    
    @ApiModelProperty(value = "取餐结束时间", required = true)
    @NotNull(message = "取餐结束时间不能为空")
    private LocalTime pickupTimeEnd;
    
    @ApiModelProperty(value = "单人限购数量")
    private Integer purchaseLimit = 1;
}
