package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.ChatMessageDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.entity.ChatMessageModel;
import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.UserModel;
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

    @Override
    public boolean sendMessage(ChatMessageModel chatMessageModel) {
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
}


