package com.yvliangbao.common.pojo.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户统计数据响应
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "UserStatsVO", description = "用户统计数据响应")
public class UserStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ========== 订单统计 ==========
    
    @ApiModelProperty(value = "待支付订单数")
    private Integer unpaidCount;
    
    @ApiModelProperty(value = "待取货订单数（已支付+待取餐）")
    private Integer waitingCount;
    
    @ApiModelProperty(value = "已完成订单数")
    private Integer completedCount;
    
    @ApiModelProperty(value = "已取消订单数")
    private Integer cancelledCount;
    
    @ApiModelProperty(value = "退款中订单数")
    private Integer refundingCount;

    // ========== 金额统计 ==========
    
    @ApiModelProperty(value = "累计消费金额（元）")
    private BigDecimal totalSpent;
    
    @ApiModelProperty(value = "累计节省金额（元）")
    private BigDecimal totalSaved;
    
    @ApiModelProperty(value = "本月消费金额（元）")
    private BigDecimal monthSpent;
    
    @ApiModelProperty(value = "本月节省金额（元）")
    private BigDecimal monthSaved;

    // ========== 环保成就 ==========
    
    @ApiModelProperty(value = "拯救食物重量（kg）")
    private BigDecimal savedFoodWeight;
    
    @ApiModelProperty(value = "减碳量（kg CO₂）")
    private BigDecimal carbonReduction;
    
    @ApiModelProperty(value = "环保行动次数（已完成订单数）")
    private Integer ecoActions;

    // ========== 余额相关 ==========
    
    @ApiModelProperty(value = "账户余额（元）")
    private BigDecimal balance;
    
    @ApiModelProperty(value = "累计充值金额（元）")
    private BigDecimal totalRecharged;
}
