package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.dto.ResultVo;
import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.UserModel;
import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.service.MerchantService;
import com.second.hand.trading.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 经营性卖家管理接口
 */
@RestController
@RequestMapping("/merchant/manage")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private IdleItemService idleItemService;

    @Autowired
    private UserService userService;

    /**
     * 批量发布商品
     */
    @PostMapping("/batch-add")
    public ResultVo batchAddItems(@CookieValue("shUserId")
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                  @RequestBody List<IdleItemModel> items) {
        // 验证用户是否为经营性卖家
        UserModel user = userService.getUser(Long.valueOf(shUserId));
        if (user == null || user.getUserRole() == null || user.getUserRole() != 1) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        if (merchantService.batchAddItems(Long.valueOf(shUserId), items)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 更新商品库存
     */
    @PostMapping("/update-stock")
    public ResultVo updateStock(@CookieValue("shUserId")
                                @NotNull(message = "登录异常 请重新登录")
                                @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                @RequestParam Long idleId,
                                @RequestParam Integer stock) {
        // 验证用户是否为经营性卖家
        UserModel user = userService.getUser(Long.valueOf(shUserId));
        if (user == null || user.getUserRole() == null || user.getUserRole() != 1) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        // 验证商品是否属于该用户
        IdleItemModel item = idleItemService.getIdleItem(idleId);
        if (item == null || !item.getUserId().equals(Long.valueOf(shUserId))) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        if (merchantService.updateStock(idleId, stock)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取店铺交易数据统计
     */
    @GetMapping("/statistics")
    public ResultVo getShopStatistics(@CookieValue("shUserId")
                                     @NotNull(message = "登录异常 请重新登录")
                                     @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        // 验证用户是否为经营性卖家
        UserModel user = userService.getUser(Long.valueOf(shUserId));
        if (user == null || user.getUserRole() == null || user.getUserRole() != 1) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        Map<String, Object> statistics = merchantService.getShopStatistics(Long.valueOf(shUserId));
        return ResultVo.success(statistics);
    }

    /**
     * 获取店铺所有商品（用于库存管理）
     */
    @GetMapping("/items")
    public ResultVo getShopItems(@CookieValue("shUserId")
                                 @NotNull(message = "登录异常 请重新登录")
                                 @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        // 验证用户是否为经营性卖家
        UserModel user = userService.getUser(Long.valueOf(shUserId));
        if (user == null || user.getUserRole() == null || user.getUserRole() != 1) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        List<IdleItemModel> items = merchantService.getShopItems(Long.valueOf(shUserId));
        return ResultVo.success(items);
    }

    /**
     * 获取店铺订单列表
     */
    @GetMapping("/orders")
    public ResultVo getShopOrders(@CookieValue("shUserId")
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        // 验证用户是否为经营性卖家
        UserModel user = userService.getUser(Long.valueOf(shUserId));
        if (user == null || user.getUserRole() == null || user.getUserRole() != 1) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }

        return ResultVo.success(merchantService.getShopOrders(Long.valueOf(shUserId)));
    }
}

