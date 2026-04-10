package com.yvliangbao.common.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 微信登录请求
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "UserLoginDTO", description = "微信登录请求")
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "微信登录code", required = true)
    @NotBlank(message = "微信登录code不能为空")
    private String code;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;
}
