package com.yvliangbao.common.pojo.vo.balance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 余额变动记录VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "BalanceLogVO", description = "余额变动记录")
public class BalanceLogVO {

    @ApiModelProperty(value = "流水号")
    private String logNo;

    @ApiModelProperty(value = "变动类型：1-充值，2-消费，3-退款，4-系统赠送")
    private Integer changeType;

    @ApiModelProperty(value = "变动类型描述")
    private String changeTypeDesc;

    @ApiModelProperty(value = "变动金额")
    private BigDecimal changeAmount;

    @ApiModelProperty(value = "变动后余额")
    private BigDecimal afterBalance;

    @ApiModelProperty(value = "备注说明")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
