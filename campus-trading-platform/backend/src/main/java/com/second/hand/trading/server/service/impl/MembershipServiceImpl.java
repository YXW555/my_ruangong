package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.IdleItemPinDao;
import com.second.hand.trading.server.dao.MembershipOrderDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.entity.MembershipOrderModel;
import com.second.hand.trading.server.entity.UserModel;
import com.second.hand.trading.server.service.MembershipService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class MembershipServiceImpl implements MembershipService {

    @Resource
    private MembershipOrderDao membershipOrderDao;

    @Resource
    private UserDao userDao;

    @Resource
    private IdleItemDao idleItemDao;

    @Resource
    private IdleItemPinDao idleItemPinDao;

    // 会员价格配置
    private static final BigDecimal BASIC_MONTHLY_PRICE = new BigDecimal("9.90");
    private static final BigDecimal BASIC_YEARLY_PRICE = new BigDecimal("99.00");
    private static final BigDecimal PREMIUM_MONTHLY_PRICE = new BigDecimal("19.90");
    private static final BigDecimal PREMIUM_YEARLY_PRICE = new BigDecimal("199.00");

    // 权限配置
    private static final int NORMAL_PUBLISH_LIMIT = 10; // 普通用户每月发布限制
    private static final int BASIC_PUBLISH_LIMIT = 20; // 基础会员每月发布限制
    private static final int BASIC_PIN_LIMIT = 5; // 基础会员每月置顶限制
    private static final int PREMIUM_PIN_LIMIT = 15; // 高级会员每月置顶限制

    @Override
    public MembershipOrderModel createOrder(Long userId, Byte membershipType, Integer durationMonths) {
        MembershipOrderModel order = new MembershipOrderModel();
        order.setUserId(userId);
        order.setMembershipType(membershipType);
        order.setDurationMonths(durationMonths);

        // 计算价格
        BigDecimal amount;
        if (membershipType == 1) { // 基础会员
            amount = durationMonths >= 12 ? BASIC_YEARLY_PRICE : BASIC_MONTHLY_PRICE.multiply(new BigDecimal(durationMonths));
        } else { // 高级会员
            amount = durationMonths >= 12 ? PREMIUM_YEARLY_PRICE : PREMIUM_MONTHLY_PRICE.multiply(new BigDecimal(durationMonths));
        }
        order.setAmount(amount);

        // 生成订单号
        String orderNumber = "MEM" + System.currentTimeMillis() + userId;
        order.setOrderNumber(orderNumber);
        order.setPaymentStatus((byte) 0); // 待支付
        order.setCreateTime(new Date());

        membershipOrderDao.insert(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activateMembership(String orderNumber) {
        MembershipOrderModel order = membershipOrderDao.selectByOrderNumber(orderNumber);
        if (order == null || order.getPaymentStatus() != 0) {
            return false;
        }

        // 更新订单状态
        order.setPaymentStatus((byte) 1); // 已支付
        order.setPaymentTime(new Date());
        order.setPaymentWay("支付宝"); // 可以根据实际情况设置
        membershipOrderDao.updateByPrimaryKeySelective(order);

        // 更新用户会员信息
        UserModel user = userDao.selectByPrimaryKey(order.getUserId());
        if (user == null) {
            return false;
        }

        Date currentExpireTime = user.getMembershipExpireTime();
        Date newExpireTime;

        // 如果当前会员未过期，在现有到期时间基础上延长
        if (currentExpireTime != null && currentExpireTime.after(new Date())) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentExpireTime);
            cal.add(Calendar.MONTH, order.getDurationMonths());
            newExpireTime = cal.getTime();
        } else {
            // 如果会员已过期或从未开通，从当前时间开始计算
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, order.getDurationMonths());
            newExpireTime = cal.getTime();
        }

        user.setMembershipType(order.getMembershipType());
        user.setMembershipExpireTime(newExpireTime);
        userDao.updateByPrimaryKeySelective(user);

        return true;
    }

    @Override
    public boolean isMembershipValid(Long userId) {
        UserModel user = userDao.selectByPrimaryKey(userId);
        if (user == null || user.getMembershipType() == null || user.getMembershipType() == 0) {
            return false;
        }
        Date expireTime = user.getMembershipExpireTime();
        return expireTime != null && expireTime.after(new Date());
    }

    @Override
    public Byte getMembershipType(Long userId) {
        if (!isMembershipValid(userId)) {
            return (byte) 0;
        }
        UserModel user = userDao.selectByPrimaryKey(userId);
        return user != null ? user.getMembershipType() : (byte) 0;
    }

    @Override
    public Date getMembershipExpireTime(Long userId) {
        UserModel user = userDao.selectByPrimaryKey(userId);
        return user != null ? user.getMembershipExpireTime() : null;
    }

    @Override
    public boolean canPublishItem(Long userId) {
        Byte membershipType = getMembershipType(userId);
        
        // 高级会员无限制
        if (membershipType == 2) {
            return true;
        }

        // 检查本月发布数量
        int publishedCount = getPublishedCountThisMonth(userId);
        int limit = membershipType == 1 ? BASIC_PUBLISH_LIMIT : NORMAL_PUBLISH_LIMIT;
        
        return publishedCount < limit;
    }

    @Override
    public boolean canPinItem(Long userId) {
        Byte membershipType = getMembershipType(userId);
        
        // 普通用户不能置顶
        if (membershipType == 0) {
            return false;
        }

        // 检查当前有效置顶数量
        int activePinCount = getActivePinCount(userId);
        int limit = membershipType == 2 ? PREMIUM_PIN_LIMIT : BASIC_PIN_LIMIT;
        
        return activePinCount < limit;
    }

    @Override
    public int getPublishedCountThisMonth(Long userId) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date monthStart = cal.getTime();

        return idleItemDao.countByUserIdAndTimeRange(userId, monthStart, new Date());
    }

    @Override
    public int getActivePinCount(Long userId) {
        return idleItemPinDao.countActivePinsByUserId(userId, new Date());
    }

    @Override
    public List<MembershipOrderModel> getUserOrders(Long userId) {
        return membershipOrderDao.selectByUserId(userId);
    }

    @Override
    public Map<String, Object> getMembershipRevenueStats(Date startDate, Date endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总收入
        BigDecimal totalRevenue = membershipOrderDao.sumAmountByDateRange(startDate, endDate, (byte) 1);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        
        // 基础会员和高级会员收入
        List<MembershipOrderModel> paidOrders = membershipOrderDao.selectByPaymentStatus((byte) 1);
        BigDecimal basicRevenue = BigDecimal.ZERO;
        BigDecimal premiumRevenue = BigDecimal.ZERO;
        int basicCount = 0;
        int premiumCount = 0;
        
        for (MembershipOrderModel order : paidOrders) {
            if (order.getPaymentTime() != null && 
                order.getPaymentTime().compareTo(startDate) >= 0 && 
                order.getPaymentTime().before(endDate)) {
                if (order.getMembershipType() == 1) {
                    basicRevenue = basicRevenue.add(order.getAmount());
                    basicCount++;
                } else if (order.getMembershipType() == 2) {
                    premiumRevenue = premiumRevenue.add(order.getAmount());
                    premiumCount++;
                }
            }
        }
        
        stats.put("basicRevenue", basicRevenue);
        stats.put("premiumRevenue", premiumRevenue);
        stats.put("basicOrderCount", basicCount);
        stats.put("premiumOrderCount", premiumCount);
        
        return stats;
    }

    @Override
    public Map<String, Object> getMembershipCountStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计有效会员数量
        List<UserModel> allUsers = userDao.getUserList();
        int basicCount = 0;
        int premiumCount = 0;
        Date now = new Date();
        
        for (UserModel user : allUsers) {
            if (user.getMembershipType() != null && user.getMembershipExpireTime() != null) {
                if (user.getMembershipExpireTime().after(now)) {
                    if (user.getMembershipType() == 1) {
                        basicCount++;
                    } else if (user.getMembershipType() == 2) {
                        premiumCount++;
                    }
                }
            }
        }
        
        stats.put("basicMemberCount", basicCount);
        stats.put("premiumMemberCount", premiumCount);
        stats.put("totalMemberCount", basicCount + premiumCount);
        
        return stats;
    }
}

