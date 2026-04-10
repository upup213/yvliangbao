package com.yvliangbao.common.service.impl.im;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yvliangbao.common.mapper.im.ImMessageMapper;
import com.yvliangbao.common.pojo.entity.im.ImMessage;
import com.yvliangbao.common.service.im.ImMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息记录服务实现类
 *
 * @author 余量宝
 */
@Slf4j
@Service
public class ImMessageServiceImpl extends ServiceImpl<ImMessageMapper, ImMessage> implements ImMessageService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(ImMessage message) {
        this.save(message);
        log.debug("消息保存成功: messageNo={}", message.getMessageNo());
    }

    @Override
    public IPage<ImMessage> getSessionMessages(Long sessionId, Page<ImMessage> page) {
        return this.page(page, new LambdaQueryWrapper<ImMessage>()
                .eq(ImMessage::getSessionId, sessionId)
                .eq(ImMessage::getStatus, ImMessage.STATUS_NORMAL)
                .orderByAsc(ImMessage::getCreateTime));
    }

    @Override
    public IPage<ImMessage> getSessionMessagesNoStore(Long sessionId, Page<ImMessage> page) {
        // 不落库的消息（仅在缓存中），暂时返回空列表
        return new Page<>();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessagesAsRead(Long sessionId, Long receiverId) {
        baseMapper.markAsRead(sessionId, receiverId, LocalDateTime.now());
        log.debug("消息已读: sessionId={}, receiverId={}", sessionId, receiverId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recallMessage(String messageNo) {
        baseMapper.recallMessage(messageNo);
        log.info("消息已撤回: messageNo={}", messageNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(String messageNo) {
        baseMapper.deleteMessage(messageNo);
        log.info("消息已删除: messageNo={}", messageNo);
    }

    @Override
    public long getUnreadCount(Long userId, Integer userType) {
        // 查询会话表中该用户类型的未读总数
        // 这里需要根据实际情况实现
        return 0;
    }
}
