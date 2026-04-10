package com.yvliangbao.common.pojo.entity.im;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息会话实体类
 *
 * @author 余量宝
 */
@Data
@TableName("im_session")
public class ImSession {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话编号
     */
    private String sessionNo;

    /**
     * 会话类型：1-用户与商户，2-用户与平台客服
     */
    private Integer sessionType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名称（冗余）
     */
    private String merchantName;

    /**
     * 关联门店ID
     */
    private Long storeId;

    /**
     * 门店名称（冗余）
     */
    private String storeName;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 订单编号（冗余）
     */
    private String orderNo;

    /**
     * 最后一条消息内容
     */
    private String lastMessage;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 用户未读数
     */
    private Integer unreadUserCount;

    /**
     * 商户未读数
     */
    private Integer unreadMerchantCount;

    /**
     * 平台未读数
     */
    private Integer unreadPlatformCount;

    /**
     * 状态：0-已结束，1-进行中
     */
    private Integer status;

    /**
     * 用户是否删除：0-否，1-是
     */
    private Integer userDeleted;

    /**
     * 商户是否删除：0-否，1-是
     */
    private Integer merchantDeleted;

    /**
     * 平台是否删除：0-否，1-是
     */
    private Integer platformDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    // ========== 常量 ==========

    /**
     * 会话类型：用户与商户
     */
    public static final int SESSION_TYPE_USER_MERCHANT = 1;

    /**
     * 会话类型：用户与平台客服
     */
    public static final int SESSION_TYPE_USER_PLATFORM = 2;

    /**
     * 会话类型：商户与平台客服
     */
    public static final int SESSION_TYPE_MERCHANT_PLATFORM = 3;

    /**
     * 状态：已结束
     */
    public static final int STATUS_ENDED = 0;

    /**
     * 状态：进行中
     */
    public static final int STATUS_ONGOING = 1;

    // ========== 发送者/接收者类型 ==========

    /**
     * 发送者类型：用户
     */
    public static final int SENDER_TYPE_USER = 1;

    /**
     * 发送者类型：商户
     */
    public static final int SENDER_TYPE_MERCHANT = 2;

    /**
     * 发送者类型：平台客服
     */
    public static final int SENDER_TYPE_PLATFORM = 3;
}
