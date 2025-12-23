package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.dto.ResultVo;
import com.second.hand.trading.server.entity.OrderModel;
import com.second.hand.trading.server.entity.RatingModel;
import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 评价相关接口
 */
@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private OrderService orderService;

    /**
     * 添加评价
     */
    @PostMapping("/add")
    public ResultVo addRating(@CookieValue("shUserId")
                              @NotNull(message = "登录异常 请重新登录")
                              @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                              @RequestBody RatingModel ratingModel) {
        // 验证订单是否存在且属于当前用户
        OrderModel order = orderService.getOrder(ratingModel.getOrderId());
        if (order == null) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        // 验证订单是否已完成
        if (order.getOrderStatus() != 3) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        // 验证是否为买家
        if (!order.getUserId().equals(Long.valueOf(shUserId))) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        // 检查是否已经评价过
        RatingModel existingRating = ratingService.getRatingByOrderId(ratingModel.getOrderId());
        if (existingRating != null) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        // 设置评价信息
        ratingModel.setBuyerId(Long.valueOf(shUserId));
        ratingModel.setSellerId(order.getIdleItem().getUserId());
        ratingModel.setCreateTime(new Date());

        // 验证评分范围（1-5分）
        if (ratingModel.getRating() == null || ratingModel.getRating() < 1 || ratingModel.getRating() > 5) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        if (ratingService.addRating(ratingModel)) {
            return ResultVo.success(ratingModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 根据订单ID查询评价
     */
    @GetMapping("/by-order")
    public ResultVo getRatingByOrderId(@RequestParam Long orderId) {
        return ResultVo.success(ratingService.getRatingByOrderId(orderId));
    }

    /**
     * 查询卖家的所有评价
     */
    @GetMapping("/by-seller")
    public ResultVo getRatingsBySellerId(@RequestParam Long sellerId) {
        return ResultVo.success(ratingService.getRatingsBySellerId(sellerId));
    }

    /**
     * 查询买家的所有评价
     */
    @GetMapping("/by-buyer")
    public ResultVo getRatingsByBuyerId(@CookieValue("shUserId")
                                        @NotNull(message = "登录异常 请重新登录")
                                        @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        return ResultVo.success(ratingService.getRatingsByBuyerId(Long.valueOf(shUserId)));
    }

    /**
     * 获取卖家的信誉信息（平均评分和评价数量）
     */
    @GetMapping("/seller-stats")
    public ResultVo getSellerStats(@RequestParam Long sellerId) {
        Double avgRating = ratingService.getAverageRating(sellerId);
        Integer count = ratingService.getRatingCount(sellerId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", avgRating);
        stats.put("ratingCount", count);
        return ResultVo.success(stats);
    }
}

