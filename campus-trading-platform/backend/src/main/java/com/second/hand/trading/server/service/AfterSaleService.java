package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.AfterSaleModel;

import java.util.List;

/**
 * 售后服务接口
 */
public interface AfterSaleService {
    /**
     * 买家申请售后
     */
    boolean applyAfterSale(AfterSaleModel afterSale);

    /**
     * 根据订单ID查询售后申请
     */
    AfterSaleModel getByOrderId(Long orderId);

    /**
     * 查询买家的售后申请列表
     */
    List<AfterSaleModel> getMyAfterSales(Long buyerId);

    /**
     * 查询卖家的待审核售后申请列表
     */
    List<AfterSaleModel> getPendingAfterSales(Long sellerId);

    /**
     * 卖家审核售后申请
     * @param afterSaleId 售后申请ID
     * @param reviewResult 审核结果：1-同意，2-拒绝
     * @param rejectReason 拒绝原因（拒绝时必填）
     * @param sellerEvidenceImages 卖家举证图片（拒绝时可选）
     * @return 是否成功
     */
    boolean reviewAfterSale(Long afterSaleId, Byte reviewResult, String rejectReason, String sellerEvidenceImages);

    /**
     * 管理员获取待审核的售后申请列表
     */
    List<AfterSaleModel> getAdminPendingAfterSales();

    /**
     * 管理员获取售后申请详情（包含订单、聊天记录等）
     */
    AfterSaleModel getAdminAfterSaleDetail(Long afterSaleId);

    /**
     * 管理员审核售后申请
     * @param afterSaleId 售后申请ID
     * @param reviewResult 审核结果：1-同意退款，2-驳回
     * @param reason 驳回原因（驳回时必填）
     * @return 是否成功
     */
    boolean adminReviewAfterSale(Long afterSaleId, Byte reviewResult, String reason);
}

