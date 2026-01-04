package com.second.hand.trading.server.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 自动回复模板实体类
 * 对应表：sh_auto_reply_template
 */
public class AutoReplyTemplateModel implements Serializable {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 用户id（卖家）
     */
    private Long userId;

    /**
     * 关键词（如：自提、价格）
     */
    private String keyword;

    /**
     * 回复内容（如：自提点：3 栋楼下快递柜）
     */
    private String replyContent;

    /**
     * 是否启用（0-禁用；1-启用）
     */
    private Byte isEnabled;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public Byte getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Byte isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

