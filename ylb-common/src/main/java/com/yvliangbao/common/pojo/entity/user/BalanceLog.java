package com.yvliangbao.common.pojo.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 余额变动记录实体
 *
 * @author 余量宝
 */
@Data
@TableName("balance_log")
@ApiModel(value = "BalanceLog", description = "余额变动记录")
public class BalanceLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "流水号")
    @TableField("log_no")
    private String logNo;

    @ApiModelProperty(value = "用户ID")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "变动类型：1-充值，2-消费，3-退款，4-系统赠送")
    @TableField("change_type")
    private Integer changeType;

    @ApiModelProperty(value = "变动金额（正数增加，负数减少）")
    @TableField("change_amount")
    private BigDecimal changeAmount;

    @ApiModelProperty(value = "变动前余额")
    @TableField("before_balance")
    private BigDecimal beforeBalance;

    @ApiModelProperty(value = "变动后余额")
    @TableField("after_balance")
    private BigDecimal afterBalance;

    @ApiModelProperty(value = "关联单号")
    @TableField("related_no")
    private String relatedNo;

    @ApiModelProperty(value = "备注说明")
    @TableField("remark")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
