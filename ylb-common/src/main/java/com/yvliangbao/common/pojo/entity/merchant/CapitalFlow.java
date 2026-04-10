package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资金流水实体类
 *
 * @author 余量宝
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("capital_flow")
public class CapitalFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流水号
     */
    private String flowNo;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 流水类型：1-收入，2-退款，3-提现，4-服务费扣减
     */
    private Integer flowType;

    /**
     * 金额（分）
     */
    private Long amount;

    /**
     * 变动前余额
     */
    private Long beforeBalance;

    /**
     * 变动后余额
     */
    private Long afterBalance;

    /**
     * 关联单号（订单号/结算号）
     */
    private String relatedNo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
