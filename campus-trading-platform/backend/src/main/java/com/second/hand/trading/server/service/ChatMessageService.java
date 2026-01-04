package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.ChatMessageModel;

import java.util.List;

/**
 * 站内私信服务
 */
public interface ChatMessageService {

    /**
     * 发送一条私信
     */
        boolean sendMessage(ChatMessageModel chatMessageModel);

    /**
     * 发送一条图片私信
     */
    boolean sendImageMessage(ChatMessageModel chatMessageModel);

    /**
     * 当前用户的会话列表（每个会话一条最新记录）
     */
    List<ChatMessageModel> getUserSessionList(Long userId);

    /**
     * 与指定用户在某个闲置下的聊天记录
     */
    List<ChatMessageModel> getChatDetail(Long userId, Long targetUserId, Long idleId);

    /**
     * 获取当前用户的未读消息总数（包括私信和商品留言）
     */
    int getUnreadMessageCount(Long userId);
}


