package com.second.hand.trading.server.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * NLP智能匹配工具类
 * 
 * 基于HanLP实现智能关键词匹配和语义相似度计算
 * 
 * 功能特点：
 * 1. 中文分词：将用户消息和关键词进行分词处理
 * 2. 关键词提取：从用户消息中提取关键词
 * 3. 语义相似度：计算文本之间的相似度
 * 4. 智能匹配：结合精确匹配和模糊匹配
 * 
 * @author Auto-generated
 */
@Component
public class NlpMatcher {

    /**
     * 初始化HanLP（可选，HanLP会自动初始化）
     */
    @PostConstruct
    public void init() {
        // HanLP会自动初始化，这里可以添加自定义配置
        System.out.println("NLP匹配器初始化完成");
    }

    /**
     * 智能匹配：结合精确匹配和语义相似度匹配
     * 
     * 匹配策略：
     * 1. 优先精确匹配（包含关键词）
     * 2. 如果精确匹配失败，使用语义相似度匹配
     * 3. 返回相似度最高的匹配结果
     * 
     * @param userMessage 用户消息
     * @param keywords 关键词列表（模板关键词）
     * @param similarityThreshold 相似度阈值（0-1），低于此值不匹配
     * @return 匹配结果，包含匹配的关键词和相似度分数
     */
    public MatchResult smartMatch(String userMessage, List<String> keywords, double similarityThreshold) {
        if (userMessage == null || userMessage.trim().isEmpty() || keywords == null || keywords.isEmpty()) {
            System.out.println("[NLP匹配] 输入参数为空，返回null");
            return null;
        }

        // 文本预处理
        String processedMessage = preprocessText(userMessage);
        System.out.println("[NLP匹配] 用户消息: " + userMessage + " -> 预处理后: " + processedMessage);
        System.out.println("[NLP匹配] 关键词列表: " + keywords);
        System.out.println("[NLP匹配] 相似度阈值: " + similarityThreshold);
        
        // 1. 优先精确匹配
        for (String keyword : keywords) {
            String processedKeyword = preprocessText(keyword);
            if (processedMessage.contains(processedKeyword)) {
                System.out.println("[NLP匹配] 精确匹配成功 - 关键词: " + keyword);
                return new MatchResult(keyword, 1.0, MatchType.EXACT);
            }
        }

        // 1.5 同义词包含匹配（快捷方案）
        String containsSynMatch = checkSynonymContains(processedMessage, keywords);
        if (containsSynMatch != null) {
            System.out.println("[NLP匹配] 同义词包含匹配成功 - 关键词: " + containsSynMatch);
            return new MatchResult(containsSynMatch, 0.9, MatchType.SEMANTIC);
        }

        // 2. 语义相似度匹配
        double maxSimilarity = 0.0;
        String bestMatch = null;
        
        for (String keyword : keywords) {
            double similarity = calculateSimilarity(processedMessage, keyword);
            System.out.println("[NLP匹配] 关键词: " + keyword + " -> 相似度: " + String.format("%.4f", similarity));
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatch = keyword;
            }
        }

        System.out.println("[NLP匹配] 最佳匹配: " + bestMatch + ", 相似度: " + String.format("%.4f", maxSimilarity));

        // 3. 如果相似度达到阈值，返回匹配结果
        if (maxSimilarity >= similarityThreshold) {
            System.out.println("[NLP匹配] 语义匹配成功 - 关键词: " + bestMatch + ", 相似度: " + String.format("%.4f", maxSimilarity));
            return new MatchResult(bestMatch, maxSimilarity, MatchType.SEMANTIC);
        } else {
            System.out.println("[NLP匹配] 相似度未达到阈值，匹配失败");
        }

