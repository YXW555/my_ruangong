package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.IdleItemPinModel;
import com.second.hand.trading.server.dao.IdleItemPinDao;
import com.second.hand.trading.server.service.IdleItemService;
import com.second.hand.trading.server.service.MembershipService;
import com.second.hand.trading.server.dto.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@RestController
@RequestMapping("idle")
public class IdleItemController {

    @Autowired
    private IdleItemService idleItemService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private IdleItemPinDao idleItemPinDao;

    @PostMapping("add")
    public ResultVo addIdleItem(@CookieValue(value = "shUserId", required = false) String shUserId,
                                @RequestBody IdleItemModel idleItemModel){
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        Long userId = Long.valueOf(shUserId);
        
        // 检查会员发布权限
        if (!membershipService.canPublishItem(userId)) {
            Byte membershipType = membershipService.getMembershipType(userId);
            int publishedCount = membershipService.getPublishedCountThisMonth(userId);
            int limit = membershipType == 1 ? 20 : 10;
            ResultVo resultVo = ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
            resultVo.setMsg("您本月已发布" + publishedCount + "个商品，已达到" + limit + "个的限制。开通会员可享受更多发布数量！");
            return resultVo;
        }
        
        idleItemModel.setUserId(userId);
        idleItemModel.setIdleStatus((byte) 1);
        idleItemModel.setReleaseTime(new Date());
        // 如果没有设置库存，默认为1
        if (idleItemModel.getStock() == null || idleItemModel.getStock() <= 0) {
            idleItemModel.setStock(1);
        }
        if(idleItemService.addIdleItem(idleItemModel)){
            return ResultVo.success(idleItemModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    @GetMapping("info")
    public ResultVo getIdleItem(@RequestParam Long id){
        return ResultVo.success(idleItemService.getIdleItem(id));
    }

    @GetMapping("all")
    public ResultVo getAllIdleItem(@CookieValue(value = "shUserId", required = false) String shUserId){
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        return ResultVo.success(idleItemService.getAllIdelItem(Long.valueOf(shUserId)));
    }

    @GetMapping("find")
    public ResultVo findIdleItem(@RequestParam(value = "findValue",required = false) String findValue,
                                 @RequestParam(value = "page",required = false) Integer page,
                                 @RequestParam(value = "nums",required = false) Integer nums){
        if(null==findValue){
            findValue="";
        }
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(idleItemService.findIdleItem(findValue,p,n));
    }

    @GetMapping({"lable","findByLabel"})
    public ResultVo findIdleItemByLable(@RequestParam(value = "idleLabel",required = true) Integer idleLabel,
                                 @RequestParam(value = "page",required = false) Integer page,
                                 @RequestParam(value = "nums",required = false) Integer nums){
        int p=1;
        int n=8;
        if(null!=page){
            p=page>0?page:1;
        }
        if(null!=nums){
            n=nums>0?nums:8;
        }
        return ResultVo.success(idleItemService.findIdleItemByLable(idleLabel,p,n));
    }

    @PostMapping("update")
    public ResultVo updateIdleItem(@CookieValue(value = "shUserId", required = false) String shUserId,
                                   @RequestBody IdleItemModel idleItemModel){
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        idleItemModel.setUserId(Long.valueOf(shUserId));
        if(idleItemService.updateIdleItem(idleItemModel)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 置顶商品
     */
    @PostMapping("/pin")
    public ResultVo pinItem(@CookieValue(value = "shUserId", required = false) String shUserId,
                           @RequestParam Long idleItemId,
                           @RequestParam Integer durationDays) {
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        Long userId = Long.valueOf(shUserId);
        
        // 检查会员置顶权限
        if (!membershipService.canPinItem(userId)) {
            Byte membershipType = membershipService.getMembershipType(userId);
            ResultVo resultVo = ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
            if (membershipType == 0) {
                resultVo.setMsg("置顶功能仅限会员使用，请先开通会员！");
            } else {
                int activePinCount = membershipService.getActivePinCount(userId);
                int limit = membershipType == 2 ? 15 : 5;
                resultVo.setMsg("您当前已有" + activePinCount + "个商品在置顶中，已达到" + limit + "个的限制。");
            }
            return resultVo;
        }
        
        // 检查商品是否属于当前用户
        IdleItemModel item = idleItemService.getIdleItem(idleItemId);
        if (item == null || !item.getUserId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
        
        // 创建置顶记录
        IdleItemPinModel pin = new IdleItemPinModel();
        pin.setIdleItemId(idleItemId);
        pin.setUserId(userId);
        Date now = new Date();
        pin.setPinStartTime(now);
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, durationDays);
        pin.setPinEndTime(cal.getTime());
        pin.setCreateTime(now);
        
        if (idleItemPinDao.insert(pin) == 1) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 取消置顶（立即使当前有效的置顶记录失效）
     */
    @PostMapping("/unpin")
    public ResultVo unpinItem(@CookieValue(value = "shUserId", required = false) String shUserId,
                              @RequestParam Long idleItemId) {
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        Long userId = Long.valueOf(shUserId);

        // 检查商品是否属于当前用户
        IdleItemModel item = idleItemService.getIdleItem(idleItemId);
        if (item == null || !item.getUserId().equals(userId)) {
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }

        // 当前时间，用于设置置顶结束时间为当前，立即生效
        java.util.Date now = new java.util.Date();
        int updated = idleItemPinDao.expireActivePinByItemId(idleItemId, now);
        if (updated > 0) {
            return ResultVo.success();
        } else {
            // 如果没有更新，说明当前没有有效的置顶记录
            return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        }
    }
}
