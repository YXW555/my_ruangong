package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.AutoReplyTemplateDao;
import com.second.hand.trading.server.entity.AutoReplyTemplateModel;
import com.second.hand.trading.server.service.AutoReplyTemplateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 自动回复模板服务实现
 */
@Service
public class AutoReplyTemplateServiceImpl implements AutoReplyTemplateService {

    @Resource
    private AutoReplyTemplateDao autoReplyTemplateDao;

    @Override
    public boolean createTemplate(AutoReplyTemplateModel template) {
        // 检查是否已存在相同关键词的模板
        AutoReplyTemplateModel existing = autoReplyTemplateDao.selectByUserIdAndKeyword(
                template.getUserId(), template.getKeyword());
        if (existing != null) {
            // 如果已存在，更新现有模板
            template.setId(existing.getId());
            template.setUpdateTime(new Date());
            return autoReplyTemplateDao.updateByPrimaryKeySelective(template) == 1;
        }
        // 创建新模板
        Date now = new Date();
        template.setCreateTime(now);
        template.setUpdateTime(now);
        if (template.getIsEnabled() == null) {
            template.setIsEnabled((byte) 1);
        }
        return autoReplyTemplateDao.insert(template) == 1;
    }

    @Override
    public boolean updateTemplate(AutoReplyTemplateModel template) {
        template.setUpdateTime(new Date());
        return autoReplyTemplateDao.updateByPrimaryKeySelective(template) == 1;
    }

    @Override
    public boolean deleteTemplate(Long id, Long userId) {
        // 验证模板是否属于该用户
        AutoReplyTemplateModel template = autoReplyTemplateDao.selectByPrimaryKey(id);
        if (template == null || !template.getUserId().equals(userId)) {
            return false;
        }
        return autoReplyTemplateDao.deleteByPrimaryKey(id) == 1;
    }

    @Override
    public List<AutoReplyTemplateModel> getTemplatesByUserId(Long userId) {
        return autoReplyTemplateDao.selectByUserId(userId);
    }

    @Override
    public List<AutoReplyTemplateModel> getEnabledTemplatesByUserId(Long userId) {
        return autoReplyTemplateDao.selectEnabledByUserId(userId);
    }

    @Override
    public String matchReply(Long userId, String messageContent) {
        if (messageContent == null || messageContent.trim().isEmpty()) {
            return null;
        }
        
        // 获取该用户所有启用的模板
        List<AutoReplyTemplateModel> templates = getEnabledTemplatesByUserId(userId);
        if (templates.isEmpty()) {
            return null;
        }

        // 去除HTML标签，只匹配纯文本
        String plainText = messageContent.replaceAll("<[^>]+>", "").trim().toLowerCase();
        
        // 简单关键词匹配：检查消息中是否包含关键词
        for (AutoReplyTemplateModel template : templates) {
            String keyword = template.getKeyword().toLowerCase().trim();
            if (plainText.contains(keyword)) {
                return template.getReplyContent();
            }
        }
        
        return null;
    }
}

