package com.yvliangbao.common.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户信息响应
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "UserInfoVO", description = "用户信息响应")
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Long id;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @ApiModelProperty(value = "虚拟余额（元）")
    private BigDecimal balance;

    @ApiModelProperty(value = "拯救食物重量（kg）")
    private BigDecimal savedFoodWeight;

    @ApiModelProperty(value = "减碳量（kg CO₂）")
    private BigDecimal carbonReduction;

    @ApiModelProperty(value = "已完成订单数")
    private Integer completedOrders;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "访问令牌")
    private String token;
}
