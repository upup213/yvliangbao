package com.yvliangbao.common.service.im;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.entity.im.ImSession;


/**
 * 消息会话服务接口
 *
 * @author 余量宝
 */
public interface ImSessionService extends IService<ImSession> {

    /**
     * 创建或获取会话
     */
    ImSession createOrGetSession(Integer sessionType, Long userId, Long merchantId,
                                  Long storeId, String storeName, Long orderId, String orderNo);

    /**
     * 获取用户的会话列表
     */
    IPage<ImSession> getUserSessions(Long userId, Integer userType, Page<ImSession> page);

    /**
     * 获取会话详情
     */
    ImSession getSessionDetail(Long sessionId);

    /**
     * 更新会话最后消息
     */
    void updateSessionLastMessage(Long sessionId, String lastMessage, java.time.LocalDateTime lastMessageTime);

    /**
     * 增加未读数
     */
    void incrementUnreadCount(Long sessionId, Integer receiverType);

    /**
     * 减少未读数
     */
    void decrementUnreadCount(Long sessionId, Integer receiverType);

    /**
     * 重置未读数
     */
    void resetUnreadCount(Long sessionId, Integer userType);

    /**
     * 结束会话
     */
    void endSession(Long sessionId);

    /**
     * 获取用户与商户的会话
     */
    ImSession getUserMerchantSession(Long userId, Long merchantId);

    /**
     * 获取用户与平台的会话
     */
    ImSession getUserPlatformSession(Long userId);

    /**
     * 获取商户与平台的会话
     */
    ImSession getMerchantPlatformSession(Long merchantId);

    /**
     * 获取平台与商户的会话（同上，别名）
     */
    default ImSession getPlatformMerchantSession(Long merchantId) {
        return getMerchantPlatformSession(merchantId);
    }
}
