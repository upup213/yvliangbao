package com.yvliangbao.gateway.controller.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yvliangbao.common.pojo.entity.im.ImMessage;
import com.yvliangbao.common.pojo.entity.im.ImSession;
import com.yvliangbao.common.pojo.entity.merchant.MerchantInfo;
import com.yvliangbao.common.pojo.entity.user.UserInfo;
import com.yvliangbao.common.result.Result;
import com.yvliangbao.common.service.im.ImMessageService;
import com.yvliangbao.common.service.im.ImSessionService;
import com.yvliangbao.common.service.merchant.MerchantInfoService;
import com.yvliangbao.common.service.user.UserInfoService;
import com.yvliangbao.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 即时通讯控制器（共用）
 *
 * @author 余量宝
 */
@Slf4j
@Api(tags = "共用-即时通讯接口")
@RestController
@RequestMapping("/im")
public class ImController {

    @Autowired
    private ImSessionService imSessionService;

    @Autowired
    private ImMessageService imMessageService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private MerchantInfoService merchantInfoService;

    @Value("${jwt.token-header:Authorization}")
    private String tokenHeader;

    @GetMapping("/ws/info")
    public Result<Map<String, Object>> getWsInfo(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null) {
            return Result.failed(403, "无法确定用户类型，请重新登录");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("wsUrl", "ws://localhost:8888/ws");
        data.put("token", generateWSToken(userId, userType));
        data.put("userId", userId);
        data.put("userType", userType);

        return Result.success(data);
    }

    // ==================== 会话管理 ====================

    @PostMapping("/session/merchant")
    public Result<ImSession> createMerchantSession(
            HttpServletRequest request,
            @RequestParam Long merchantId,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String orderNo) {

        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null) {
            return Result.failed(403, "无法确定用户类型，请重新登录");
        }

        if (userType != 1) {
            return Result.failed(403, "只有用户才能创建与商户的会话");
        }

        ImSession session = imSessionService.createOrGetSession(
                ImSession.SESSION_TYPE_USER_MERCHANT,
                userId,
                merchantId,
                storeId,
                storeName,
                orderId,
                orderNo
        );

