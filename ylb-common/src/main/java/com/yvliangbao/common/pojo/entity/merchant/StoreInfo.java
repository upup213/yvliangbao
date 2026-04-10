package com.yvliangbao.common.pojo.entity.merchant;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 门店信息实体
 *
 * @author 余量宝
 */
@Data
@TableName("store_info")
@ApiModel(value = "StoreInfo", description = "门店信息")
public class StoreInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商户ID")
    @TableField("merchant_id")
    private Long merchantId;

    @ApiModelProperty(value = "门店编号")
    @TableField("store_no")
    private String storeNo;

    @ApiModelProperty(value = "门店名称")
    @TableField("store_name")
    private String storeName;

    @ApiModelProperty(value = "门店Logo")
    @TableField("store_logo")
    private String storeLogo;

    @ApiModelProperty(value = "联系电话")
    @TableField("contact_phone")
    private String contactPhone;

    @ApiModelProperty(value = "省")
    @TableField("province")
    private String province;

    @ApiModelProperty(value = "市")
    @TableField("city")
    private String city;

    @ApiModelProperty(value = "区")
    @TableField("district")
    private String district;

    @ApiModelProperty(value = "详细地址")
    @TableField("detail_address")
    private String detailAddress;

    @ApiModelProperty(value = "经度")
    @TableField("longitude")
    private BigDecimal longitude;

    @ApiModelProperty(value = "纬度")
    @TableField("latitude")
    private BigDecimal latitude;

    @ApiModelProperty(value = "营业开始时间")
    @TableField("business_hours_start")
    private LocalTime businessHoursStart;

    @ApiModelProperty(value = "营业结束时间")
    @TableField("business_hours_end")
    private LocalTime businessHoursEnd;

    @ApiModelProperty(value = "门店公告")
    @TableField("store_notice")
    private String storeNotice;

    @ApiModelProperty(value = "门店图片JSON数组")
    @TableField("store_images")
    private String storeImages;

    @ApiModelProperty(value = "营业状态：0-休息中，1-营业中")
    @TableField("business_status")
    private Integer businessStatus;

    @ApiModelProperty(value = "状态：0-禁用，1-正常")
    @TableField("status")
    private Integer status;

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
