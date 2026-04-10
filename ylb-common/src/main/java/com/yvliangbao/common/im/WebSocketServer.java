package com.yvliangbao.common.im;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Netty WebSocket 服务器
 *
 * @author 余量宝
 */
@Slf4j
@Component
public class WebSocketServer {

    /**
     * 端口号
     */
    @Value("${im.websocket.port:8888}")
    private int port;

    /**
     * WebSocket 路径
     */
    @Value("${im.websocket.path:/ws}")
    private String websocketPath;

    /**
     * 读取空闲时间（秒）
     */
    @Value("${im.websocket.idle-read-timeout:60}")
    private int idleReadTimeout;

    /**
     * 写空闲时间（秒）
     */
    @Value("${im.websocket.idle-write-timeout:0}")
    private int idleWriteTimeout;

    /**
     *  ping/pong 超时时间（秒）
     */
    @Value("${im.websocket.idle-ping-timeout:0}")
    private int idlePingTimeout;

    /**
     * Boss 线程组
     */
    private EventLoopGroup bossGroup;

    /**
     * Worker 线程组
     */
    private EventLoopGroup workerGroup;

    /**
     * 用户连接会话缓存：key = userId_userType, value = Channel
     */
    public static final ConcurrentHashMap<String, Channel> USER_CHANNELS = new ConcurrentHashMap<>();

    /**
     * 会话ID到 Channel 的映射
     */
    public static final ConcurrentHashMap<Long, Channel> SESSION_CHANNELS = new ConcurrentHashMap<>();

    /**
     * Channel 到用户ID的映射
     */
    public static final ConcurrentHashMap<Channel, UserInfo> CHANNEL_USER_MAP = new ConcurrentHashMap<>();

    /**
     * 用户信息
     */
    @lombok.Data
    public static class UserInfo {
        private Long userId;
        private Integer userType; // 1-用户，2-商户，3-平台客服

        public UserInfo(Long userId, Integer userType) {
            this.userId = userId;
            this.userType = userType;
        }
    }

    /**
     * WebSocket 消息类
     */
    @lombok.Data
    public static class WebSocketMessage {
        // 消息类型常量
        public static final String TYPE_CONNECT = "connect";
        public static final String TYPE_AUTH = "auth";
        public static final String TYPE_CHAT = "chat";
        public static final String TYPE_READ = "read";
        public static final String TYPE_RECALL = "recall";
        public static final String TYPE_HEARTBEAT = "heartbeat";
        public static final String TYPE_JOIN_SESSION = "join_session";
        public static final String TYPE_LEAVE_SESSION = "leave_session";
        public static final String TYPE_NOTICE = "notice";

        private String type;
        private Integer code;
        private String message;
        private Long sessionId;
        private String sessionNo;
        private String messageNo;
        private Long senderId;
        private Integer senderType;
        private Long receiverId;
        private Integer receiverType;
        private Integer messageType;
        private String content;
        private String attachmentUrl;
        private String attachmentName;
        private Object data;

        /**
         * 创建成功消息
         */
        public static WebSocketMessage success(String type, String message) {
            WebSocketMessage msg = new WebSocketMessage();
            msg.setType(type);
            msg.setCode(200);
            msg.setMessage(message);
            return msg;
        }

        /**
         * 创建错误消息
         */
        public static WebSocketMessage error(String message) {
            WebSocketMessage msg = new WebSocketMessage();
            msg.setType("error");
            msg.setCode(500);
            msg.setMessage(message);
            return msg;
        }

        /**
         * 创建认证成功消息
         */
        public static WebSocketMessage authSuccess(Long userId, Integer userType) {
            WebSocketMessage msg = new WebSocketMessage();
            msg.setType(TYPE_AUTH);
            msg.setCode(200);
            msg.setMessage("认证成功");
            msg.setSenderId(userId);
            msg.setSenderType(userType);
            return msg;
        }

        /**
         * 创建聊天消息
         */
        public static WebSocketMessage chatMessage(com.yuliangbao.common.im.WebSocketMessage original) {
            WebSocketMessage msg = new WebSocketMessage();
            msg.setType(TYPE_CHAT);
            msg.setCode(200);
            msg.setSessionId(original.getSessionId());
            msg.setSessionNo(original.getSessionNo());
            msg.setMessageNo(original.getMessageNo());
            msg.setSenderId(original.getSenderId());
            msg.setSenderType(original.getSenderType());
            msg.setReceiverId(original.getReceiverId());
            msg.setReceiverType(original.getReceiverType());
            msg.setMessageType(original.getMessageType());
            msg.setContent(original.getContent());
            msg.setAttachmentUrl(original.getAttachmentUrl());
            msg.setAttachmentName(original.getAttachmentName());
            return msg;
        }
    }

