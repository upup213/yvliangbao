package com.yvliangbao.common.pojo.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@ApiModel(value = "ProductUpdateDTO", description = "更新商品请求")
public class ProductUpdateDTO implements Serializable {
    
    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Long id;
    
    @ApiModelProperty(value = "门店ID")
    private Long storeId;
    
    @ApiModelProperty(value = "商品名称")
    private String productName;
    
    @ApiModelProperty(value = "商品描述")
    private String productDesc;
    
    @ApiModelProperty(value = "商品图片JSON数组")
    private String productImages;
    
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;
    
    @ApiModelProperty(value = "原价（分）")
    private BigDecimal originalPrice;
    
    @ApiModelProperty(value = "清仓价（分）")
    private BigDecimal salePrice;
    
    @ApiModelProperty(value = "总库存")
    private Integer totalStock;
    
    @ApiModelProperty(value = "取餐开始时间")
    private LocalTime pickupTimeStart;
    
    @ApiModelProperty(value = "取餐结束时间")
    private LocalTime pickupTimeEnd;
    
    @ApiModelProperty(value = "单人限购数量")
    private Integer purchaseLimit;
}
