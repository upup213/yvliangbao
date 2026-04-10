package com.yvliangbao.common.pojo.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户更新 DTO
 *
 * @author 余量宝
 */
@Data
@ApiModel("用户更新请求")
public class UserUpdateDTO {

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像URL")
    private String avatar;
}
