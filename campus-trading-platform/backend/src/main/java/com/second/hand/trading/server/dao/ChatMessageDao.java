package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.ChatMessageModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMessageDao {

    int insert(ChatMessageModel record);

    /**
     * 查询当前用户参与的所有聊天记录（按时间倒序）
     */
    List<ChatMessageModel> getAllUserChat(Long userId);

    /**
     * 查询与某个用户在某个闲置下的聊天记录
     */
    List<ChatMessageModel> getChatDetail(@Param("userId") Long userId,
                                         @Param("targetUserId") Long targetUserId,
                                         @Param("idleId") Long idleId);

    /**
     * 将某个会话下对当前用户的未读消息置为已读
     */
    int markRead(@Param("userId") Long userId,
                 @Param("targetUserId") Long targetUserId,
                 @Param("idleId") Long idleId);
}


