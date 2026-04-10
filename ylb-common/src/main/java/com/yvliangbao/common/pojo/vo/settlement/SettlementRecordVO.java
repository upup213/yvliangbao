package com.yvliangbao.common.pojo.vo.settlement;

import lombok.Data;

import java.io.Serializable;

/**
 * 结算记录VO
 *
 * @author 余量宝
 */
@Data
public class SettlementRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
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
    private String periodStart;

    /**
     * 结算周期结束日期
     */
    private String periodEnd;

    /**
     * 结算总金额（元）
     */
    private Double totalAmount;

    /**
     * 平台服务费（元）
     */
    private Double serviceFee;

    /**
     * 结算金额（元）
     */
    private Double settleAmount;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 退款金额（元）
     */
    private Double refundAmount;

    /**
     * 结算状态：0-待结算，1-待确认，2-已结算，3-已拒绝
     */
    private Integer status;

    /**
     * 结算状态文本
     */
    private String statusText;

    /**
     * 结算时间
     */
    private String settleTime;

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
    private String createTime;
}
