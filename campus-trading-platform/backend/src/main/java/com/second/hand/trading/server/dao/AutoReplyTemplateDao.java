package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.AutoReplyTemplateModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自动回复模板 DAO
 */
@Mapper
public interface AutoReplyTemplateDao {

    /**
     * 插入一条自动回复模板
     */
    int insert(AutoReplyTemplateModel record);

    /**
     * 根据主键查询
     */
    AutoReplyTemplateModel selectByPrimaryKey(Long id);

    /**
     * 根据用户ID查询所有模板
     */
    List<AutoReplyTemplateModel> selectByUserId(Long userId);

    /**
     * 根据用户ID和关键词查询（用于检查重复）
     */
    AutoReplyTemplateModel selectByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 根据用户ID查询启用的模板
     */
    List<AutoReplyTemplateModel> selectEnabledByUserId(Long userId);

    /**
     * 更新模板
     */
    int updateByPrimaryKeySelective(AutoReplyTemplateModel record);

    /**
     * 删除模板
     */
    int deleteByPrimaryKey(Long id);
}
