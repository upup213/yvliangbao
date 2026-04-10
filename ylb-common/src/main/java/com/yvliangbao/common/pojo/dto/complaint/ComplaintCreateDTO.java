package com.yvliangbao.common.pojo.dto.complaint;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建投诉请求
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "ComplaintCreateDTO", description = "创建投诉请求")
public class ComplaintCreateDTO implements Serializable {

    @ApiModelProperty(value = "关联订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID")
    private Long merchantId;

    @ApiModelProperty(value = "投诉类型：1-商品质量问题，2-服务态度问题，3-配送问题，4-退款问题，5-其他", required = true)
    @NotNull(message = "投诉类型不能为空")
    private Integer complaintType;

    @ApiModelProperty(value = "投诉内容", required = true)
    @NotBlank(message = "投诉内容不能为空")
    private String complaintContent;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "图片证据（逗号分隔）")
    private String imageUrls;
}
