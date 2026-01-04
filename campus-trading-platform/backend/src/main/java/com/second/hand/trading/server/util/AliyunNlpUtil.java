package com.second.hand.trading.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 阿里云智能客服工具类
 * 使用阿里云自然语言处理API进行智能匹配
 */
@Component
public class AliyunNlpUtil {

    @Value("${aliyun.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.access-key-secret:}")
    private String accessKeySecret;

    @Value("${aliyun.region-id:cn-shanghai}")
    private String regionId;

    @Value("${aliyun.nlp-endpoint:nlp.cn-shanghai.aliyuncs.com}")
    private String endpoint;

    private IAcsClient client;
    private boolean isAvailable = false;

    @PostConstruct
    public void init() {
        if (accessKeyId != null && !accessKeyId.isEmpty() 
            && accessKeySecret != null && !accessKeySecret.isEmpty()) {
            try {
                DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
                client = new DefaultAcsClient(profile);
                isAvailable = true;
                System.out.println("[阿里云NLP] 初始化成功，endpoint: " + endpoint);
            } catch (Exception e) {
                System.err.println("[阿里云NLP] 初始化失败: " + e.getMessage());
                isAvailable = false;
            }
        } else {
            System.out.println("[阿里云NLP] AccessKey未配置，将使用本地匹配算法");
            isAvailable = false;
        }
    }

    /**
     * 智能匹配：使用阿里云NLP API进行语义相似度匹配
     * 包含同义词匹配和API相似度匹配
     * 
     * @param userMessage 用户消息
     * @param keywords 关键词列表
     * @param similarityThreshold 相似度阈值
     * @return 匹配结果，包含匹配的关键词和相似度
     */
    public MatchResult smartMatch(String userMessage, List<String> keywords, double similarityThreshold) {
        if (userMessage == null || keywords == null || keywords.isEmpty()) {
            return null;
        }

        String processedMessage = userMessage.toLowerCase().trim();

        // 1. 优先精确匹配（包含关键词）
        for (String keyword : keywords) {
            String processedKeyword = keyword.toLowerCase().trim();
            if (processedMessage.contains(processedKeyword)) {
                System.out.println("[阿里云NLP] 精确匹配成功 - 关键词: " + keyword);
                return new MatchResult(keyword, 1.0, MatchType.EXACT);
            }
        }

        // 2. 同义词包含匹配（重要：处理"价格"和"多少钱"等）
        String synonymMatch = checkSynonymContains(processedMessage, keywords);
        if (synonymMatch != null) {
            System.out.println("[阿里云NLP] 同义词匹配成功 - 关键词: " + synonymMatch);
            return new MatchResult(synonymMatch, 0.9, MatchType.SEMANTIC);
        }

        // 3. 如果阿里云API可用，尝试使用API进行语义相似度匹配
        if (isAvailable) {
            try {
                double maxSimilarity = 0.0;
                String bestMatch = null;

                for (String keyword : keywords) {
                    double similarity = calculateTextSimilarity(userMessage, keyword);
                    System.out.println("[阿里云NLP] 关键词: " + keyword + " -> API相似度: " + String.format("%.4f", similarity));
                    if (similarity > maxSimilarity) {
                        maxSimilarity = similarity;
                        bestMatch = keyword;
                    }
                }

                if (maxSimilarity >= similarityThreshold && bestMatch != null) {
                    System.out.println(String.format(
                        "[阿里云NLP] API匹配成功 - 关键词: %s, 相似度: %.4f", bestMatch, maxSimilarity));
                    return new MatchResult(bestMatch, maxSimilarity, MatchType.SEMANTIC);
                }
            } catch (Exception e) {
                System.err.println("[阿里云NLP] API调用异常，使用本地匹配: " + e.getMessage());
            }
        }

        // 4. 使用增强的本地相似度计算（包含同义词）
        double maxSimilarity = 0.0;
        String bestMatch = null;

        for (String keyword : keywords) {
            double similarity = calculateEnhancedSimilarity(processedMessage, keyword);
            System.out.println("[阿里云NLP] 关键词: " + keyword + " -> 本地相似度: " + String.format("%.4f", similarity));
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatch = keyword;
            }
        }

