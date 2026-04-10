package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商户违规记录实体
 *
 * @author 余量宝
 */
@Data
@TableName("merchant_violation")
@ApiModel(value = "MerchantViolation", description = "商户违规记录")
public class MerchantViolation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商户ID")
    @TableField("merchant_id")
    private Long merchantId;

    @ApiModelProperty(value = "商户名称")
    @TableField("merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "违规类型：1-商品违规，2-订单违规，3-服务违规，4-其他")
    @TableField("violation_type")
    private Integer violationType;

    @ApiModelProperty(value = "违规类型描述")
    private String violationTypeDesc;

    @ApiModelProperty(value = "违规等级：1-轻微，2-一般，3-严重，4-极其严重")
    @TableField("violation_level")
    private Integer violationLevel;

    @ApiModelProperty(value = "违规等级描述")
    private String violationLevelDesc;

    @ApiModelProperty(value = "违规描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "处理方式：1-警告，2-罚款，3-限单，4-封禁")
    @TableField("handle_type")
    private Integer handleType;

    @ApiModelProperty(value = "处理方式描述")
    private String handleTypeDesc;

    @ApiModelProperty(value = "处罚金额（分）")
    @TableField("penalty_amount")
    private Long penaltyAmount;

    @ApiModelProperty(value = "处理备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "处理人ID")
    @TableField("handler_id")
    private Long handlerId;

    @ApiModelProperty(value = "处理人用户名")
    @TableField("handler_name")
    private String handlerName;

    @ApiModelProperty(value = "关联订单ID")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "关联订单号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "处理状态：0-待处理，1-已处理，2-已申诉")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除：0-未删除，1-已删除")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
