package com.yvliangbao.common.pojo.vo.merchant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商户违规记录VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MerchantViolationVO", description = "商户违规记录")
public class MerchantViolationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "违规记录ID")
    private Long id;

    @ApiModelProperty(value = "商户ID")
    private Long merchantId;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "违规类型：1-商品违规，2-订单违规，3-服务违规，4-其他")
    private Integer violationType;

    @ApiModelProperty(value = "违规类型描述")
    private String violationTypeDesc;

    @ApiModelProperty(value = "违规等级：1-轻微，2-一般，3-严重，4-极其严重")
    private Integer violationLevel;

    @ApiModelProperty(value = "违规等级描述")
    private String violationLevelDesc;

    @ApiModelProperty(value = "违规描述")
    private String description;

    @ApiModelProperty(value = "处理方式：1-警告，2-罚款，3-限单，4-封禁")
    private Integer handleType;

    @ApiModelProperty(value = "处理方式描述")
    private String handleTypeDesc;

    @ApiModelProperty(value = "处罚金额（分）")
    private Long penaltyAmount;

    @ApiModelProperty(value = "处理备注")
    private String remark;

    @ApiModelProperty(value = "处理人ID")
    private Long handlerId;

    @ApiModelProperty(value = "处理人用户名")
    private String handlerName;

    @ApiModelProperty(value = "关联订单ID")
    private Long orderId;

    @ApiModelProperty(value = "关联订单号")
    private String orderNo;

    @ApiModelProperty(value = "处理状态：0-待处理，1-已处理，2-已申诉")
    private Integer status;

    @ApiModelProperty(value = "处理状态描述")
    private String statusDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "处理时间")
    private LocalDateTime handleTime;
}
