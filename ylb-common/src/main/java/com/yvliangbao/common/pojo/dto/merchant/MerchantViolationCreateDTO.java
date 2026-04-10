package com.yvliangbao.common.pojo.dto.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建商户违规记录DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MerchantViolationCreateDTO", description = "创建商户违规记录")
public class MerchantViolationCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "商户ID不能为空")
    @ApiModelProperty(value = "商户ID", required = true)
    private Long merchantId;

    @NotNull(message = "违规类型不能为空")
    @ApiModelProperty(value = "违规类型：1-商品违规，2-订单违规，3-服务违规，4-其他", required = true)
    private Integer violationType;

    @NotNull(message = "违规等级不能为空")
    @ApiModelProperty(value = "违规等级：1-轻微，2-一般，3-严重，4-极其严重", required = true)
    private Integer violationLevel;

    @ApiModelProperty(value = "违规描述")
    private String description;

    @ApiModelProperty(value = "处理方式：1-警告，2-罚款，3-封禁")
    private Integer handleType;

    @ApiModelProperty(value = "罚款金额（分）")
    private Long penaltyAmount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "关联订单ID")
    private Long orderId;

    @ApiModelProperty(value = "关联订单编号")
    private String orderNo;
}
