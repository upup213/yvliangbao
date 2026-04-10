package com.yvliangbao.common.service.im;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yvliangbao.common.pojo.entity.im.ImMessage;


/**
 * 消息记录服务接口
 *
 * @author 余量宝
 */
public interface ImMessageService extends IService<ImMessage> {

    /**
     * 保存消息
     */
    void saveMessage(ImMessage message);

    /**
     * 获取会话消息列表
     */
    IPage<ImMessage> getSessionMessages(Long sessionId, Page<ImMessage> page);

    /**
     * 获取会话消息列表（不落库）
     */
    IPage<ImMessage> getSessionMessagesNoStore(Long sessionId, Page<ImMessage> page);

    /**
     * 标记消息为已读
     */
    void markMessagesAsRead(Long sessionId, Long receiverId);

    /**
     * 撤回消息
     */
    void recallMessage(String messageNo);

    /**
     * 删除消息
     */
    void deleteMessage(String messageNo);

    /**
     * 获取未读消息数
     */
    long getUnreadCount(Long userId, Integer userType);
}
