package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.MerchantApplicationModel;

import java.util.List;

/**
 * 商家认证服务接口
 */
public interface MerchantApplicationService {

    /**
     * 提交商家认证申请
     */
    boolean submitApplication(MerchantApplicationModel applicationModel);

    /**
     * 根据用户ID查询申请
     */
    MerchantApplicationModel getApplicationByUserId(Long userId);

    /**
     * 根据ID查询申请
     */
    MerchantApplicationModel getApplicationById(Long id);

    /**
     * 查询所有申请（按状态筛选）
     */
    List<MerchantApplicationModel> getAllApplications(Byte status);

    /**
     * 审核申请（管理员操作）
     */
        boolean reviewApplication(Long applicationId, Long adminId, Byte status, String adminComment);

    /**
     * 获取待审核的申请数量
     */
    int countPendingApplications();
}

