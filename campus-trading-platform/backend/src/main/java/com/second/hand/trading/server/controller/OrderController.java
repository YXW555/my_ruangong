package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.entity.OrderModel;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.utils.IdFactoryUtil;
import com.second.hand.trading.server.utils.OrderTaskHandler;
import com.second.hand.trading.server.dto.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/add")
    public ResultVo addOrder(@CookieValue(value = "shUserId", required = false) String shUserId,
                             @RequestBody OrderModel orderModel){
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        if(OrderTaskHandler.orderService==null){
            OrderTaskHandler.orderService=orderService;
        }
        orderModel.setOrderNumber(IdFactoryUtil.getOrderId());
        orderModel.setCreateTime(new Date());
        orderModel.setUserId(Long.valueOf(shUserId));
        orderModel.setOrderStatus((byte) 0);
        orderModel.setPaymentStatus((byte)0);
        // 保证数据库的 NOT NULL 字段有默认值，避免因 isDeleted 为 null 导致插入失败
        if (orderModel.getIsDeleted() == null) {
            orderModel.setIsDeleted((byte)0);
        }
        if(orderService.addOrder(orderModel)){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/info")
    public ResultVo getOrderInfo(@CookieValue(value = "shUserId", required = false) String shUserId,
                                 @RequestParam Long id){
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        OrderModel orderModel=orderService.getOrder(id);
        if(orderModel.getUserId().equals(Long.valueOf(shUserId))||
                orderModel.getIdleItem().getUserId().equals(Long.valueOf(shUserId))){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @PostMapping("/update")
    public ResultVo updateOrder(@CookieValue(value = "shUserId", required = false) String shUserId,
                             @RequestBody OrderModel orderModel,
                             @RequestParam(value = "price",required = false) String price){
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }








        if(orderModel.getPaymentStatus()!=null&&orderModel.getPaymentStatus().equals((byte) 1)){
            orderModel.setPaymentTime(new Date());
        }
        if(orderService.updateOrder(orderModel)){
            return ResultVo.success(orderModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("/my")
    public ResultVo getMyOrder(@CookieValue(value = "shUserId", required = false) String shUserId){
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        return ResultVo.success(orderService.getMyOrder(Long.valueOf(shUserId)));
    }

    /**
     * 计算单个订单的碳减排估算（演示用）
     */
    @GetMapping("/carbon")
    public ResultVo getOrderCarbon(@CookieValue(value = "shUserId", required = false) String shUserId,
                                   @RequestParam(value = "orderId", required = false) Long orderId) {
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        try {
            if (orderId == null) {
                return ResultVo.fail("orderId required");
            }
            OrderModel order = orderService.getOrder(orderId);
            if (order == null) return ResultVo.fail("order not found");
            // only allow buyer or seller to view
            Long currentUser = Long.valueOf(shUserId);
            if (!order.getUserId().equals(currentUser) && !(order.getIdleItem() != null && order.getIdleItem().getUserId().equals(currentUser))) {
                return ResultVo.fail("permission denied");
            }
            double factor = 0.03; // kg CO2 per ￥, demo factor (configurable)
            double kwhPerKg = 3.0; // conversion factor
            double price = order.getOrderPrice() != null ? order.getOrderPrice().doubleValue() : 0.0;
            double kg = Math.round((price * factor) * 10.0) / 10.0;
            double kwh = Math.round((kg * kwhPerKg) * 10.0) / 10.0;
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("carbonKg", kg);
            data.put("equivalentKwh", kwh);
            return ResultVo.success(data);
        } catch (Exception e) {
            return ResultVo.fail(e.getMessage());
        }
    }

    /**
     * 计算当前用户累计碳贡献（基于已完成订单）
     */
    @GetMapping("/my-carbon")
    public ResultVo getMyCarbon(@CookieValue(value = "shUserId", required = false) String shUserId) {
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        Long userId = Long.valueOf(shUserId);
        List<OrderModel> orders = orderService.getMyOrder(userId);
        int count = 0;
        double totalKg = 0.0;
        double factor = 0.03;
        double kwhPerKg = 3.0;
        if (orders != null) {
            for (OrderModel o : orders) {
                if (o.getOrderStatus() != null && o.getOrderStatus().equals((byte)3) && o.getPaymentStatus() != null && o.getPaymentStatus().equals((byte)1)) {
                    count++;
                    double price = o.getOrderPrice() != null ? o.getOrderPrice().doubleValue() : 0.0;
                    totalKg += price * factor;
                }
            }
        }
        totalKg = Math.round(totalKg * 10.0) / 10.0;
        double totalKwh = Math.round(totalKg * kwhPerKg * 10.0) / 10.0;
        Map<String, Object> data = new HashMap<>();
        data.put("orderCount", count);
        data.put("totalCarbonKg", totalKg);
        data.put("equivalentKwh", totalKwh);
        return ResultVo.success(data);
    }

    @GetMapping({"/my-sold", "/mySoldIdle", "/mySold"})
    public ResultVo getMySoldIdle(@CookieValue(value = "shUserId", required = false) String shUserId){
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        return ResultVo.success(orderService.getMySoldIdle(Long.valueOf(shUserId)));
    }
}
