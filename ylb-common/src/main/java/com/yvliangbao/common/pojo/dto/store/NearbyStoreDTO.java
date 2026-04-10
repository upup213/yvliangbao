package com.yvliangbao.common.pojo.dto.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 周边门店 DTO（包含距离信息）
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "NearbyStoreDTO", description = "周边门店信息")
public class NearbyStoreDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "门店ID")
    private Long id;

    @ApiModelProperty(value = "门店名称")
    private String storeName;

    @ApiModelProperty(value = "门店Logo")
    private String storeLogo;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "详细地址")
    private String detailAddress;

    @ApiModelProperty(value = "经度")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度")
    private BigDecimal latitude;

    @ApiModelProperty(value = "营业状态：0-休息中，1-营业中")
    private Integer businessStatus;

    @ApiModelProperty(value = "距离（米）")
    private Double distance;

    @ApiModelProperty(value = "距离描述")
    private String distanceText;

    @ApiModelProperty(value = "在售商品数量")
    private Integer productCount;

    @ApiModelProperty(value = "商户类型：1-餐饮，2-烘焙，3-零售，4-其他")
    private Integer merchantType;
}
