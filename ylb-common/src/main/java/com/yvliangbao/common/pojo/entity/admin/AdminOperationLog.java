package com.yvliangbao.common.pojo.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员操作日志实体
 *
 * @author 余量宝
 */
@Data
@TableName("admin_operation_log")
@ApiModel(value = "AdminOperationLog", description = "管理员操作日志")
public class AdminOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作人ID")
    @TableField("admin_id")
    private Long adminId;

    @ApiModelProperty(value = "操作人用户名")
    @TableField("admin_name")
    private String adminName;

    @ApiModelProperty(value = "操作类型")
    @TableField("operation_type")
    private String operationType;

    @ApiModelProperty(value = "操作模块")
    @TableField("module")
    private String module;

    @ApiModelProperty(value = "操作内容(JSON)")
    @TableField("content")
    private String content;

    @ApiModelProperty(value = "IP地址")
    @TableField("ip")
    private String ip;

    @ApiModelProperty(value = "结果:1成功,0失败")
    @TableField("result")
    private Integer result;

    @ApiModelProperty(value = "错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
