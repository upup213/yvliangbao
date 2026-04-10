package com.yvliangbao.common.pojo.vo.merchant;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商户审核信息VO
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MerchantAuditVO", description = "商户审核信息")
public class MerchantAuditVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商户ID")
    private Long id;

    @ApiModelProperty(value = "商户编号")
    private String merchantNo;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户类型：1-餐饮，2-烘焙，3-零售，4-其他")
    private Integer merchantType;

    @ApiModelProperty(value = "商户类型描述")
    private String merchantTypeDesc;

    @ApiModelProperty(value = "联系人姓名")
    private String contactName;

    @ApiModelProperty(value = "联系人电话")
    private String contactPhone;

    @ApiModelProperty(value = "营业执照号")
    private String businessLicenseNo;

    @ApiModelProperty(value = "营业执照图片URL")
    private String businessLicenseImg;

    @ApiModelProperty(value = "食品经营许可证号")
    private String foodLicenseNo;

    @ApiModelProperty(value = "食品经营许可证图片URL")
    private String foodLicenseImg;

    @ApiModelProperty(value = "法人姓名")
    private String legalPersonName;

    @ApiModelProperty(value = "法人身份证号")
    private String legalPersonIdCard;

    @ApiModelProperty(value = "法人身份证图片URL")
    private String legalPersonIdCardImg;

    @ApiModelProperty(value = "开户银行")
    private String bankName;

    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayAccount;

    @ApiModelProperty(value = "状态：0-待审核，1-正常，2-已驳回，3-已禁用")
    private Integer status;

    @ApiModelProperty(value = "状态描述")
    private String statusDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核时间")
    private LocalDateTime auditTime;

    @ApiModelProperty(value = "审核人ID")
    private Long auditorId;

    @ApiModelProperty(value = "驳回原因")
    private String rejectReason;

    @ApiModelProperty(value = "封禁原因")
    private String banReason;
}
