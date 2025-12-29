package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.DisputeDao;
import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.entity.DisputeModel;
import com.second.hand.trading.server.entity.OrderModel;
import com.second.hand.trading.server.service.DisputeService;
import com.second.hand.trading.server.service.OrderService;
import com.second.hand.trading.server.dto.PageVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class DisputeServiceImpl implements DisputeService {

    @Resource
    private DisputeDao disputeDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderService orderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyDispute(DisputeModel disputeModel) {
        // 检查订单是否存在
        OrderModel order = orderDao.selectByPrimaryKey(disputeModel.getOrderId());
        if (order == null) {
            return false;
        }

        // 检查该订单是否已经有待处理的纠纷
        DisputeModel existingDispute = disputeDao.selectByOrderId(disputeModel.getOrderId());
        if (existingDispute != null && existingDispute.getDisputeStatus() == 0) {
            // 已有待处理的纠纷，不允许重复申请
            return false;
        }

        // 设置默认值
        disputeModel.setDisputeStatus((byte) 0); // 待处理
        disputeModel.setCreateTime(new Date());

        return disputeDao.insert(disputeModel) == 1;
    }

    @Override
    public List<DisputeModel> getMyDisputes(Long userId) {
        return disputeDao.selectByApplicantId(userId);
    }

    @Override
    public DisputeModel getDispute(Long id) {
        return disputeDao.selectByPrimaryKey(id);
    }

    @Override
    public PageVo<DisputeModel> getAllDisputes(Byte status, int page, int nums) {
        int begin = (page - 1) * nums;
        List<DisputeModel> list = disputeDao.selectAll(status, begin, nums);
        int count = disputeDao.countAll(status);
        return new PageVo<>(list, count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleDispute(Long disputeId, Byte handleResult, String handleDescription, Long handlerId) {
        DisputeModel dispute = disputeDao.selectByPrimaryKey(disputeId);
        if (dispute == null || dispute.getDisputeStatus() != 0) {
            // 纠纷不存在或已处理
            return false;
        }

        // 更新纠纷状态
        dispute.setDisputeStatus((byte) 2); // 已解决
        dispute.setHandleResult(handleResult);
        dispute.setHandleDescription(handleDescription);
        dispute.setHandlerId(handlerId);
        dispute.setHandleTime(new Date());

        // 如果处理结果是退款给买家，需要更新订单状态
        if (handleResult == 1) {
            // 退款给买家：更新订单支付状态为已退款
            OrderModel order = orderDao.selectByPrimaryKey(dispute.getOrderId());
            if (order != null) {
                // 这里可以添加退款逻辑
                // 由于是模拟支付，可以简单地将订单状态标记为已退款
                // order.setPaymentStatus((byte) 2); // 假设2表示已退款
                // orderDao.updateByPrimaryKeySelective(order);
            }
        }

        return disputeDao.updateByPrimaryKeySelective(dispute) == 1;
    }
}

