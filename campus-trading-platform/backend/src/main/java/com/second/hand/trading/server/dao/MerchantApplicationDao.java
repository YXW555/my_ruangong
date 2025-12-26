package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.MerchantApplicationModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MerchantApplicationDao {

    /**
     * 插入商家认证申请
     */
    int insert(MerchantApplicationModel record);

    /**
     * 根据用户ID查询申请
     */
    MerchantApplicationModel selectByUserId(@Param("userId") Long userId);

    /**
     * 根据ID查询申请
     */
    MerchantApplicationModel selectByPrimaryKey(@Param("id") Long id);

    /**
     * 查询所有申请（按状态筛选）
     */
    List<MerchantApplicationModel> selectAll(@Param("status") Byte status);

    /**
     * 更新申请信息
     */
        int updateByPrimaryKeySelective(MerchantApplicationModel record);

    /**
     * 根据状态计算申请数量
     */
    int countByStatus(@Param("status") Byte status);
}

