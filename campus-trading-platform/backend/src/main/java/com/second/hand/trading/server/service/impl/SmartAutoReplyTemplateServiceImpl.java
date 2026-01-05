package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.AutoReplyTemplateDao;
import com.second.hand.trading.server.entity.AutoReplyTemplateModel;
import com.second.hand.trading.server.service.AutoReplyTemplateService;
import com.second.hand.trading.server.util.NlpMatcher;
import com.second.hand.trading.server.util.AliyunNlpUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 增强版自动回复模板服务：支持语义相似度匹配（阿里云优先，本地HanLP备选），
 * 并在不可用时回退到原始的关键词包含匹配。
 */
@Service
@Primary
public class SmartAutoReplyTemplateServiceImpl implements AutoReplyTemplateService {

    @Resource
    private AutoReplyTemplateDao autoReplyTemplateDao;
    @Resource
    private NlpMatcher nlpMatcher;
    @Resource
    private AliyunNlpUtil aliyunNlpUtil;

    @Override
    public boolean createTemplate(AutoReplyTemplateModel template) {
        AutoReplyTemplateModel existing = autoReplyTemplateDao.selectByUserIdAndKeyword(
                template.getUserId(), template.getKeyword());
        if (existing != null) {
            template.setId(existing.getId());
            template.setUpdateTime(new Date());
            return autoReplyTemplateDao.updateByPrimaryKeySelective(template) == 1;
        }
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

        List<AutoReplyTemplateModel> templates = getEnabledTemplatesByUserId(userId);
        if (templates == null || templates.isEmpty()) {
            return null;
        }

        String plainText = messageContent.replaceAll("<[^>]+>", "").trim().toLowerCase();

        try {
            List<String> keywords = new java.util.ArrayList<>();
            for (AutoReplyTemplateModel t : templates) {
                if (t.getKeyword() != null && !t.getKeyword().trim().isEmpty()) {
                    keywords.add(t.getKeyword().trim());
                }
            }

            // 1) aliyun
            try {
                Object aliyunResult = null;
                if (aliyunNlpUtil != null) {
                    aliyunResult = aliyunNlpUtil.smartMatch(plainText, keywords, 0.65);
                }
                if (aliyunResult != null) {
                    java.lang.reflect.Method gm = aliyunResult.getClass().getMethod("getMatchedKeyword");
                    Object matched = gm.invoke(aliyunResult);
                    if (matched != null) {
                        String matchedKeyword = matched.toString();
                        for (AutoReplyTemplateModel template : templates) {
                            if (template.getKeyword() != null && template.getKeyword().equalsIgnoreCase(matchedKeyword)) {
                                return template.getReplyContent();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                System.err.println("[SmartAutoReply] 阿里云匹配异常，继续本地匹配: " + t.getMessage());
            }

            // 2) local nlp
            try {
                Object localResult = null;
                if (nlpMatcher != null) {
                    localResult = nlpMatcher.smartMatch(plainText, keywords, 0.65);
                }
                if (localResult != null) {
                    java.lang.reflect.Method gm2 = localResult.getClass().getMethod("getMatchedKeyword");
                    Object matched2 = gm2.invoke(localResult);
                    if (matched2 != null) {
                        String matchedKeyword = matched2.toString();
                        for (AutoReplyTemplateModel template : templates) {
                            if (template.getKeyword() != null && template.getKeyword().equalsIgnoreCase(matchedKeyword)) {
                                return template.getReplyContent();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                System.err.println("[SmartAutoReply] 本地NLP匹配异常: " + t.getMessage());
            }
        } catch (Exception ex) {
            System.err.println("[SmartAutoReply] 智能匹配流程异常: " + ex.getMessage());
        }

        // fallback simple contains
        for (AutoReplyTemplateModel template : templates) {
            String keyword = template.getKeyword() == null ? "" : template.getKeyword().toLowerCase().trim();
            if (!keyword.isEmpty() && plainText.contains(keyword)) {
                return template.getReplyContent();
            }
        }

        return null;
    }
}

