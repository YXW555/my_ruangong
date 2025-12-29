package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.DisputeModel;
import com.second.hand.trading.server.dto.PageVo;

import java.util.List;

public interface DisputeService {
    /**
     * 用户申请纠纷处理
     * @param disputeModel 纠纷信息
     * @return 是否成功
     */
    boolean applyDispute(DisputeModel disputeModel);

    /**
     * 获取用户的纠纷列表
     * @param userId 用户ID
     * @return 纠纷列表
     */
    List<DisputeModel> getMyDisputes(Long userId);

    /**
     * 获取纠纷详情
     * @param id 纠纷ID
     * @return 纠纷详情
     */
    DisputeModel getDispute(Long id);

    /**
     * 管理员获取纠纷列表（分页）
     * @param status 纠纷状态（可选，null表示全部）
     * @param page 页码
     * @param nums 每页数量
     * @return 分页结果
     */
    PageVo<DisputeModel> getAllDisputes(Byte status, int page, int nums);

    /**
     * 管理员处理纠纷
     * @param disputeId 纠纷ID
     * @param handleResult 处理结果：1-退款给买家，2-驳回申请，3-要求退货退款，4-警告用户，5-其他
     * @param handleDescription 处理说明
     * @param handlerId 处理人ID（管理员ID）
     * @return 是否成功
     */
    boolean handleDispute(Long disputeId, Byte handleResult, String handleDescription, Long handlerId);
}

