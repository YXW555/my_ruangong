package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.ChatMessageDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.entity.ChatMessageModel;
import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.UserModel;
import com.second.hand.trading.server.service.AutoReplyTemplateService;
import com.second.hand.trading.server.service.ChatMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Resource
    private ChatMessageDao chatMessageDao;

    @Resource
    private UserDao userDao;

    @Resource
    private IdleItemDao idleItemDao;

    @Resource
    private AutoReplyTemplateService autoReplyTemplateService;

    @Override
    public boolean sendMessage(ChatMessageModel chatMessageModel) {
        boolean success = chatMessageDao.insert(chatMessageModel) == 1;

        // 如果消息发送成功，检查是否需要自动回复
        if (success) {
            System.out.println("[自动回复流程] 消息发送成功，开始检查自动回复...");
            System.out.println("[自动回复流程] 接收方ID: " + chatMessageModel.getToUser() + ", 消息内容: " + chatMessageModel.getContent());
            
            // 获取接收方的自动回复（接收方是卖家，检查是否有匹配的关键词）
            System.out.println("[自动回复流程] 准备调用matchReply，接收方ID: " + chatMessageModel.getToUser() + ", 消息内容: " + chatMessageModel.getContent());
            String autoReply = null;
            try {
                autoReply = autoReplyTemplateService.matchReply(
                        chatMessageModel.getToUser(),
                        chatMessageModel.getContent()
                );
                System.out.println("[自动回复流程] matchReply返回结果: " + (autoReply != null ? ("有匹配: " + autoReply) : "无匹配"));
            } catch (Exception e) {
                System.err.println("[自动回复流程] ❌ matchReply调用异常: " + e.getMessage());
                e.printStackTrace();
            }

            if (autoReply != null && !autoReply.trim().isEmpty()) {
                // 创建自动回复消息
                ChatMessageModel autoReplyMessage = new ChatMessageModel();
                autoReplyMessage.setFromUser(chatMessageModel.getToUser()); // 接收方（卖家）发送回复
                autoReplyMessage.setToUser(chatMessageModel.getFromUser()); // 发送给原发送方（买家）
                autoReplyMessage.setIdleId(chatMessageModel.getIdleId());
                // 在回复内容后添加【自动回复】标识
                String replyWithTag = autoReply + "<br><span style='color: #909399; font-size: 12px;'>【自动回复】</span>";
                autoReplyMessage.setContent(replyWithTag);
                autoReplyMessage.setCreateTime(new Date());
                autoReplyMessage.setIsRead((byte) 0);

                // 发送自动回复
                chatMessageDao.insert(autoReplyMessage);
            }
        }

        return success;
    }

    @Override
    public boolean sendImageMessage(ChatMessageModel chatMessageModel) {
        if (chatMessageModel.getContent() == null || chatMessageModel.getContent().trim().isEmpty()) {
            chatMessageModel.setContent("[图片]");
        }
        return chatMessageDao.insert(chatMessageModel) == 1;
    }

    @Override
    public List<ChatMessageModel> getUserSessionList(Long userId) {
        List<ChatMessageModel> list = chatMessageDao.getAllUserChat(userId);
        if (list.isEmpty()) {
            return list;
        }
        // 按会话分组，每个会话只保留最新一条（getAllUserChat 已经倒序）
        Map<String, ChatMessageModel> sessionMap = new LinkedHashMap<>();
        Set<Long> userIdSet = new HashSet<>();
        Set<Long> idleIdSet = new HashSet<>();

        for (ChatMessageModel msg : list) {
            Long from = msg.getFromUser();
            Long to = msg.getToUser();
            Long idleId = msg.getIdleId();
            Long small = from < to ? from : to;
            Long big = from < to ? to : from;
            Long idleKey = idleId == null ? 0L : idleId;
            String key = small + "_" + big + "_" + idleKey;
            if (!sessionMap.containsKey(key)) {
                sessionMap.put(key, msg);
                userIdSet.add(from);
                userIdSet.add(to);
                if (idleId != null) {
                    idleIdSet.add(idleId);
                }
            }
        }

        List<UserModel> users = userDao.findUserByList(new ArrayList<>(userIdSet));
        Map<Long, UserModel> userMap = new HashMap<>();
        for (UserModel u : users) {
            userMap.put(u.getId(), u);
        }

        if (!idleIdSet.isEmpty()) {
            List<IdleItemModel> idleList = idleItemDao.findIdleByList(new ArrayList<>(idleIdSet));
            Map<Long, IdleItemModel> idleMap = new HashMap<>();
            for (IdleItemModel idle : idleList) {
                idleMap.put(idle.getId(), idle);
            }
            for (ChatMessageModel msg : sessionMap.values()) {
                if (msg.getIdleId() != null) {
                    msg.setIdle(idleMap.get(msg.getIdleId()));
                }
            }
        }

        for (ChatMessageModel msg : sessionMap.values()) {
            msg.setFromU(userMap.get(msg.getFromUser()));
            msg.setToU(userMap.get(msg.getToUser()));
        }

        return new ArrayList<>(sessionMap.values());
    }

    @Override
    public List<ChatMessageModel> getChatDetail(Long userId, Long targetUserId, Long idleId) {
        List<ChatMessageModel> list = chatMessageDao.getChatDetail(userId, targetUserId, idleId);
        if (list.isEmpty()) {
            return list;
        }
        Set<Long> userIdSet = new HashSet<>();
        Set<Long> idleIdSet = new HashSet<>();
        for (ChatMessageModel msg : list) {
            userIdSet.add(msg.getFromUser());
            userIdSet.add(msg.getToUser());
            if (msg.getIdleId() != null) {
                idleIdSet.add(msg.getIdleId());
            }
        }

        List<UserModel> users = userDao.findUserByList(new ArrayList<>(userIdSet));
        Map<Long, UserModel> userMap = new HashMap<>();
        for (UserModel u : users) {
            userMap.put(u.getId(), u);
        }

        Map<Long, IdleItemModel> idleMap = new HashMap<>();
        if (!idleIdSet.isEmpty()) {
            List<IdleItemModel> idleList = idleItemDao.findIdleByList(new ArrayList<>(idleIdSet));
            for (IdleItemModel idle : idleList) {
                idleMap.put(idle.getId(), idle);
            }
        }

        for (ChatMessageModel msg : list) {
            msg.setFromU(userMap.get(msg.getFromUser()));
            msg.setToU(userMap.get(msg.getToUser()));
            if (msg.getIdleId() != null) {
                msg.setIdle(idleMap.get(msg.getIdleId()));
            }
        }

        // 标记为已读
        chatMessageDao.markRead(userId, targetUserId, idleId);
        return list;
    }

    @Override
    public int getUnreadMessageCount(Long userId) {
        // 获取所有发送给当前用户的消息
        List<ChatMessageModel> allMessages = chatMessageDao.getAllUserChat(userId);
        // 统计未读消息数量（isRead = 0 且 toUser = userId）
        int unreadCount = 0;
        for (ChatMessageModel msg : allMessages) {
            if (msg.getToUser().equals(userId) && msg.getIsRead() == 0) {
                unreadCount++;
            }
        }
        return unreadCount;
    }
}


