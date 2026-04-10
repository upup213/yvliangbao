package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商户信息实体
 *
 * @author 余量宝
 */
@Data
@TableName("merchant_info")
@ApiModel(value = "MerchantInfo", description = "商户信息")
public class MerchantInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商户编号")
    @TableField("merchant_no")
    private String merchantNo;

    @ApiModelProperty(value = "商户名称")
    @TableField("merchant_name")
    private String merchantName;

    @ApiModelProperty(value = "商户类型：1-餐饮，2-烘焙，3-零售，4-其他")
    @TableField("merchant_type")
    private Integer merchantType;

    @ApiModelProperty(value = "联系人姓名")
    @TableField("contact_name")
    private String contactName;

    @ApiModelProperty(value = "联系电话")
    @TableField("contact_phone")
    private String contactPhone;

    @ApiModelProperty(value = "登录密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "营业执照号")
    @TableField("business_license_no")
    private String businessLicenseNo;

    @ApiModelProperty(value = "营业执照图片URL")
    @TableField("business_license_img")
    private String businessLicenseImg;

    @ApiModelProperty(value = "食品经营许可证号")
    @TableField("food_license_no")
    private String foodLicenseNo;

    @ApiModelProperty(value = "食品经营许可证图片URL")
    @TableField("food_license_img")
    private String foodLicenseImg;

    @ApiModelProperty(value = "法人姓名")
    @TableField("legal_person_name")
    private String legalPersonName;

    @ApiModelProperty(value = "法人身份证号")
    @TableField("legal_person_id_card")
    private String legalPersonIdCard;

    @ApiModelProperty(value = "法人身份证正面图片URL")
    @TableField("legal_person_id_card_img_front")
    private String legalPersonIdCardImgFront;

    @ApiModelProperty(value = "法人身份证反面图片URL")
    @TableField("legal_person_id_card_img_back")
    private String legalPersonIdCardImgBack;

    @ApiModelProperty(value = "开户银行")
    @TableField("bank_name")
    private String bankName;

    @ApiModelProperty(value = "银行账号")
    @TableField("bank_account")
    private String bankAccount;

    @ApiModelProperty(value = "支付宝账号")
    @TableField("alipay_account")
    private String alipayAccount;

    @ApiModelProperty(value = "状态：0-待审核，1-正常，2-已驳回，3-已禁用")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "审核时间")
    @TableField("audit_time")
    private LocalDateTime auditTime;

    @ApiModelProperty(value = "审核人ID")
    @TableField("auditor_id")
    private Long auditorId;

    @ApiModelProperty(value = "驳回原因")
    @TableField("reject_reason")
    private String rejectReason;

    @ApiModelProperty(value = "封禁原因")
    @TableField("ban_reason")
    private String banReason;

    @ApiModelProperty(value = "账户余额（元）")
    @TableField("balance")
    private Long balance;

    @ApiModelProperty(value = "版本号（乐观锁）")
    @Version
    @TableField("version")
    private Integer version;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除：0-未删除，1-已删除")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
