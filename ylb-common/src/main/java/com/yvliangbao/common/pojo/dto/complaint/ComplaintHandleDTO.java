package com.yvliangbao.common.pojo.dto.complaint;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 处理投诉请求
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "ComplaintHandleDTO", description = "处理投诉请求")
public class ComplaintHandleDTO implements Serializable {

    @ApiModelProperty(value = "客诉ID", required = true)
    @NotNull(message = "客诉ID不能为空")
    private Long complaintId;

    @ApiModelProperty(value = "处理结果", required = true)
    @NotBlank(message = "处理结果不能为空")
    private String handleResult;

    @ApiModelProperty(value = "用户是否满意：0-不满意，1-满意")
    private Integer userSatisfied;

    @ApiModelProperty(value = "用户反馈")
    private String userFeedback;
}
