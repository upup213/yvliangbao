package com.yvliangbao.common.pojo.dto.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商户违规记录查询DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MerchantViolationDTO", description = "商户违规记录查询")
public class MerchantViolationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户名称（模糊搜索）")
    private String merchantName;

    @ApiModelProperty(value = "违规类型：1-商品违规，2-订单违规，3-服务违规，4-其他")
    private Integer violationType;

    @ApiModelProperty(value = "违规等级：1-轻微，2-一般，3-严重，4-极其严重")
    private Integer violationLevel;

    @ApiModelProperty(value = "处理状态：0-待处理，1-已处理，2-已申诉")
    private Integer status;

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "当前页码，默认1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量，默认10")
    private Integer pageSize = 10;
}
