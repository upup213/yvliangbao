package com.yvliangbao.common.pojo.entity.im;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息记录实体类
 *
 * @author 余量宝
 */
@Data
@TableName("im_message")
public class ImMessage {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息编号
     */
    private String messageNo;

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 会话编号
     */
    private String sessionNo;

    /**
     * 消息类型：1-文本，2-图片，3-语音，4-订单信息
     */
    private Integer messageType;

    /**
     * 发送者类型：1-用户，2-商户，3-平台客服
     */
    private Integer senderType;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者名称（冗余）
     */
    private String senderName;

    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 接收者类型：1-用户，2-商户，3-平台客服
     */
    private Integer receiverType;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收者名称（冗余）
     */
    private String receiverName;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 附件URL（图片/语音）
     */
    private String attachmentUrl;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 是否已读：0-未读，1-已读
     */
    private Integer isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 消息状态：1-正常，2-撤回，3-删除
     */
    private Integer status;

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
     * 消息类型：文本
     */
    public static final int MESSAGE_TYPE_TEXT = 1;

    /**
     * 消息类型：图片
     */
    public static final int MESSAGE_TYPE_IMAGE = 2;

    /**
     * 消息类型：语音
     */
    public static final int MESSAGE_TYPE_VOICE = 3;

    /**
     * 消息类型：订单信息
     */
    public static final int MESSAGE_TYPE_ORDER = 4;

    /**
     * 状态：正常
     */
    public static final int STATUS_NORMAL = 1;

    /**
     * 状态：撤回
     */
    public static final int STATUS_RECALL = 2;

    /**
     * 状态：删除
     */
    public static final int STATUS_DELETED = 3;

    /**
     * 已读
     */
    public static final int READ = 1;

    /**
     * 未读
     */
    public static final int UNREAD = 0;
}
