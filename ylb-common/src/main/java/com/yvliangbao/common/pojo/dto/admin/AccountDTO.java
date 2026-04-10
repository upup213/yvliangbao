package com.yvliangbao.common.pojo.dto.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 账号DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "AccountDTO", description = "账号传输对象")
public class AccountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号ID（更新时必填）")
    private Long id;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度4-20个字符")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "用户名必须字母开头，仅包含字母数字下划线")
    private String username;

    @ApiModelProperty(value = "密码（新增时必填）")
    @Size(min = 8, max = 32, message = "密码长度8-32个字符")
    private String password;

    @ApiModelProperty(value = "真实姓名", required = true)
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名最长50个字符")
    private String realName;

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "角色ID列表（逗号分隔）")
    private String roleIds;

    @ApiModelProperty(value = "数据权限范围:1全部,2本部门,3仅本人")
    private Integer dataScope;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    private Integer status;
}
