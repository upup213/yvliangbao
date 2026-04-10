package com.yvliangbao.common.mapper.im;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yvliangbao.common.pojo.entity.im.ImMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 消息记录 Mapper
 *
 * @author 余量宝
 */
@Mapper
public interface ImMessageMapper extends BaseMapper<ImMessage> {

    /**
     * 标记消息为已读
     */
    @Update("UPDATE im_message SET is_read = 1, read_time = #{readTime} " +
            "WHERE session_id = #{sessionId} AND receiver_id = #{receiverId} AND is_read = 0")
    int markAsRead(@Param("sessionId") Long sessionId,
                   @Param("receiverId") Long receiverId,
                   @Param("readTime") LocalDateTime readTime);

    /**
     * 撤回消息
     */
    @Update("UPDATE im_message SET status = 2 WHERE message_no = #{messageNo}")
    int recallMessage(@Param("messageNo") String messageNo);

    /**
     * 删除消息
     */
    @Update("UPDATE im_message SET status = 3 WHERE message_no = #{messageNo}")
    int deleteMessage(@Param("messageNo") String messageNo);
}
