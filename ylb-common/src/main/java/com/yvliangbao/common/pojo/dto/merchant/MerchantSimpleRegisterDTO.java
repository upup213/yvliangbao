package com.yvliangbao.common.pojo.dto.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 商户简单注册请求（第一步）
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MerchantSimpleRegisterDTO", description = "商户简单注册请求")
public class MerchantSimpleRegisterDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户名称", required = true)
    @NotBlank(message = "商户名称不能为空")
    private String merchantName;

    @ApiModelProperty(value = "联系人姓名", required = true)
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    @ApiModelProperty(value = "联系电话", required = true)
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String contactPhone;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
}
