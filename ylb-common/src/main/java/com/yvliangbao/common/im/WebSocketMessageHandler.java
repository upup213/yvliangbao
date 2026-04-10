package com.yvliangbao.common.im;

import com.alibaba.fastjson.JSON;
import com.yvliangbao.common.pojo.entity.im.ImMessage;
import com.yvliangbao.common.service.im.ImMessageService;
import com.yvliangbao.common.service.im.ImSessionService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * WebSocket 消息处理服务
 *
 * @author 余量宝
 */
@Slf4j
@Component
public class WebSocketMessageHandler {

    @Autowired
    private ImSessionService imSessionService;

    @Autowired
    private ImMessageService imMessageService;

    /**
     * 处理聊天消息
     */
    public void handleChatMessage(WebSocketMessage message) {
        log.debug("处理聊天消息: {}", JSON.toJSONString(message));

        // 1. 保存消息到数据库
        ImMessage imMessage = new ImMessage();
        imMessage.setMessageNo(message.getMessageNo());
        imMessage.setSessionId(message.getSessionId());
        imMessage.setSessionNo(message.getSessionNo());
        imMessage.setMessageType(message.getMessageType() != null ? message.getMessageType() : ImMessage.MESSAGE_TYPE_TEXT);
        imMessage.setSenderId(message.getSenderId());
        imMessage.setSenderType(message.getSenderType());
        imMessage.setReceiverId(message.getReceiverId());
        imMessage.setReceiverType(message.getReceiverType());
        imMessage.setContent(message.getContent());
        imMessage.setAttachmentUrl(message.getAttachmentUrl());
        imMessage.setAttachmentName(message.getAttachmentName());
        imMessage.setStatus(ImMessage.STATUS_NORMAL);
        imMessage.setIsRead(ImMessage.UNREAD);
        imMessage.setCreateTime(LocalDateTime.now());

        // 保存消息
        imMessageService.saveMessage(imMessage);

        // 2. 更新会话信息
        imSessionService.updateSessionLastMessage(
                message.getSessionId(),
                message.getContent(),
                LocalDateTime.now()
        );

        // 3. 更新未读数
        imSessionService.incrementUnreadCount(
                message.getSessionId(),
                message.getReceiverType()
        );

        // 4. 发送消息给接收者
        WebSocketServer.WebSocketMessage chatMsg = WebSocketServer.WebSocketMessage.chatMessage(message);
        WebSocketServer.sendMessage(message.getReceiverId(), message.getReceiverType(), chatMsg);

        // 5. 发送确认给发送者
        WebSocketServer.WebSocketMessage ack = WebSocketServer.WebSocketMessage.success(WebSocketMessage.TYPE_CHAT, "消息已发送");
        ack.setMessageNo(message.getMessageNo());
        Channel senderChannel = WebSocketServer.getChannel(message.getSenderId(), message.getSenderType());
        if (senderChannel != null) {
            senderChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(ack)));
        }

        log.info("消息处理完成: messageNo={}, from={} to={}",
                message.getMessageNo(), message.getSenderId(), message.getReceiverId());
    }

    /**
     * 处理已读回执
     */
    public void handleReadReceipt(WebSocketMessage message) {
        log.debug("处理已读回执: sessionId={}, receiverId={}, receiverType={}",
                message.getSessionId(), message.getReceiverId(), message.getReceiverType());

        // 更新消息已读状态
        if (message.getSessionId() != null && message.getReceiverId() != null) {
            imMessageService.markMessagesAsRead(message.getSessionId(), message.getReceiverId());

            // 减少未读数
            imSessionService.decrementUnreadCount(message.getSessionId(), message.getReceiverType());
        }

        // 通知发送者消息已读
        WebSocketServer.WebSocketMessage ack = WebSocketServer.WebSocketMessage.success(WebSocketMessage.TYPE_READ, "消息已读");
        WebSocketServer.sendMessage(message.getSenderId(), message.getSenderType(), ack);
    }

    /**
     * 处理消息撤回
     */
    public void handleRecallMessage(WebSocketMessage message) {
        log.debug("处理消息撤回: messageNo={}", message.getMessageNo());

        // 撤回消息
        if (message.getMessageNo() != null) {
            imMessageService.recallMessage(message.getMessageNo());

            // 通知接收者消息已撤回
            WebSocketServer.WebSocketMessage recall = WebSocketServer.WebSocketMessage.success(WebSocketMessage.TYPE_RECALL, "消息已撤回");
            recall.setMessageNo(message.getMessageNo());
            WebSocketServer.sendMessage(message.getReceiverId(), message.getReceiverType(), recall);
        }
    }

    /**
     * 处理加入会话
     */
    public void handleJoinSession(WebSocketMessage message) {
        log.debug("处理加入会话: sessionId={}", message.getSessionId());

        if (message.getSessionId() != null) {
            // 将 Channel 与会话关联
            Channel channel = WebSocketServer.getChannel(message.getSenderId(), message.getSenderType());
            if (channel != null) {
                WebSocketServer.addSessionChannel(message.getSessionId(), channel);
            }

            // 返回加入成功
            WebSocketServer.WebSocketMessage ack = WebSocketServer.WebSocketMessage.success(WebSocketMessage.TYPE_JOIN_SESSION, "加入会话成功");
            WebSocketServer.sendMessage(message.getSenderId(), message.getSenderType(), ack);
        }
    }

    /**
     * 处理离开会话
     */
    public void handleLeaveSession(WebSocketMessage message) {
        log.debug("处理离开会话: sessionId={}", message.getSessionId());

        if (message.getSessionId() != null) {
            // 移除会话与 Channel 的关联
            WebSocketServer.removeSessionChannel(message.getSessionId());

            // 返回离开成功
            WebSocketServer.WebSocketMessage ack = WebSocketServer.WebSocketMessage.success(WebSocketMessage.TYPE_LEAVE_SESSION, "离开会话成功");
            WebSocketServer.sendMessage(message.getSenderId(), message.getSenderType(), ack);
        }
    }

    /**
     * 发送系统通知
     */
    public void sendSystemNotice(Long userId, Integer userType, String content) {
        WebSocketMessage notice = new WebSocketMessage();
        notice.setType(WebSocketMessage.TYPE_NOTICE);
        notice.setCode(200);
        notice.setContent(content);
        notice.setMessageType(ImMessage.MESSAGE_TYPE_TEXT);

        WebSocketServer.sendMessage(userId, userType, notice);
    }
}
