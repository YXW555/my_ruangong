package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.AutoReplyTemplateModel;

import java.util.List;

/**
 * 自动回复模板服务接口
 */
public interface AutoReplyTemplateService {

    /**
     * 创建自动回复模板
     */
    boolean createTemplate(AutoReplyTemplateModel template);

    /**
     * 更新自动回复模板
     */
    boolean updateTemplate(AutoReplyTemplateModel template);

    /**
     * 删除自动回复模板
     */
    boolean deleteTemplate(Long id, Long userId);

    /**
     * 根据用户ID获取所有模板
     */
    List<AutoReplyTemplateModel> getTemplatesByUserId(Long userId);

    /**
     * 根据用户ID获取启用的模板
     */
    List<AutoReplyTemplateModel> getEnabledTemplatesByUserId(Long userId);

    /**
     * 根据关键词匹配回复内容（简单关键词匹配）
     * @param userId 接收消息的用户ID（卖家）
     * @param messageContent 消息内容
     * @return 匹配到的回复内容，如果没有匹配到返回null
     */
    String matchReply(Long userId, String messageContent);
}

