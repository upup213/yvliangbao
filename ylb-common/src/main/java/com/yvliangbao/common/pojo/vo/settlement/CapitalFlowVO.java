package com.yvliangbao.common.pojo.vo.settlement;

import lombok.Data;

import java.io.Serializable;

/**
 * 资金流水VO
 *
 * @author 余量宝
 */
@Data
public class CapitalFlowVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
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
     * 流水类型文本
     */
    private String flowTypeText;

    /**
     * 金额（元）
     */
    private Double amount;

    /**
     * 变动前余额（元）
     */
    private Double beforeBalance;

    /**
     * 变动后余额（元）
     */
    private Double afterBalance;

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
    private String createTime;
}
