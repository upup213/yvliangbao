package com.yvliangbao.common.mapper.im;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.im.ImSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 消息会话 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface ImSessionMapper extends BaseMapper<ImSession> {

    /**
     * 更新会话最后消息
     */
    int updateLastMessage(@Param("sessionId") Long sessionId,
                          @Param("lastMessage") String lastMessage,
                          @Param("lastMessageTime") LocalDateTime lastMessageTime);

    /**
     * 增加未读数
     */
    int incrementUnreadCount(@Param("sessionId") Long sessionId,
                             @Param("receiverType") Integer receiverType);

    /**
     * 减少未读数
     */
    int decrementUnreadCount(@Param("sessionId") Long sessionId,
                             @Param("receiverType") Integer receiverType);

    /**
     * 重置未读数
     */
    int resetUnreadCount(@Param("sessionId") Long sessionId,
                         @Param("userType") Integer userType);
}
