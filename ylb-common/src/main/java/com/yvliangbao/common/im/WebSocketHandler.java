package com.yvliangbao.common.im;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * WebSocket 消息处理器
 *
 * @author 余量宝
 */
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 消息处理服务
     */
    @Autowired
    private WebSocketMessageHandler messageHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String content = msg.text();
        log.debug("收到 WebSocket 消息: {}", content);

        try {
            WebSocketMessage message = JSON.parseObject(content, WebSocketMessage.class);
            if (message == null) {
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WebSocketServer.WebSocketMessage.error("消息格式错误"))));
                return;
            }

            String type = message.getType();
            switch (type) {
                case WebSocketMessage.TYPE_CONNECT:
                    // 处理连接
                    break;
                case WebSocketMessage.TYPE_AUTH:
                    // 处理认证
                    handleAuth(ctx, message);
                    break;
                case WebSocketMessage.TYPE_CHAT:
                    // 处理聊天消息
                    messageHandler.handleChatMessage(message);
                    break;
                case WebSocketMessage.TYPE_READ:
                    // 处理已读回执
                    messageHandler.handleReadReceipt(message);
                    break;
                case WebSocketMessage.TYPE_RECALL:
                    // 处理消息撤回
                    messageHandler.handleRecallMessage(message);
                    break;
                case WebSocketMessage.TYPE_HEARTBEAT:
                    // 处理心跳
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WebSocketServer.WebSocketMessage.success(WebSocketMessage.TYPE_HEARTBEAT, "pong"))));
                    break;
                case WebSocketMessage.TYPE_JOIN_SESSION:
                    // 处理加入会话
                    messageHandler.handleJoinSession(message);
                    break;
                case WebSocketMessage.TYPE_LEAVE_SESSION:
                    // 处理离开会话
                    messageHandler.handleLeaveSession(message);
                    break;
                default:
                    log.warn("未知消息类型: {}", type);
                    break;
            }
        } catch (Exception e) {
            log.error("处理 WebSocket 消息失败: {}", e.getMessage(), e);
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WebSocketServer.WebSocketMessage.error("处理消息失败"))));
        }
    }

    /**
     * 处理认证
     */
    private void handleAuth(ChannelHandlerContext ctx, WebSocketMessage message) {
        Long userId = message.getSenderId();
        Integer userType = message.getSenderType();

        if (userId == null || userType == null) {
            ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WebSocketServer.WebSocketMessage.error("认证信息不完整"))));
            return;
        }

        // 将用户信息绑定到 Channel
        WebSocketServer.addUser(userId, userType, ctx.channel());

        // 返回认证成功消息
        ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(WebSocketServer.WebSocketMessage.authSuccess(userId, userType))));
        log.info("用户认证成功: userId={}, userType={}", userId, userType);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.debug("新的 WebSocket 连接: {}", ctx.channel().id());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        WebSocketServer.removeUser(ctx.channel());
        log.debug("WebSocket 连接断开: {}", ctx.channel().id());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("Channel 激活: {}", ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("Channel 断开: {}", ctx.channel().id());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.debug("读空闲，关闭连接: {}", ctx.channel().id());
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("WebSocket 异常: {}", cause.getMessage(), cause);
        ctx.close();
    }
}
