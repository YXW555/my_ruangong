package com.second.hand.trading.server.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 站内私信消息
 * 对应表：sh_chat_message
 */
public class ChatMessageModel implements Serializable {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 发送方用户id
     */
    private Long fromUser;

    /**
     * 接收方用户id
     */
    private Long toUser;

    /**
     * 关联的闲置信息（可为空）
     */
    private Long idleId;

    private IdleItemModel idle;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 发送时间
     */
    private Date createTime;

    /**
     * 是否已读（0-未读；1-已读）
     */
    private Byte isRead;

    /**
     * 发送方信息
     */
    private UserModel fromU;

    /**
     * 接收方信息
     */
    private UserModel toU;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUser() {
        return fromUser;
    }

    public void setFromUser(Long fromUser) {
        this.fromUser = fromUser;
    }

    public Long getToUser() {
        return toUser;
    }

    public void setToUser(Long toUser) {
        this.toUser = toUser;
    }

    public Long getIdleId() {
        return idleId;
    }

    public void setIdleId(Long idleId) {
        this.idleId = idleId;
    }

    public IdleItemModel getIdle() {
        return idle;
    }

    public void setIdle(IdleItemModel idle) {
        this.idle = idle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Byte getIsRead() {
        return isRead;
    }

    public void setIsRead(Byte isRead) {
        this.isRead = isRead;
    }

    public UserModel getFromU() {
        return fromU;
    }

    public void setFromU(UserModel fromU) {
        this.fromU = fromU;
    }

    public UserModel getToU() {
        return toU;
    }

    public void setToU(UserModel toU) {
        this.toU = toU;
    }
}


