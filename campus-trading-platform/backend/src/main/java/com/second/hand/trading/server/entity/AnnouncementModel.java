package com.second.hand.trading.server.entity;

import java.io.Serializable;
import java.util.Date;

public class AnnouncementModel implements Serializable {
    private Long id;
    private String title;
    private String content;
    private Long creatorId;
    private Byte creatorRole;
    private Byte isPinned;
    private Date pinTime;
    private Date createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public Byte getCreatorRole() { return creatorRole; }
    public void setCreatorRole(Byte creatorRole) { this.creatorRole = creatorRole; }
    public Byte getIsPinned() { return isPinned; }
    public void setIsPinned(Byte isPinned) { this.isPinned = isPinned; }
    public Date getPinTime() { return pinTime; }
    public void setPinTime(Date pinTime) { this.pinTime = pinTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}

