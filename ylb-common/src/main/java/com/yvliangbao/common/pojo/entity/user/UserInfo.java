package com.yvliangbao.common.pojo.entity.user;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息实体
 *
 * @author 余量宝
 */
@Data
@TableName("user_info")
@ApiModel(value = "UserInfo", description = "用户信息")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "微信openid")
    @TableField("openid")
    private String openid;

    @ApiModelProperty(value = "微信union_id")
    @TableField("union_id")
    private String unionId;

    @ApiModelProperty(value = "手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "昵称")
    @TableField("nickname")
    private String nickname;

    @ApiModelProperty(value = "头像URL")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "性别：0-未知，1-男，2-女")
    @TableField("gender")
    private Integer gender;

    @ApiModelProperty(value = "虚拟余额（元）")
    @TableField("balance")
    private BigDecimal balance;

    @ApiModelProperty(value = "版本号（乐观锁）")
    @TableField("version")
    private Integer version;

    @ApiModelProperty(value = "拯救食物重量（kg）")
    @TableField("saved_food_weight")
    private BigDecimal savedFoodWeight;

    @ApiModelProperty(value = "减碳量（kg CO₂）")
    @TableField("carbon_reduction")
    private BigDecimal carbonReduction;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "最后登录时间")
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "最后登录IP")
    @TableField("last_login_ip")
    private String lastLoginIp;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除：0-未删除，1-已删除")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
