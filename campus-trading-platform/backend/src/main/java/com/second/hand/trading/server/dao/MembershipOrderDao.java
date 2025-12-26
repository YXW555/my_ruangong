package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.MembershipOrderModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MembershipOrderDao {
    /**
     * 插入会员订单
     */
    int insert(MembershipOrderModel record);

    /**
     * 根据订单号查询
     */
    MembershipOrderModel selectByOrderNumber(@Param("orderNumber") String orderNumber);

    /**
     * 根据用户ID查询订单列表
     */
    List<MembershipOrderModel> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据支付状态查询订单
     */
    List<MembershipOrderModel> selectByPaymentStatus(@Param("paymentStatus") Byte paymentStatus);

    /**
     * 更新订单
     */
    int updateByPrimaryKeySelective(MembershipOrderModel record);

    /**
     * 统计会员收入（按支付状态）
     */
    java.math.BigDecimal sumAmountByPaymentStatus(@Param("paymentStatus") Byte paymentStatus);

    /**
     * 统计指定时间范围内的会员收入
     */
    java.math.BigDecimal sumAmountByDateRange(@Param("startDate") java.util.Date startDate, 
                                               @Param("endDate") java.util.Date endDate,
                                               @Param("paymentStatus") Byte paymentStatus);
}

