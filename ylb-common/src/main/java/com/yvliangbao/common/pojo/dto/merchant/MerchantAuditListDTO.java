package com.yvliangbao.common.pojo.dto.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商户审核列表查询DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MerchantAuditListDTO", description = "商户审核列表查询")
public class MerchantAuditListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户名称（模糊搜索）")
    private String merchantName;

    @ApiModelProperty(value = "联系人手机号")
    private String contactPhone;

    @ApiModelProperty(value = "商户状态：0-待审核，1-正常，2-已驳回，3-已禁用")
    private Integer status;

    @ApiModelProperty(value = "开始日期（创建时间）")
    private String startDate;

    @ApiModelProperty(value = "结束日期（创建时间）")
    private String endDate;

    @ApiModelProperty(value = "当前页码，默认1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量，默认10")
    private Integer pageSize = 10;
}
