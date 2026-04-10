package com.yvliangbao.common.pojo.vo.complaint;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投诉详情响应
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "ComplaintVO", description = "投诉详情响应")
public class ComplaintVO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "客诉编号")
    private String complaintNo;

    @ApiModelProperty(value = "关联订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户昵称")
    private String userNickname;

    @ApiModelProperty(value = "用户手机号")
    private String userPhone;

    @ApiModelProperty(value = "商户ID")
    private Long merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "投诉类型：1-商品质量问题，2-服务态度问题，3-配送问题，4-退款问题，5-其他")
    private Integer complaintType;

    @ApiModelProperty(value = "投诉类型名称")
    private String complaintTypeName;

    @ApiModelProperty(value = "投诉内容")
    private String complaintContent;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "图片证据（逗号分隔）")
    private String imageUrls;

    @ApiModelProperty(value = "状态：0-待处理，1-处理中，2-已解决，3-已关闭")
    private Integer status;

    @ApiModelProperty(value = "状态名称")
    private String statusName;

    @ApiModelProperty(value = "处理人ID")
    private Long handlerId;

    @ApiModelProperty(value = "处理人姓名")
    private String handlerName;

    @ApiModelProperty(value = "处理时间")
    private LocalDateTime handleTime;

    @ApiModelProperty(value = "处理结果")
    private String handleResult;

    @ApiModelProperty(value = "用户是否满意：0-不满意，1-满意")
    private Integer userSatisfied;

    @ApiModelProperty(value = "用户反馈")
    private String userFeedback;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
