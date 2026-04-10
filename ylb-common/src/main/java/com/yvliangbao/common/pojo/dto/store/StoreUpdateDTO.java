package com.yvliangbao.common.pojo.dto.store;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 更新门店请求
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "StoreUpdateDTO", description = "更新门店请求")
public class StoreUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "门店ID", required = true)
    @NotNull(message = "门店ID不能为空")
    private Long id;

    @ApiModelProperty(value = "门店名称", required = true)
    @NotBlank(message = "门店名称不能为空")
    private String storeName;

    @ApiModelProperty(value = "门店Logo")
    private String storeLogo;

    @ApiModelProperty(value = "联系电话", required = true)
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    @ApiModelProperty(value = "省", required = true)
    @NotBlank(message = "省份不能为空")
    private String province;

    @ApiModelProperty(value = "市", required = true)
    @NotBlank(message = "城市不能为空")
    private String city;

    @ApiModelProperty(value = "区", required = true)
    @NotBlank(message = "区县不能为空")
    private String district;

    @ApiModelProperty(value = "详细地址", required = true)
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    @ApiModelProperty(value = "经度", required = true)
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度", required = true)
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    @ApiModelProperty(value = "营业开始时间", required = true)
    @NotNull(message = "营业开始时间不能为空")
    private LocalTime businessHoursStart;

    @ApiModelProperty(value = "营业结束时间", required = true)
    @NotNull(message = "营业结束时间不能为空")
    private LocalTime businessHoursEnd;

    @ApiModelProperty(value = "门店公告")
    private String storeNotice;

    @ApiModelProperty(value = "门店图片JSON数组")
    private String storeImages;

    @ApiModelProperty(value = "营业状态：0-休息中，1-营业中")
    private Integer businessStatus;
}
