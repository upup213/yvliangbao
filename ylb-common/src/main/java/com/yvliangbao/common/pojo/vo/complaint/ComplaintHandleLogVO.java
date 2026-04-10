package com.yvliangbao.common.pojo.vo.complaint;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 处理记录响应
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "ComplaintHandleLogVO", description = "处理记录响应")
public class ComplaintHandleLogVO implements Serializable {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "客诉ID")
    private Long complaintId;

    @ApiModelProperty(value = "操作人ID")
    private Long operatorId;

    @ApiModelProperty(value = "操作人姓名")
    private String operatorName;

    @ApiModelProperty(value = "操作人类型：1-用户，2-商户，3-管理员")
    private Integer operatorType;

    @ApiModelProperty(value = "操作人类型名称")
    private String operatorTypeName;

    @ApiModelProperty(value = "操作类型")
    private String action;

    @ApiModelProperty(value = "操作内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
}