    /**
     * 启动服务器
     */
    @PostConstruct
    public void start() {
        new Thread(() -> {
            try {
                // 创建 Boss 和 Worker 线程组
                bossGroup = new NioEventLoopGroup(1);
                workerGroup = new NioEventLoopGroup();

                // 创建服务器引导
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline pipeline = ch.pipeline();

                                // HTTP 编解码器
                                pipeline.addLast(new HttpServerCodec());

                                // HTTP 聚合器
                                pipeline.addLast(new HttpObjectAggregator(65536));

                                // WebSocket 压缩支持
                                pipeline.addLast(new WebSocketServerCompressionHandler());

                                // WebSocket 协议处理器
                                pipeline.addLast(new WebSocketServerProtocolHandler(
                                        websocketPath,
                                        null,
                                        true,
                                        65536,
                                        true,
                                        false
                                ));

                                // 空闲检测
                                if (idleReadTimeout > 0 || idleWriteTimeout > 0 || idlePingTimeout > 0) {
                                    pipeline.addLast(new IdleStateHandler(
                                            idleReadTimeout,
                                            idleWriteTimeout,
                                            idlePingTimeout,
                                            TimeUnit.SECONDS
                                    ));
                                }

                                // 自定义处理器
                                pipeline.addLast(new WebSocketHandler());
                            }
                        });

                // 绑定端口
                ChannelFuture future = bootstrap.bind(port).sync();
                log.info("WebSocket 服务器已启动，监听端口: {}", port);

                // 等待服务器关闭
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("WebSocket 服务器启动失败: {}", e.getMessage(), e);
            }
        }).start();
    }

    /**
     * 销毁服务器
     */
    @PreDestroy
    public void destroy() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        log.info("WebSocket 服务器已关闭");
    }

    /**
     * 添加用户连接
     */
    public static void addUser(Long userId, Integer userType, Channel channel) {
        String key = userId + "_" + userType;
        USER_CHANNELS.put(key, channel);
        CHANNEL_USER_MAP.put(channel, new UserInfo(userId, userType));
        log.info("用户连接: userId={}, userType={}, channel={}", userId, userType, channel.id());
    }

    /**
     * 移除用户连接
     */
    public static void removeUser(Channel channel) {
        UserInfo userInfo = CHANNEL_USER_MAP.remove(channel);
        if (userInfo != null) {
            String key = userInfo.getUserId() + "_" + userInfo.getUserType();
            USER_CHANNELS.remove(key);
            log.info("用户断开: userId={}, userType={}", userInfo.getUserId(), userInfo.getUserType());
        }
    }

    /**
     * 添加会话频道
     */
    public static void addSessionChannel(Long sessionId, Channel channel) {
        SESSION_CHANNELS.put(sessionId, channel);
    }

    /**
     * 移除会话频道
     */
    public static void removeSessionChannel(Long sessionId) {
        SESSION_CHANNELS.remove(sessionId);
    }

    /**
     * 根据用户ID和类型获取 Channel
     */
    public static Channel getChannel(Long userId, Integer userType) {
        String key = userId + "_" + userType;
        return USER_CHANNELS.get(key);
    }

    /**
     * 发送消息给指定用户
     */
    public static void sendMessage(Long userId, Integer userType, Object message) {
        Channel channel = getChannel(userId, userType);
        if (channel != null && channel.isActive()) {
            String json = com.alibaba.fastjson.JSON.toJSONString(message);
            channel.writeAndFlush(new TextWebSocketFrame(json));
            log.debug("发送消息给 userId={}, userType={}: {}", userId, userType, json);
        }
    }

    /**
     * 发送消息给会话中的所有参与者
     */
    public static void sendMessageToSession(Long sessionId, Object message) {
        Channel channel = SESSION_CHANNELS.get(sessionId);
        if (channel != null && channel.isActive()) {
            String json = com.alibaba.fastjson.JSON.toJSONString(message);
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    /**
     * 广播消息给所有在线用户
     */
    public static void broadcast(Object message) {
        String json = com.alibaba.fastjson.JSON.toJSONString(message);
        for (Channel channel : USER_CHANNELS.values()) {
            if (channel.isActive()) {
                channel.writeAndFlush(new TextWebSocketFrame(json));
            }
        }
    }
}
