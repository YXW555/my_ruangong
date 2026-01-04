# EasyRec推荐框架集成说明

## 关于EasyRec

EasyRec是阿里巴巴开源的推荐算法框架，主要用于Python/TensorFlow环境。对于Java Spring Boot项目，我们采用以下方案：

### 方案选择

由于EasyRec是Python框架，我们实现了一个**基于EasyRec设计理念的Java推荐引擎**，核心算法包括：

1. **协同过滤算法**（Collaborative Filtering）
   - 基于用户的协同过滤（User-based CF）
   - 基于物品的协同过滤（Item-based CF）

2. **内容推荐算法**（Content-based）
   - 基于商品类别、标签的相似度推荐

3. **混合推荐算法**（Hybrid）
   - 结合协同过滤和内容推荐

## 核心实现逻辑

### 1. 协同过滤算法核心代码

位置：`RecommendServiceImpl.java` 的 `getCollaborativeRecommendations()` 方法

**算法流程：**
1. 收集用户行为数据（收藏、购买）
2. 计算用户相似度（基于共同交互的商品）
3. 找到相似用户
4. 推荐相似用户喜欢的其他商品

### 2. 自动回复算法说明

**算法类型：关键词匹配算法（Keyword Matching）**

位置：`AutoReplyTemplateServiceImpl.java` 的 `matchReply()` 方法

**核心实现逻辑：**
1. **关键词提取**：从用户消息中提取关键词
2. **模板匹配**：在用户设置的自动回复模板中查找匹配的关键词
3. **字符串包含匹配**：使用 `String.contains()` 方法进行简单匹配
4. **返回回复内容**：找到匹配的模板后返回对应的回复内容

**算法特点：**
- ✅ 简单高效，响应速度快
- ✅ 易于理解和维护
- ✅ 支持用户自定义关键词和回复内容
- ⚠️ 不支持复杂的语义理解（如NLP）
- ⚠️ 需要精确的关键词匹配

**如果要升级为智能NLP算法：**
- 可以使用阿里云的智能客服API
- 或者集成开源NLP框架如HanLP、jieba等
- 实现语义相似度匹配而非简单的字符串包含

