package com.yvliangbao.common.pojo.dto.merchant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 商户完善资料请求（第二步：资质信息）
 *
 * @author 余量宝
 */
@Data
@ApiModel(value = "MerchantCompleteInfoDTO", description = "商户完善资料请求")
public class MerchantCompleteInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "营业执照号", required = true)
    @NotBlank(message = "营业执照号不能为空")
    private String businessLicenseNo;

    @ApiModelProperty(value = "营业执照图片URL", required = true)
    @NotBlank(message = "营业执照图片不能为空")
    private String businessLicenseImg;

    @ApiModelProperty(value = "食品经营许可证号")
    private String foodLicenseNo;

    @ApiModelProperty(value = "食品经营许可证图片URL")
    private String foodLicenseImg;

    @ApiModelProperty(value = "法人姓名", required = true)
    @NotBlank(message = "法人姓名不能为空")
    private String legalPersonName;

    @ApiModelProperty(value = "法人身份证号", required = true)
    @NotBlank(message = "法人身份证号不能为空")
    private String legalPersonIdCard;

    @ApiModelProperty(value = "法人身份证正面图片URL", required = true)
    @NotBlank(message = "法人身份证正面图片不能为空")
    private String legalPersonIdCardImgFront;

    @ApiModelProperty(value = "法人身份证反面图片URL")
    private String legalPersonIdCardImgBack;

    @ApiModelProperty(value = "开户银行")
    private String bankName;

    @ApiModelProperty(value = "银行账号")
    private String bankAccount;

    @ApiModelProperty(value = "支付宝账号")
    private String alipayAccount;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "商户类型：1-餐饮，2-烘焙，3-零售，4-其他")
    private Integer merchantType;

    @ApiModelProperty(value = "联系人姓名")
    private String contactName;

    @ApiModelProperty(value = "营业执照图片URL（兼容字段）")
    private String businessLicense;
}
