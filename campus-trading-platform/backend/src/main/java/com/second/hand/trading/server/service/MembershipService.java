package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.MembershipOrderModel;
import com.second.hand.trading.server.entity.UserModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 会员服务接口
 */
public interface MembershipService {
    /**
     * 创建会员订单
     */
    MembershipOrderModel createOrder(Long userId, Byte membershipType, Integer durationMonths);

    /**
     * 支付成功后开通会员
     */
    boolean activateMembership(String orderNumber);

    /**
     * 检查用户会员状态
     */
    boolean isMembershipValid(Long userId);

    /**
     * 获取用户会员类型
     */
    Byte getMembershipType(Long userId);

    /**
     * 获取用户会员到期时间
     */
    Date getMembershipExpireTime(Long userId);

    /**
     * 检查用户是否可以发布商品（检查发布数量限制）
     */
    boolean canPublishItem(Long userId);

    /**
     * 检查用户是否可以置顶商品（检查置顶数量限制）
     */
    boolean canPinItem(Long userId);

    /**
     * 获取用户本月已发布商品数量
     */
    int getPublishedCountThisMonth(Long userId);

    /**
     * 获取用户当前有效置顶数量
     */
    int getActivePinCount(Long userId);

    /**
     * 获取用户会员订单列表
     */
    List<MembershipOrderModel> getUserOrders(Long userId);

    /**
     * 管理员：获取会员收入统计
     */
    Map<String, Object> getMembershipRevenueStats(Date startDate, Date endDate);

    /**
     * 管理员：获取会员数量统计
     */
    Map<String, Object> getMembershipCountStats();
}

