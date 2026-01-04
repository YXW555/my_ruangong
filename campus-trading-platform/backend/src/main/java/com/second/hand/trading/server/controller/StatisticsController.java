package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.service.UserService;
import com.second.hand.trading.server.dto.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("statistics")
public class StatisticsController {

    @Autowired
    private IdleItemService idleItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * 获取平台交易数据统计，包括总交易量、总交易额等
     */
    @GetMapping("/trading-data")
    public ResultVo getTradingData(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        Map<String, Object> data = new HashMap<>();

        // 获取总交易数
        int totalOrders = orderService.getTotalOrderCount();
        data.put("totalOrders", totalOrders);

        // 获取总交易金额
        BigDecimal totalAmount = orderService.getTotalOrderAmount();
        data.put("totalAmount", totalAmount);

        // 获取当月交易数
        int currentMonthOrders = orderService.getCurrentMonthOrderCount();
        data.put("currentMonthOrders", currentMonthOrders);

        // 获取当月交易金额
        BigDecimal currentMonthAmount = orderService.getCurrentMonthOrderAmount();
        data.put("currentMonthAmount", currentMonthAmount);

        return ResultVo.success(data);
    }

    /**
     * 获取过去6个月的交易统计数据
     */
    @GetMapping("/monthly-statistics")
    public ResultVo getMonthlyStatistics(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        List<Map<String, Object>> monthlyData = orderService.getMonthlyOrderStatistics();
        return ResultVo.success(monthlyData);
    }

    /**
     * 获取商品分类统计数据
     */
    @GetMapping("/category-statistics")
    public ResultVo getCategoryStatistics(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        List<Map<String, Object>> categoryData = idleItemService.getCategoryStatistics();
        return ResultVo.success(categoryData);
    }

    /**
     * 获取用户注册统计数据
     */
    @GetMapping("/user-statistics")
    public ResultVo getUserStatistics(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        Map<String, Object> userData = new HashMap<>();

        // 获取总用户数
        int totalUsers = userService.getTotalUserCount();
        userData.put("totalUsers", totalUsers);

        // 获取本月新增用户数
        int newUsers = userService.getNewUserCount();
        userData.put("newUsers", newUsers);

        // 获取活跃用户数（有发布或购买行为的用户）
        int activeUsers = userService.getActiveUserCount();
        userData.put("activeUsers", activeUsers);

        return ResultVo.success(userData);
    }

    /**
     * 获取环境效益统计数据（碳排放减少等）
     */
    @GetMapping("/environmental-benefits")
    public ResultVo getEnvironmentalBenefits(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }

        Map<String, Object> envData = new HashMap<>();

        // 获取总交易金额
        BigDecimal totalAmount = orderService.getTotalOrderAmount();
        int totalOrders = orderService.getTotalOrderCount();

        // 环境效益计算逻辑：
        // 1. 碳排放减少：假设每100元二手交易额相当于减少1kg CO2排放（避免新商品生产）
        // 2. 资源循环：每笔交易代表一件商品被重复利用
        // 3. 废弃物减少：假设每笔交易减少0.5kg废弃物产生

        // 碳排放减少（kg CO2）
        // 公式：交易金额(元) / 100 * 1kg = CO2减少量
        double co2Reduction = totalAmount.doubleValue() / 100.0;
        envData.put("co2Reduction", Math.round(co2Reduction * 100.0) / 100.0); // 保留2位小数

        // 资源循环利用次数（件）
        envData.put("recycledItems", totalOrders);

        // 废弃物减少（kg）
        // 假设每笔交易减少0.5kg废弃物
        double wasteReduction = totalOrders * 0.5;
        envData.put("wasteReduction", Math.round(wasteReduction * 100.0) / 100.0);

        // 相当于种植树木数量（假设1kg CO2 ≈ 0.05棵树）
        int treesEquivalent = (int) Math.round(co2Reduction * 0.05);
        envData.put("treesEquivalent", treesEquivalent);

        return ResultVo.success(envData);
    }
}
