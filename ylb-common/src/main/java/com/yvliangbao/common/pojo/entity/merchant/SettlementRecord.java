package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 结算记录实体类
 *
 * @author 余量宝
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("settlement_record")
public class SettlementRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 结算流水号
     */
    private String settlementNo;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 结算周期开始日期
     */
    private LocalDate periodStart;

    /**
     * 结算周期结束日期
     */
    private LocalDate periodEnd;

    /**
     * 结算总金额（分）
     */
    private Long totalAmount;

    /**
     * 平台服务费（分）
     */
    private Long serviceFee;

    /**
     * 结算金额（分）
     */
    private Long settleAmount;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 退款金额（分）
     */
    private Long refundAmount;

    /**
     * 结算状态：0-待结算，1-待确认，2-已结算，3-已拒绝
     */
    private Integer status;

    /**
     * 结算时间
     */
    private LocalDateTime settleTime;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
