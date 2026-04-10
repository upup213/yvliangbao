package com.yvliangbao.common.pojo.dto.complaint;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询投诉请求
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "ComplaintQueryDTO", description = "查询投诉请求")
public class ComplaintQueryDTO implements Serializable {

    @ApiModelProperty(value = "客诉编号")
    private String complaintNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "商户ID")
    private Long merchantId;

    @ApiModelProperty(value = "投诉类型：1-商品质量问题，2-服务态度问题，3-配送问题，4-退款问题，5-其他")
    private Integer complaintType;

    @ApiModelProperty(value = "状态：0-待处理，1-处理中，2-已解决，3-已关闭")
    private Integer status;

    @ApiModelProperty(value = "开始时间（yyyy-MM-dd）")
    private String startDate;

    @ApiModelProperty(value = "结束时间（yyyy-MM-dd）")
    private String endDate;

    @ApiModelProperty(value = "页码（默认1）")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小（默认10）")
    private Integer size = 10;
}
