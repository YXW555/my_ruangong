package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.entity.AfterSaleModel;
import com.second.hand.trading.server.service.AfterSaleService;
import com.second.hand.trading.server.dto.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 售后服务Controller
 */
@RestController
@RequestMapping("/after-sale")
public class AfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    /**
     * 买家申请售后
     */
    @PostMapping("/apply")
    public ResultVo<AfterSaleModel> applyAfterSale(@CookieValue("shUserId")
                                                   @NotNull(message = "登录异常 请重新登录")
                                                   @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                                   @RequestBody AfterSaleModel afterSale) {
        // 设置买家ID
        afterSale.setBuyerId(Long.valueOf(shUserId));
        
        if (afterSaleService.applyAfterSale(afterSale)) {
            return ResultVo.success(afterSale);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 根据订单ID查询售后申请
     */
    @GetMapping("/by-order")
    public ResultVo<AfterSaleModel> getByOrderId(@RequestParam Long orderId) {
        AfterSaleModel afterSale = afterSaleService.getByOrderId(orderId);
        return ResultVo.success(afterSale);
    }

    /**
     * 买家查看自己的售后申请列表
     */
    @GetMapping("/my")
    public ResultVo<List<AfterSaleModel>> getMyAfterSales(@CookieValue("shUserId")
                                                          @NotNull(message = "登录异常 请重新登录")
                                                          @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        return ResultVo.success(afterSaleService.getMyAfterSales(Long.valueOf(shUserId)));
    }

    /**
     * 卖家查看待审核的售后申请列表
     */
    @GetMapping("/pending")
    public ResultVo<List<AfterSaleModel>> getPendingAfterSales(@CookieValue("shUserId")
                                                                @NotNull(message = "登录异常 请重新登录")
                                                                @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        return ResultVo.success(afterSaleService.getPendingAfterSales(Long.valueOf(shUserId)));
    }

    /**
     * 卖家审核售后申请
     */
    @PostMapping("/review")
    public ResultVo reviewAfterSale(@CookieValue("shUserId")
                                    @NotNull(message = "登录异常 请重新登录")
                                    @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                    @RequestBody java.util.Map<String, Object> params) {
        Long afterSaleId = Long.valueOf(params.get("id").toString());
        Byte reviewResult = Byte.valueOf(params.get("reviewResult").toString());
        String rejectReason = params.get("rejectReason") != null ? params.get("rejectReason").toString() : null;
        String sellerEvidenceImages = params.get("sellerEvidenceImages") != null ? params.get("sellerEvidenceImages").toString() : null;
        
        if (reviewResult == 2 && (rejectReason == null || rejectReason.trim().isEmpty())) {
            ResultVo resultVo = new ResultVo();
            resultVo.setStatus_code(0);
            resultVo.setMsg("拒绝申请时必须填写拒绝原因");
            return resultVo;
        }
        
        if (afterSaleService.reviewAfterSale(afterSaleId, reviewResult, rejectReason, sellerEvidenceImages)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 管理员获取待审核的售后申请列表
     */
    @GetMapping("/admin/pending")
    public ResultVo<List<AfterSaleModel>> getAdminPendingAfterSales(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        return ResultVo.success(afterSaleService.getAdminPendingAfterSales());
    }

    /**
     * 管理员获取售后申请详情（包含订单、聊天记录等）
     */
    @GetMapping("/admin/detail")
    public ResultVo<AfterSaleModel> getAdminAfterSaleDetail(HttpSession session, @RequestParam Long id) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        return ResultVo.success(afterSaleService.getAdminAfterSaleDetail(id));
    }

    /**
     * 管理员审核售后申请
     * @param params {id: 售后ID, reviewResult: 审核结果(1-同意退款, 2-驳回), reason: 驳回原因}
     */
    @PostMapping("/admin/review")
    public ResultVo adminReviewAfterSale(HttpSession session, @RequestBody java.util.Map<String, Object> params) {
        if (session.getAttribute("admin") == null) {
            return ResultVo.fail(ErrorMsg.COOKIE_ERROR);
        }
        
        Long afterSaleId = Long.valueOf(params.get("id").toString());
        Byte reviewResult = Byte.valueOf(params.get("reviewResult").toString());
        String reason = params.get("reason") != null ? params.get("reason").toString() : null;
        
        if (reviewResult == 2 && (reason == null || reason.trim().isEmpty())) {
            ResultVo resultVo = new ResultVo();
            resultVo.setStatus_code(0);
            resultVo.setMsg("驳回申请时必须填写驳回原因");
            return resultVo;
        }
        
        if (afterSaleService.adminReviewAfterSale(afterSaleId, reviewResult, reason)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}