        return null;
    }

    /**
     * 计算两个文本的相似度
     * 
     * 算法：基于Jaccard相似度 + 关键词重叠度
     * 
     * @param text1 文本1
     * @param text2 文本2
     * @return 相似度分数（0-1）
     */
    public double calculateSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isEmpty() || text2.isEmpty()) {
            return 0.0;
        }

        // 文本预处理
        text1 = preprocessText(text1);
        text2 = preprocessText(text2);

        // 1. 提取关键词（使用HanLP分词）
        Set<String> words1 = extractKeywords(text1);
        Set<String> words2 = extractKeywords(text2);

        if (words1.isEmpty() || words2.isEmpty()) {
            return 0.0;
        }

        // 2. 计算Jaccard相似度
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        if (union.isEmpty()) {
            return 0.0;
        }

        double jaccardSimilarity = (double) intersection.size() / union.size();

        // 3. 计算关键词重叠度（考虑关键词顺序和重要性）
        double keywordOverlap = calculateKeywordOverlap(words1, words2);

        // 3. 语义相似度（HanLP 同义词词典）
        double semanticSim = 0.0;
        try {
            // 通过反射调用 CoreSynonymDictionary.similarity 避免编译期依赖缺失
            Class<?> clazz = Class.forName("com.hankcs.hanlp.dictionary.synonym.CoreSynonymDictionary");
            java.lang.reflect.Method method = clazz.getMethod("similarity", String.class, String.class);
            Object sim = method.invoke(null, text1, text2);
            if (sim instanceof Number) {
                semanticSim = ((Number) sim).doubleValue();
                if (Double.isNaN(semanticSim)) {
                    semanticSim = 0.0;
                }
            }
        } catch (Exception e) {
            // 反射失败时，使用基于关键词的语义相似度作为替代
            semanticSim = calculateSemanticSimilarityFallback(words1, words2);
        }

        // 4. 综合相似度（加权平均）
        // 权重：Jaccard 0.4 + Overlap 0.2 + 语义 0.4
        return 0.4 * jaccardSimilarity + 0.2 * keywordOverlap + 0.4 * semanticSim;
    }

    /**
     * 提取文本关键词
     * 
     * 使用HanLP进行中文分词，过滤停用词和标点符号
     * 
     * @param text 文本
     * @return 关键词集合
     */
    public Set<String> extractKeywords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new HashSet<>();
        }

        try {
            // 使用HanLP进行分词
            List<Term> termList = HanLP.segment(text);
            
            // 过滤：只保留名词、动词、形容词等有意义的词，过滤停用词和标点
            return termList.stream()
                    .filter(term -> {
                        String nature = term.nature.toString();
                        // 保留：名词(n)、动词(v)、形容词(a)、数词(m)
                        return nature.startsWith("n") || nature.startsWith("v") 
                                || nature.startsWith("a") || nature.startsWith("m");
                    })
                    .map(term -> term.word.toLowerCase().trim())
                    .filter(word -> word.length() > 1) // 过滤单字
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            // 如果HanLP分词失败，回退到简单分词
            return simpleTokenize(text);
        }
    }

    /**
     * 简单分词（回退方案）
     * 当HanLP不可用时使用
     */
    private Set<String> simpleTokenize(String text) {
        Set<String> words = new HashSet<>();
        // 简单按空格和标点分割
        String[] tokens = text.split("[\\s\\p{Punct}]+");
        for (String token : tokens) {
            if (token.length() > 1) {
                words.add(token.toLowerCase());
            }
        }
        return words;
    }

    /**
     * 计算关键词重叠度
     */
    private double calculateKeywordOverlap(Set<String> words1, Set<String> words2) {
        if (words1.isEmpty() || words2.isEmpty()) {
            return 0.0;
        }

        int commonCount = 0;
        for (String word : words1) {
            if (words2.contains(word)) {
                commonCount++;
            }
        }

        int maxSize = Math.max(words1.size(), words2.size());
        return maxSize > 0 ? (double) commonCount / maxSize : 0.0;
    }

    /**
     * 语义相似度回退方案（当HanLP同义词词典不可用时）
     * 基于同义词映射表计算相似度
     */
    private Map<String, Set<String>> getSynonymMap() {
        Map<String, Set<String>> synonymMap = new HashMap<>();
        Set<String> priceSynonyms = new HashSet<>(Arrays.asList("价钱", "多少钱", "多少", "售价", "卖多少", "卖多少钱"));
        synonymMap.put("价格", priceSynonyms);
        // 可以继续添加其他关键词的同义词
        // synonymMap.put("自提", new HashSet<>(Arrays.asList("自己拿", "上门取")));
        return synonymMap;
    }

    private String checkSynonymContains(String userMessage, List<String> keywords) {
        Map<String, Set<String>> synonymMap = getSynonymMap();
        for (String keyword : keywords) {
            // 检查关键词的同义词
            if (synonymMap.containsKey(keyword)) {
                for (String synonym : synonymMap.get(keyword)) {
                    if (userMessage.contains(synonym)) {
                        return keyword; // 匹配成功，返回原始关键词
                    }
                }
            }
        }
        return null;
    }

    private double calculateSemanticSimilarityFallback(Set<String> words1, Set<String> words2) {
        if (words1.isEmpty() || words2.isEmpty()) {
            return 0.0;
        }

        // 定义常见同义词映射（针对价格相关）
        Map<String, Set<String>> synonymMap = new HashMap<>();
        synonymMap.put("价格", new HashSet<>(Arrays.asList("价钱", "多少钱", "多少", "售价", "卖多少", "卖多少钱")));
        synonymMap.put("多少钱", new HashSet<>(Arrays.asList("价格", "价钱", "多少", "售价", "卖多少", "卖多少钱")));
        synonymMap.put("价钱", new HashSet<>(Arrays.asList("价格", "多少钱", "多少", "售价", "卖多少", "卖多少钱")));
        synonymMap.put("多少", new HashSet<>(Arrays.asList("价格", "多少钱", "价钱", "售价", "卖多少", "卖多少钱")));
        synonymMap.put("卖多少", new HashSet<>(Arrays.asList("价格", "多少钱", "价钱", "多少", "售价", "卖多少钱")));
        synonymMap.put("卖多少钱", new HashSet<>(Arrays.asList("价格", "多少钱", "价钱", "多少", "售价", "卖多少")));

        // 计算同义词匹配度
        int synonymMatchCount = 0;
        for (String word1 : words1) {
            for (String word2 : words2) {
                // 直接匹配
                if (word1.equals(word2)) {
                    synonymMatchCount++;
                    continue;
                }
                // 同义词匹配
                Set<String> synonyms1 = synonymMap.get(word1);
                Set<String> synonyms2 = synonymMap.get(word2);
                if (synonyms1 != null && synonyms1.contains(word2)) {
                    synonymMatchCount++;
                } else if (synonyms2 != null && synonyms2.contains(word1)) {
                    synonymMatchCount++;
                }
            }
        }

        int totalWords = words1.size() + words2.size();
        return totalWords > 0 ? (double) synonymMatchCount / totalWords : 0.0;
    }

    /**
     * 文本预处理
     * 去除HTML标签、转换为小写、去除首尾空格
     */
    private String preprocessText(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("<[^>]+>", "") // 去除HTML标签
                .trim()
                .toLowerCase();
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


