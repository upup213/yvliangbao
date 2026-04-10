package com.yvliangbao.common.pojo.entity.im;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 客诉处理记录实体
 *
 * @author 余量宝
 */
@Data
@TableName("complaint_handle_log")
@ApiModel(value = "ComplaintHandleLog", description = "客诉处理记录")
public class ComplaintHandleLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "客诉ID")
    @TableField("complaint_id")
    private Long complaintId;

    @ApiModelProperty(value = "操作人ID")
    @TableField("operator_id")
    private Long operatorId;

    @ApiModelProperty(value = "操作人姓名")
    @TableField("operator_name")
    private String operatorName;

    @ApiModelProperty(value = "操作人类型：1-用户，2-商户，3-管理员")
    @TableField("operator_type")
    private Integer operatorType;

    @ApiModelProperty(value = "操作类型")
    @TableField("action")
    private String action;

    @ApiModelProperty(value = "操作内容")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;
}
