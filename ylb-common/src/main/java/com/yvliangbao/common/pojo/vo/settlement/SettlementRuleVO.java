package com.yvliangbao.common.pojo.vo.settlement;

import lombok.Data;

import java.io.Serializable;

/**
 * 结算规则VO
 *
 * @author 余量宝
 */
@Data
public class SettlementRuleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型：1-平台服务费，2-结算周期，3-提现规则
     */
    private Integer ruleType;

    /**
     * 规则内容
     */
    private String ruleContent;

    /**
     * 规则值
     */
    private String ruleValue;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
}
