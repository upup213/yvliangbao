package com.yvliangbao.common.im;

import lombok.Data;

import java.io.Serializable;

/**
 * WebSocket 消息传输对象
 *
 * @author 余量宝
 */
@Data
public class WebSocketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者类型：1-用户，2-商户，3-平台客服
     */
    private Integer senderType;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收者类型：1-用户，2-商户，3-平台客服
     */
    private Integer receiverType;

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 会话编号
     */
    private String sessionNo;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息编号
     */
    private String messageNo;

    /**
     * 附件URL
     */
    private String attachmentUrl;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 消息类型：1-文本，2-图片，3-语音，4-订单信息
     */
    private Integer messageType;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String msg;

    // ========== 消息类型常量 ==========

    /**
     * 消息类型：连接
     */
    public static final String TYPE_CONNECT = "connect";

    /**
     * 消息类型：认证
     */
    public static final String TYPE_AUTH = "auth";

    /**
     * 消息类型：私聊消息
     */
    public static final String TYPE_CHAT = "chat";

    /**
     * 消息类型：已读回执
     */
    public static final String TYPE_READ = "read";

    /**
     * 消息类型：撤回消息
     */
    public static final String TYPE_RECALL = "recall";

    /**
     * 消息类型：心跳
     */
    public static final String TYPE_HEARTBEAT = "heartbeat";

    /**
     * 消息类型：通知（系统消息）
     */
    public static final String TYPE_NOTICE = "notice";

    /**
     * 消息类型：进入会话
     */
    public static final String TYPE_JOIN_SESSION = "join_session";

    /**
     * 消息类型：离开会话
     */
    public static final String TYPE_LEAVE_SESSION = "leave_session";

    // ========== 成功响应 ==========

    public static WebSocketMessage success(String type, String msg) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType(type);
        message.setCode(200);
        message.setMsg(msg);
        return message;
    }

    public static WebSocketMessage error(String msg) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType("error");
        message.setCode(500);
        message.setMsg(msg);
        return message;
    }

    public static WebSocketMessage authSuccess(Long userId, Integer userType) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType(TYPE_AUTH);
        message.setCode(200);
        message.setMsg("认证成功");
        message.setSenderId(userId);
        message.setSenderType(userType);
        return message;
    }

    public static WebSocketMessage chatMessage(WebSocketMessage msg) {
        WebSocketMessage message = new WebSocketMessage();
        message.setType(TYPE_CHAT);
        message.setSessionId(msg.getSessionId());
        message.setSessionNo(msg.getSessionNo());
        message.setSenderId(msg.getSenderId());
        message.setSenderType(msg.getSenderType());
        message.setReceiverId(msg.getReceiverId());
        message.setReceiverType(msg.getReceiverType());
        message.setContent(msg.getContent());
        message.setMessageNo(msg.getMessageNo());
        message.setAttachmentUrl(msg.getAttachmentUrl());
        message.setAttachmentName(msg.getAttachmentName());
        message.setMessageType(msg.getMessageType());
        return message;
    }
}
