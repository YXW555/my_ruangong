package com.second.hand.trading.server.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品置顶记录实体类
 */
public class IdleItemPinModel implements Serializable {
    private Long id;
    private Long idleItemId;
    private Long userId;
    private Date pinStartTime;
    private Date pinEndTime;
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdleItemId() {
        return idleItemId;
    }

    public void setIdleItemId(Long idleItemId) {
        this.idleItemId = idleItemId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getPinStartTime() {
        return pinStartTime;
    }

    public void setPinStartTime(Date pinStartTime) {
        this.pinStartTime = pinStartTime;
    }

    public Date getPinEndTime() {
        return pinEndTime;
    }

    public void setPinEndTime(Date pinEndTime) {
        this.pinEndTime = pinEndTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

