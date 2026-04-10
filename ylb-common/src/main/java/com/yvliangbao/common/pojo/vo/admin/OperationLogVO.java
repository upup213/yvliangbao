package com.yvliangbao.common.pojo.vo.admin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "OperationLogVO", description = "操作日志视图对象")
public class OperationLogVO implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "日志ID") private Long id;
    @ApiModelProperty(value = "操作人ID") private Long adminId;
    @ApiModelProperty(value = "操作人用户名") private String adminName;
    @ApiModelProperty(value = "操作类型") private String operationType;
    @ApiModelProperty(value = "操作模块") private String module;
    @ApiModelProperty(value = "操作内容(JSON)") private String content;
    @ApiModelProperty(value = "IP地址") private String ip;
    @ApiModelProperty(value = "结果:1成功,0失败") private Integer result;
    @ApiModelProperty(value = "错误信息") private String errorMsg;
    @ApiModelProperty(value = "创建时间") private LocalDateTime createTime;
}