        if (maxSimilarity >= similarityThreshold && bestMatch != null) {
            System.out.println(String.format(
                "[阿里云NLP] 本地匹配成功 - 关键词: %s, 相似度: %.4f", bestMatch, maxSimilarity));
            return new MatchResult(bestMatch, maxSimilarity, MatchType.SEMANTIC);
        }

        return null;
    }

    /**
     * 计算两个文本的相似度（使用阿里云文本相似度API）
     * 注意：由于阿里云NLP API的具体接口可能因产品版本而异，
     * 这里提供一个通用的实现框架，实际使用时需要根据具体的API文档调整
     */
    private double calculateTextSimilarity(String text1, String text2) {
        if (!isAvailable) {
            return calculateSimpleSimilarity(text1, text2);
        }

        try {
            // 使用阿里云通用请求方式
            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain(endpoint);
            request.setSysVersion("2019-04-08");
            request.setSysAction("GetEmbedding"); // 或使用其他相似度计算接口

            request.putQueryParameter("Text", text1);
            request.putQueryParameter("Text2", text2);

            CommonResponse response = client.getCommonResponse(request);
            String responseData = response.getData();

            JSONObject json = JSON.parseObject(responseData);
            
            // 尝试多种可能的返回格式
            if (json.containsKey("Data")) {
                JSONObject data = json.getJSONObject("Data");
                if (data.containsKey("Similarity")) {
                    return data.getDoubleValue("Similarity");
                }
                if (data.containsKey("similarity")) {
                    return data.getDoubleValue("similarity");
                }
            }
            
            // 如果返回格式不匹配，使用本地计算作为fallback
            return calculateSimpleSimilarity(text1, text2);
        } catch (ClientException e) {
            System.err.println("[阿里云NLP] API调用失败: " + e.getMessage());
            // API调用失败时，使用简单的相似度计算作为fallback
            return calculateSimpleSimilarity(text1, text2);
        } catch (Exception e) {
            System.err.println("[阿里云NLP] 相似度计算异常: " + e.getMessage());
            return calculateSimpleSimilarity(text1, text2);
        }
    }

    /**
     * 增强的相似度计算（包含同义词支持）
     */
    private double calculateEnhancedSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        text1 = text1.toLowerCase().trim();
        text2 = text2.toLowerCase().trim();

        // 如果完全包含，返回高相似度
        if (text1.contains(text2) || text2.contains(text1)) {
            return 0.9;
        }

        // 检查同义词匹配
        Map<String, Set<String>> synonymMap = getSynonymMap();
        if (synonymMap.containsKey(text2)) {
            for (String synonym : synonymMap.get(text2)) {
                if (text1.contains(synonym)) {
                    return 0.85; // 同义词匹配，返回较高相似度
                }
            }
        }
        if (synonymMap.containsKey(text1)) {
            for (String synonym : synonymMap.get(text1)) {
                if (text2.contains(synonym)) {
                    return 0.85;
                }
            }
        }

        // 计算字符级别的Jaccard相似度
        java.util.Set<Character> set1 = new java.util.HashSet<>();
        java.util.Set<Character> set2 = new java.util.HashSet<>();
        
        for (char c : text1.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                set1.add(c);
            }
        }
        for (char c : text2.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                set2.add(c);
            }
        }

        java.util.Set<Character> intersection = new java.util.HashSet<>(set1);
        intersection.retainAll(set2);
        java.util.Set<Character> union = new java.util.HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }

    /**
     * 简单的相似度计算（fallback方案）
     */
    private double calculateSimpleSimilarity(String text1, String text2) {
        return calculateEnhancedSimilarity(text1, text2);
    }

    /**
     * 获取同义词映射表
     */
    private Map<String, Set<String>> getSynonymMap() {
        Map<String, Set<String>> synonymMap = new HashMap<>();
        
        // 价格相关同义词
        Set<String> priceSynonyms = new HashSet<>(Arrays.asList(
            "价钱", "多少钱", "多少", "售价", "卖多少", "卖多少钱", 
            "价格多少", "价格是多少", "多少钱一个", "什么价", "价位"
        ));
        synonymMap.put("价格", priceSynonyms);
        
        // 地址相关同义词
        Set<String> addressSynonyms = new HashSet<>(Arrays.asList(
            "地址", "在哪里", "在哪", "位置", "地点", "什么地方", 
            "自提", "自提点", "取货", "取货地址", "在哪里取"
        ));
        synonymMap.put("地址", addressSynonyms);
        synonymMap.put("自提", addressSynonyms);
        
        // 联系方式相关同义词
        Set<String> contactSynonyms = new HashSet<>(Arrays.asList(
            "电话", "手机", "联系方式", "怎么联系", "联系", 
            "微信", "QQ", "怎么找你"
        ));
        synonymMap.put("联系方式", contactSynonyms);
        synonymMap.put("电话", contactSynonyms);
        
        // 商品状态相关同义词
        Set<String> statusSynonyms = new HashSet<>(Arrays.asList(
            "还有吗", "还有没有", "还有", "是否还有", 
            "在吗", "还在吗", "还有货吗", "还有库存吗"
        ));
        synonymMap.put("库存", statusSynonyms);
        synonymMap.put("还有", statusSynonyms);
        
        return synonymMap;
    }

    /**
     * 检查同义词包含匹配
     */
    private String checkSynonymContains(String userMessage, List<String> keywords) {
        Map<String, Set<String>> synonymMap = getSynonymMap();
        System.out.println("[阿里云NLP] 开始同义词匹配，用户消息: " + userMessage + ", 关键词列表: " + keywords);
        
        for (String keyword : keywords) {
            String lowerKeyword = keyword.toLowerCase().trim();
            System.out.println("[阿里云NLP] 检查关键词: " + keyword + " (小写: " + lowerKeyword + ")");
            
            // 检查关键词的同义词
            if (synonymMap.containsKey(lowerKeyword)) {
                Set<String> synonyms = synonymMap.get(lowerKeyword);
                System.out.println("[阿里云NLP] 找到关键词的同义词: " + synonyms);
                for (String synonym : synonyms) {
                    String lowerSynonym = synonym.toLowerCase();
                    if (userMessage.contains(lowerSynonym)) {
                        System.out.println("[阿里云NLP] 同义词匹配成功: 用户消息包含 '" + synonym + "'，匹配到关键词 '" + keyword + "'");
                        return keyword; // 匹配成功，返回原始关键词
                    }
                }
            } else {
                System.out.println("[阿里云NLP] 关键词 '" + lowerKeyword + "' 在同义词映射中不存在");
            }
        }
        
        System.out.println("[阿里云NLP] 同义词匹配失败，未找到匹配");
        return null;
    }

    /**
     * 检查阿里云API是否可用
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * 匹配结果
     */
    public static class MatchResult {
        private String matchedKeyword;
        private double similarity;
        private MatchType matchType;

        public MatchResult(String matchedKeyword, double similarity, MatchType matchType) {
            this.matchedKeyword = matchedKeyword;
            this.similarity = similarity;
            this.matchType = matchType;
        }

        public String getMatchedKeyword() {
            return matchedKeyword;
        }

        public double getSimilarity() {
            return similarity;
        }

        public MatchType getMatchType() {
            return matchType;
        }
    }

    /**
     * 匹配类型
     */
    public enum MatchType {
        EXACT,      // 精确匹配
        SEMANTIC    // 语义匹配
    }
}

