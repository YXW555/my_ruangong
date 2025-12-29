package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.DisputeModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DisputeDao {
    /**
     * 插入纠纷记录
     */
    int insert(DisputeModel record);

    /**
     * 根据主键查询
     */
    DisputeModel selectByPrimaryKey(Long id);

    /**
     * 根据订单ID查询纠纷
     */
    DisputeModel selectByOrderId(Long orderId);

    /**
     * 查询用户的纠纷列表
     */
    List<DisputeModel> selectByApplicantId(Long applicantId);

    /**
     * 查询所有纠纷（管理员用）
     */
    List<DisputeModel> selectAll(@Param("status") Byte status, @Param("begin") int begin, @Param("nums") int nums);

    /**
     * 统计纠纷数量
     */
    int countAll(@Param("status") Byte status);

    /**
     * 更新纠纷信息
     */
    int updateByPrimaryKeySelective(DisputeModel record);
}

