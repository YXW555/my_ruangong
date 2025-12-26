package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.dto.ResultVo;
import com.second.hand.trading.server.entity.MembershipOrderModel;
import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员相关接口
 */
@RestController
@RequestMapping("/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    /**
     * 创建会员订单
     */
    @PostMapping("/create-order")
    public ResultVo createOrder(@CookieValue("shUserId")
                                @NotNull(message = "登录异常 请重新登录")
                                @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                @RequestParam Byte membershipType,
                                @RequestParam Integer durationMonths) {
        if (membershipType != 1 && membershipType != 2) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        if (durationMonths == null || durationMonths < 1 || durationMonths > 12) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        MembershipOrderModel order = membershipService.createOrder(Long.valueOf(shUserId), membershipType, durationMonths);
        return ResultVo.success(order);
    }

    /**
     * 支付成功后开通会员
     */
    @PostMapping("/activate")
    public ResultVo activateMembership(@RequestParam String orderNumber) {
        if (membershipService.activateMembership(orderNumber)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 查询用户会员状态
     */
    @GetMapping("/status")
    public ResultVo getMembershipStatus(@CookieValue("shUserId")
                                       @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        Map<String, Object> status = new HashMap<>();
        status.put("isValid", membershipService.isMembershipValid(Long.valueOf(shUserId)));
        status.put("membershipType", membershipService.getMembershipType(Long.valueOf(shUserId)));
        status.put("expireTime", membershipService.getMembershipExpireTime(Long.valueOf(shUserId)));
        status.put("canPublish", membershipService.canPublishItem(Long.valueOf(shUserId)));
        status.put("canPin", membershipService.canPinItem(Long.valueOf(shUserId)));
        status.put("publishedCount", membershipService.getPublishedCountThisMonth(Long.valueOf(shUserId)));
        status.put("activePinCount", membershipService.getActivePinCount(Long.valueOf(shUserId)));
        return ResultVo.success(status);
    }

    /**
     * 查询用户会员订单列表
     */
    @GetMapping("/orders")
    public ResultVo getUserOrders(@CookieValue("shUserId")
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        List<MembershipOrderModel> orders = membershipService.getUserOrders(Long.valueOf(shUserId));
        return ResultVo.success(orders);
    }

    /**
     * 管理员：获取会员收入统计
     */
    @GetMapping("/admin/revenue-stats")
    public ResultVo getRevenueStats(@RequestParam(required = false) String startDate,
                                    @RequestParam(required = false) String endDate) {
        Date start = startDate != null ? java.sql.Timestamp.valueOf(startDate) : null;
        Date end = endDate != null ? java.sql.Timestamp.valueOf(endDate) : null;
        
        if (start == null || end == null) {
            // 默认查询最近30天
            Calendar cal = Calendar.getInstance();
            end = new Date();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            start = cal.getTime();
        }
        
        Map<String, Object> stats = membershipService.getMembershipRevenueStats(start, end);
        return ResultVo.success(stats);
    }

    /**
     * 管理员：获取会员数量统计
     */
    @GetMapping("/admin/count-stats")
    public ResultVo getCountStats() {
        Map<String, Object> stats = membershipService.getMembershipCountStats();
        return ResultVo.success(stats);
    }
}