        return Result.success(session);
    }

    @PostMapping("/session/platform")
    public Result<ImSession> createPlatformSession(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null) {
            return Result.failed(403, "无法确定用户类型，请重新登录");
        }

        ImSession existSession = imSessionService.getUserPlatformSession(userId);
        if (existSession != null) {
            return Result.success(existSession);
        }

        ImSession session = imSessionService.createOrGetSession(
                ImSession.SESSION_TYPE_USER_PLATFORM,
                userId,
                null,
                null,
                null,
                null,
                null
        );

        return Result.success(session);
    }

    @PostMapping("/session/merchant/platform")
    public Result<ImSession> createMerchantPlatformSession(HttpServletRequest request) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Long merchantId = Long.valueOf(claims.getSubject());
        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null) {
            return Result.failed(403, "无法确定用户类型，请重新登录");
        }

        if (userType != 2) {
            return Result.failed(403, "只有商户才能创建此会话");
        }

        ImSession existSession = imSessionService.getMerchantPlatformSession(merchantId);
        if (existSession != null) {
            return Result.success(existSession);
        }

        ImSession session = imSessionService.createOrGetSession(
                ImSession.SESSION_TYPE_MERCHANT_PLATFORM,
                null,
                merchantId,
                null,
                null,
                null,
                null
        );

        return Result.success(session);
    }

    @PostMapping("/session/platform/user")
    @PreAuthorize("hasAuthority('im:session') or hasAuthority('*:*')")
    public Result<ImSession> createSessionWithUser(
            HttpServletRequest request,
            @RequestParam Long userId) {

        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null || userType != 3) {
            return Result.failed(403, "只有平台客服才能创建此会话");
        }

        ImSession existSession = imSessionService.getUserPlatformSession(userId);
        if (existSession != null) {
            return Result.success(existSession);
        }

        ImSession session = imSessionService.createOrGetSession(
                ImSession.SESSION_TYPE_USER_PLATFORM,
                userId,
                null,
                null,
                null,
                null,
                null
        );

        return Result.success(session);
    }

    @PostMapping("/session/platform/merchant")
    @PreAuthorize("hasAuthority('im:session') or hasAuthority('*:*')")
    public Result<ImSession> createSessionWithMerchant(
            HttpServletRequest request,
            @RequestParam Long merchantId,
            @RequestParam(required = false) String merchantName) {

        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null || userType != 3) {
            return Result.failed(403, "只有平台客服才能创建此会话");
        }

        ImSession existSession = imSessionService.getPlatformMerchantSession(merchantId);
        if (existSession != null) {
            return Result.success(existSession);
        }

        ImSession session = new ImSession();
        session.setSessionNo(generateSessionNo());
        session.setSessionType(ImSession.SESSION_TYPE_USER_MERCHANT);
        session.setUserId(0L);
        session.setMerchantId(merchantId);
        session.setStoreName(merchantName);
        session.setUnreadUserCount(0);
        session.setUnreadMerchantCount(0);
        session.setUnreadPlatformCount(0);
        session.setStatus(ImSession.STATUS_ONGOING);
        session.setUserDeleted(0);
        session.setMerchantDeleted(0);
        session.setPlatformDeleted(0);
        session.setCreateTime(LocalDateTime.now());

        imSessionService.save(session);
        log.info("平台创建与商户会话: sessionNo={}, merchantId={}", session.getSessionNo(), merchantId);

        return Result.success(session);
    }

    @GetMapping("/session/list")
    public Result<IPage<ImSession>> getSessionList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null) {
            return Result.failed(403, "无法确定用户类型，请重新登录");
        }

        Page<ImSession> pageParam = new Page<>(page, size);
        IPage<ImSession> sessions = imSessionService.getUserSessions(userId, userType, pageParam);

        return Result.success(sessions);
    }

    @GetMapping("/session/{sessionId}")
    public Result<ImSession> getSessionDetail(@PathVariable Long sessionId) {
        ImSession session = imSessionService.getSessionDetail(sessionId);
        if (session == null) {
            return Result.failed(404, "会话不存在");
        }
        return Result.success(session);
    }

    @GetMapping("/message/list")
    public Result<IPage<ImMessage>> getMessageList(
            HttpServletRequest request,
            @RequestParam Long sessionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer size) {

        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        ImSession session = imSessionService.getSessionDetail(sessionId);
        if (session == null) {
            return Result.failed(404, "会话不存在");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null) {
            return Result.failed(403, "无法确定用户类型，请重新登录");
        }

        boolean hasPermission = false;
        if (userType == 1 && session.getUserId().equals(userId)) {
            hasPermission = true;
        } else if (userType == 2 && session.getMerchantId() != null && session.getMerchantId().equals(userId)) {
            hasPermission = true;
        } else if (userType == 3) {
            hasPermission = true;
        }

        if (!hasPermission) {
            return Result.failed(403, "无权限查看该会话");
        }

        imMessageService.markMessagesAsRead(sessionId, userId);
        imSessionService.resetUnreadCount(sessionId, userType);

        Page<ImMessage> pageParam = new Page<>(page, size);
        IPage<ImMessage> messages = imMessageService.getSessionMessages(sessionId, pageParam);

        return Result.success(messages);
    }

    @PostMapping("/message/send")
    public Result<ImMessage> sendMessage(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {

        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer userType = getUserTypeFromClaims(claims);
        if (userType == null) {
            log.error("无法确定用户类型, userId={}", userId);
            return Result.failed(403, "无法确定用户类型，请重新登录");
        }

        Long sessionId = Long.valueOf(params.get("sessionId").toString());
        String content = (String) params.get("content");
        Integer messageType = params.get("messageType") != null ?
            Integer.valueOf(params.get("messageType").toString()) : 1;
        String attachmentUrl = (String) params.get("attachmentUrl");
        String attachmentName = (String) params.get("attachmentName");

        ImSession session = imSessionService.getSessionDetail(sessionId);
        if (session == null) {
            return Result.failed(404, "会话不存在");
        }

        Long receiverId;
        Integer receiverType;
        if (userType == 1) {
            if (session.getSessionType() == ImSession.SESSION_TYPE_USER_MERCHANT) {
                receiverId = session.getMerchantId();
                receiverType = ImSession.SENDER_TYPE_MERCHANT;
            } else {
                receiverId = 1L;
                receiverType = ImSession.SENDER_TYPE_PLATFORM;
            }
        } else if (userType == 2) {
            receiverId = session.getUserId();
            receiverType = ImSession.SENDER_TYPE_USER;
        } else if (userType == 3) {
            receiverId = session.getUserId();
            receiverType = ImSession.SENDER_TYPE_USER;
        } else {
            return Result.failed(403, "无效的用户类型");
        }

        ImMessage message = new ImMessage();
        message.setMessageNo(generateMessageNo());
        message.setSessionId(sessionId);
        message.setSessionNo(session.getSessionNo());
        message.setMessageType(messageType);
        message.setSenderId(userId);
        message.setSenderType(userType);
        message.setReceiverId(receiverId);
        message.setReceiverType(receiverType);
        message.setContent(content);
        message.setAttachmentUrl(attachmentUrl);
        message.setAttachmentName(attachmentName);
        message.setStatus(ImMessage.STATUS_NORMAL);
        message.setIsRead(ImMessage.UNREAD);
        message.setCreateTime(LocalDateTime.now());

        setSenderInfo(message, userId, userType);

        imMessageService.saveMessage(message);
        imSessionService.updateSessionLastMessage(sessionId, content, LocalDateTime.now());
        imSessionService.incrementUnreadCount(sessionId, receiverType);

        return Result.success(message);
    }

    @PostMapping("/message/recall")
    public Result<Void> recallMessage(
            HttpServletRequest request,
            @RequestParam String messageNo) {

        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        imMessageService.recallMessage(messageNo);

        return Result.success(null);
    }

    @PostMapping("/session/end/{sessionId}")
    @PreAuthorize("hasAuthority('im:session:end') or hasAuthority('*:*') or #request.getHeader('Authorization') != null")
    public Result<Void> endSession(HttpServletRequest request, @PathVariable Long sessionId) {
        Claims claims = getClaims(request);
        if (claims == null) {
            return Result.failed(401, "未登录");
        }

        Long userId = Long.valueOf(claims.getSubject());
        Integer userType = getUserTypeFromClaims(claims);

        ImSession session = imSessionService.getSessionDetail(sessionId);
        if (session == null) {
            return Result.failed(404, "会话不存在");
        }

        if (userType == 3) {
            imSessionService.endSession(sessionId);
            return Result.success(null);
        }

        boolean isParticipant = false;
        if (userType == 1 && session.getUserId().equals(userId)) {
            isParticipant = true;
        } else if (userType == 2 && session.getMerchantId() != null && session.getMerchantId().equals(userId)) {
            isParticipant = true;
        }

        if (!isParticipant) {
            return Result.failed(403, "无权限结束该会话");
        }

        imSessionService.endSession(sessionId);
        return Result.success(null);
    }

    @GetMapping("/customer-service/online")
    public Result<Void> getOnlineCustomerServices() {
        return Result.success(null);
    }

    // ==================== 辅助方法 ====================

    private Claims getClaims(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null) {
            return null;
        }
        try {
            return JwtUtil.parseToken(token);
        } catch (Exception e) {
            log.error("解析Token失败: {}", e.getMessage());
            return null;
        }
    }

    private Integer getUserTypeFromClaims(Claims claims) {
        if (claims == null) {
            return null;
        }

        Integer userType = claims.get("userType", Integer.class);
        if (userType != null) {
            return userType;
        }

        String openid = claims.get("openid", String.class);
        if (openid != null) {
            if (openid.startsWith("ws_")) {
                try {
                    return Integer.parseInt(openid.substring(3));
                } catch (NumberFormatException e) {
                    log.warn("无法解析 userType from openid: {}", openid);
                }
            } else {
                return 1;
            }
        }

        String username = claims.get("username", String.class);
        if (username != null) {
            return 3;
        }

        return null;
    }

    private String generateWSToken(Long userId, Integer userType) {
        return JwtUtil.generateToken(userId, "ws_" + userType, userType);
    }

    private String generateMessageNo() {
        return "MSG" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    private String generateSessionNo() {
        return "IM" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }

    private void setSenderInfo(ImMessage message, Long senderId, Integer senderType) {
        if (senderType == 1) {
            UserInfo user = userInfoService.getById(senderId);
            if (user != null) {
                message.setSenderName(user.getNickname());
                message.setSenderAvatar(user.getAvatar());
            }
        } else if (senderType == 2) {
            MerchantInfo merchant = merchantInfoService.getById(senderId);
            if (merchant != null) {
                message.setSenderName(merchant.getMerchantName());
                message.setSenderAvatar(null);
            }
        } else if (senderType == 3) {
            message.setSenderName("平台客服");
            message.setSenderAvatar(null);
        }
    }
}
