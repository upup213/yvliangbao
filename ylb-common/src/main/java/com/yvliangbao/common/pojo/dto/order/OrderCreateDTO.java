package com.yvliangbao.common.pojo.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "OrderCreateDTO", description = "创建订单请求")
public class OrderCreateDTO implements Serializable {
    
    @ApiModelProperty(value = "商品ID", required = true)
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @ApiModelProperty(value = "购买数量", required = true)
    @NotNull(message = "购买数量不能为空")
    private Integer quantity;
}
