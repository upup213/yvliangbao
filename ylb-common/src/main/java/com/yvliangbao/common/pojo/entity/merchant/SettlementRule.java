package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 结算规则实体类
 *
 * @author 余量宝
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("settlement_rule")
public class SettlementRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 规则值（如费率百分比、周期天数）
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
