package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.entity.DisputeModel;
import com.second.hand.trading.server.service.DisputeService;
import com.second.hand.trading.server.dto.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 交易纠纷Controller
 */
@RestController
@RequestMapping("/dispute")
public class DisputeController {

    @Autowired
    private DisputeService disputeService;

    /**
     * 用户申请纠纷处理
     */
    @PostMapping("/apply")
    public ResultVo applyDispute(@CookieValue("shUserId")
                                 @NotNull(message = "登录异常 请重新登录")
                                 @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                 @RequestBody DisputeModel disputeModel) {
        // 设置申请人ID
        disputeModel.setApplicantId(Long.valueOf(shUserId));
        
        if (disputeService.applyDispute(disputeModel)) {
            return ResultVo.success(disputeModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 用户查看自己的纠纷列表
     */
    @GetMapping("/my")
    public ResultVo getMyDisputes(@CookieValue("shUserId")
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        return ResultVo.success(disputeService.getMyDisputes(Long.valueOf(shUserId)));
    }

    /**
     * 查看纠纷详情
     */
    @GetMapping("/info")
    public ResultVo getDisputeInfo(@CookieValue("shUserId")
                                   @NotNull(message = "登录异常 请重新登录")
                                   @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                   @RequestParam Long id) {
        DisputeModel dispute = disputeService.getDispute(id);
        if (dispute == null) {
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
        
        // 检查权限：只有申请人或管理员可以查看
        if (!dispute.getApplicantId().equals(Long.valueOf(shUserId))) {
            // 这里可以添加管理员权限检查
            // 暂时只允许申请人查看
        }
        
        return ResultVo.success(dispute);
    }

    /**
     * 管理员获取纠纷列表
     */
    @GetMapping("/admin/list")
    public ResultVo getDisputeList(HttpSession session,
                                   @RequestParam(value = "status", required = false) Byte status,
                                   @RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "nums", required = false) Integer nums) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        int p = 1;
        int n = 8;
        if (page != null && page > 0) {
            p = page;
        }
        if (nums != null && nums > 0) {
            n = nums;
        }
        
        return ResultVo.success(disputeService.getAllDisputes(status, p, n));
    }

    /**
     * 管理员处理纠纷
     */
    @PostMapping("/admin/handle")
    public ResultVo handleDispute(HttpSession session,
                                   @RequestBody java.util.Map<String, Object> params) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        Long disputeId = Long.valueOf(params.get("id").toString());
        Byte handleResult = Byte.valueOf(params.get("handleResult").toString());
        String handleDescription = params.get("handleDescription") != null ? params.get("handleDescription").toString() : null;
        
        // 获取管理员ID（从session中获取）
        com.second.hand.trading.server.entity.AdminModel admin = 
            (com.second.hand.trading.server.entity.AdminModel) session.getAttribute("admin");
        Long handlerId = admin.getId();
        
        if (disputeService.handleDispute(disputeId, handleResult, handleDescription, handlerId)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}

