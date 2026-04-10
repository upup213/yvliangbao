package com.yvliangbao.common.pojo.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 轮播图实体
 *
 * @author 余量宝
 */
@Data
@TableName("banner_info")
@ApiModel(value = "BannerInfo", description = "轮播图")
public class BannerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "图片URL")
    @TableField("image_url")
    private String imageUrl;

    @ApiModelProperty(value = "跳转类型：0-无跳转，1-商品详情，2-门店详情，3-外部链接")
    @TableField("link_type")
    private Integer linkType;

    @ApiModelProperty(value = "跳转链接")
    @TableField("link_url")
    private String linkUrl;

    @ApiModelProperty(value = "排序（数字越小越靠前）")
    @TableField("sort")
    private Integer sort;

    @ApiModelProperty(value = "状态：0-禁用，1-启用")
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
