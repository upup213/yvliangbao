package com.yvliangbao.common.service.impl.im;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.mapper.im.ImSessionMapper;
import com.yvliangbao.common.pojo.entity.im.ImSession;
import com.yvliangbao.common.service.im.ImSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息会话服务实现类
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class ImSessionServiceImpl extends ServiceImpl<ImSessionMapper, ImSession> implements ImSessionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImSession createOrGetSession(Integer sessionType, Long userId, Long merchantId,
                                        Long storeId, String storeName, Long orderId, String orderNo) {
        // 查询是否已存在会话
        LambdaQueryWrapper<ImSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImSession::getSessionType, sessionType)
                .eq(ImSession::getUserId, userId)
                .eq(ImSession::getStatus, ImSession.STATUS_ONGOING);

        if (sessionType == ImSession.SESSION_TYPE_USER_MERCHANT) {
            wrapper.eq(ImSession::getMerchantId, merchantId);
            if (orderId != null) {
                wrapper.eq(ImSession::getOrderId, orderId);
            }
        }

        ImSession existSession = this.getOne(wrapper);
        if (existSession != null) {
            return existSession;
        }

        // 创建新会话
        ImSession session = new ImSession();
        session.setSessionNo(generateSessionNo());
        session.setSessionType(sessionType);
        session.setUserId(userId);
        session.setMerchantId(merchantId);
        session.setStoreId(storeId);
        session.setStoreName(storeName);
        session.setOrderId(orderId);
        session.setOrderNo(orderNo);
        session.setUnreadUserCount(0);
        session.setUnreadMerchantCount(0);
        session.setUnreadPlatformCount(0);
        session.setStatus(ImSession.STATUS_ONGOING);
        session.setUserDeleted(0);
        session.setMerchantDeleted(0);
        session.setPlatformDeleted(0);
        session.setCreateTime(LocalDateTime.now());

        this.save(session);
        log.info("创建新会话: sessionNo={}, sessionType={}", session.getSessionNo(), sessionType);

        return session;
    }

    @Override
    public IPage<ImSession> getUserSessions(Long userId, Integer userType, Page<ImSession> page) {
        LambdaQueryWrapper<ImSession> wrapper = new LambdaQueryWrapper<>();

        // 根据用户类型筛选
        if (userType == 1) {
            // 用户
            wrapper.eq(ImSession::getUserId, userId)
                    .eq(ImSession::getUserDeleted, 0);
        } else if (userType == 2) {
            // 商户
            wrapper.eq(ImSession::getMerchantId, userId)
                    .eq(ImSession::getMerchantDeleted, 0);
        } else if (userType == 3) {
            // 平台客服
            wrapper.eq(ImSession::getPlatformDeleted, 0);
        }

        wrapper.eq(ImSession::getStatus, ImSession.STATUS_ONGOING)
                .orderByDesc(ImSession::getLastMessageTime);

        return this.page(page, wrapper);
    }

    @Override
    public ImSession getSessionDetail(Long sessionId) {
        return this.getById(sessionId);
    }

    @Override
    public void updateSessionLastMessage(Long sessionId, String lastMessage, LocalDateTime lastMessageTime) {
        baseMapper.updateLastMessage(sessionId, lastMessage, lastMessageTime);
    }

    @Override
    public void incrementUnreadCount(Long sessionId, Integer receiverType) {
        baseMapper.incrementUnreadCount(sessionId, receiverType);
    }

    @Override
    public void decrementUnreadCount(Long sessionId, Integer receiverType) {
        baseMapper.decrementUnreadCount(sessionId, receiverType);
    }

    @Override
    public void resetUnreadCount(Long sessionId, Integer userType) {
        baseMapper.resetUnreadCount(sessionId, userType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endSession(Long sessionId) {
        ImSession session = this.getById(sessionId);
        if (session != null) {
            session.setStatus(ImSession.STATUS_ENDED);
            this.updateById(session);
            log.info("会话已结束: sessionNo={}", session.getSessionNo());
        }
    }

    @Override
    public ImSession getUserMerchantSession(Long userId, Long merchantId) {
        LambdaQueryWrapper<ImSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImSession::getSessionType, ImSession.SESSION_TYPE_USER_MERCHANT)
                .eq(ImSession::getUserId, userId)
                .eq(ImSession::getMerchantId, merchantId)
                .eq(ImSession::getStatus, ImSession.STATUS_ONGOING)
                .orderByDesc(ImSession::getCreateTime)
                .last("LIMIT 1");

        return this.getOne(wrapper);
    }

    @Override
    public ImSession getUserPlatformSession(Long userId) {
        LambdaQueryWrapper<ImSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImSession::getSessionType, ImSession.SESSION_TYPE_USER_PLATFORM)
                .eq(ImSession::getUserId, userId)
                .eq(ImSession::getStatus, ImSession.STATUS_ONGOING)
                .orderByDesc(ImSession::getCreateTime)
                .last("LIMIT 1");

        return this.getOne(wrapper);
    }

    @Override
    public ImSession getMerchantPlatformSession(Long merchantId) {
        LambdaQueryWrapper<ImSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ImSession::getSessionType, ImSession.SESSION_TYPE_MERCHANT_PLATFORM)
                .eq(ImSession::getMerchantId, merchantId)
                .eq(ImSession::getStatus, ImSession.STATUS_ONGOING)
                .orderByDesc(ImSession::getCreateTime)
                .last("LIMIT 1");

        return this.getOne(wrapper);
    }

    /**
     * 生成会话编号
     */
    private String generateSessionNo() {
        return "IM" + System.currentTimeMillis() + String.format("%04d", (int) (Math.random() * 10000));
    }
}
