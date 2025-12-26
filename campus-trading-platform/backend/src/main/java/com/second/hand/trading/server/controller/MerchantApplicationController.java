package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.dto.ResultVo;
import com.second.hand.trading.server.entity.MerchantApplicationModel;
import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.service.MerchantApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 商家认证相关接口
 */
@RestController
@RequestMapping("/merchant")
public class MerchantApplicationController {

    @Autowired
    private MerchantApplicationService merchantApplicationService;

    /**
     * 提交商家认证申请
     */
    @PostMapping("/apply")
    public ResultVo submitApplication(@CookieValue("shUserId")
                                      @NotNull(message = "登录异常 请重新登录")
                                      @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                      @RequestBody MerchantApplicationModel applicationModel) {
        applicationModel.setUserId(Long.valueOf(shUserId));
        if (merchantApplicationService.submitApplication(applicationModel)) {
            return ResultVo.success(applicationModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 查询当前用户的申请状态
     */
    @GetMapping("/my-application")
    public ResultVo getMyApplication(@CookieValue("shUserId")
                                     @NotNull(message = "登录异常 请重新登录")
                                     @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        MerchantApplicationModel application = merchantApplicationService.getApplicationByUserId(Long.valueOf(shUserId));
        return ResultVo.success(application);
    }

    /**
     * 管理员：查询所有申请
     */
    @GetMapping("/admin/list")
    public ResultVo getAllApplications(@RequestParam(required = false) Byte status) {
        return ResultVo.success(merchantApplicationService.getAllApplications(status));
    }

    /**
     * 管理员：审核申请
     */
    /**
     * 管理员：获取待审核申请数量
     */
    @GetMapping("/admin/pending-count")
    public ResultVo getPendingCount() {
        return ResultVo.success(merchantApplicationService.countPendingApplications());
    }

    /**
     * 管理员：审核申请
     */
    @PostMapping("/admin/review")
    public ResultVo reviewApplication(@CookieValue("shAdminId")
                                     @NotNull(message = "管理员登录异常")
                                     @NotEmpty(message = "管理员登录异常") String shAdminId,
                                     @RequestParam Long applicationId,
                                     @RequestParam Byte status,
                                     @RequestParam(required = false) String adminComment) {
        if (merchantApplicationService.reviewApplication(applicationId, Long.valueOf(shAdminId), status, adminComment)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}

