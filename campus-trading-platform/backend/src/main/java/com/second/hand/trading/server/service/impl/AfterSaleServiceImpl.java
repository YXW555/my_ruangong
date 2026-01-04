package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.AfterSaleDao;
import com.second.hand.trading.server.dao.IdleItemDao;
import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.entity.AfterSaleModel;
import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.entity.OrderModel;
import com.second.hand.trading.server.entity.UserModel;
import com.second.hand.trading.server.service.AfterSaleService;
import com.second.hand.trading.server.service.ChatMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class AfterSaleServiceImpl implements AfterSaleService {

    @Resource
    private AfterSaleDao afterSaleDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private IdleItemDao idleItemDao;

    @Resource
    private UserDao userDao;

    @Resource
    private ChatMessageService chatMessageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyAfterSale(AfterSaleModel afterSale) {
        // 检查订单是否存在
        OrderModel order = orderDao.selectByPrimaryKey(afterSale.getOrderId());
        if (order == null) {
            return false;
        }

        // 检查订单状态：只有状态3（交易完成-售后保障中）才能申请售后
        if (order.getOrderStatus() != 3) {
            return false;
        }

        // 检查是否已有待处理的售后申请
        AfterSaleModel existing = afterSaleDao.selectByOrderId(afterSale.getOrderId());
        if (existing != null && existing.getApplicationStatus() == 0) {
            // 已有待处理的售后申请
            return false;
        }

        // 设置卖家ID（从订单中获取商品发布者）
        IdleItemModel idleItem = idleItemDao.selectByPrimaryKey(order.getIdleId());
        if (idleItem == null) {
            return false;
        }
        afterSale.setSellerId(idleItem.getUserId());

        // 设置默认值
        afterSale.setApplicationStatus((byte) 0); // 待卖家审核
        afterSale.setCreateTime(new Date());

        // 插入售后申请
        int result = afterSaleDao.insert(afterSale);
        
        if (result == 1) {
            // 更新订单状态为5（售后申请中）
            order.setOrderStatus((byte) 5);
            orderDao.updateByPrimaryKeySelective(order);
            return true;
        }
        
        return false;
    }

    @Override
    public AfterSaleModel getByOrderId(Long orderId) {
        return afterSaleDao.selectByOrderId(orderId);
    }

    @Override
    public List<AfterSaleModel> getMyAfterSales(Long buyerId) {
        return afterSaleDao.selectByBuyerId(buyerId);
    }

    @Override
    public List<AfterSaleModel> getPendingAfterSales(Long sellerId) {
        return afterSaleDao.selectPendingBySellerId(sellerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewAfterSale(Long afterSaleId, Byte reviewResult, String rejectReason, String sellerEvidenceImages) {
        AfterSaleModel afterSale = afterSaleDao.selectByPrimaryKey(afterSaleId);
        if (afterSale == null || afterSale.getApplicationStatus() != 0) {
            // 售后申请不存在或已处理
            return false;
        }

        OrderModel order = orderDao.selectByPrimaryKey(afterSale.getOrderId());
        if (order == null) {
            return false;
        }

        Date now = new Date();
        afterSale.setSellerReviewTime(now);
        afterSale.setSellerReviewResult(reviewResult);
        afterSale.setUpdateTime(now);

        if (reviewResult == 1) {
            // 卖家同意退货退款，转给管理员审核
            afterSale.setApplicationStatus((byte) 3); // 待管理员审核（卖家已同意）
            
            // 更新订单状态为5（售后申请中，等待管理员审核）
            order.setOrderStatus((byte) 5);
            orderDao.updateByPrimaryKeySelective(order);
            
        } else if (reviewResult == 2) {
            // 卖家拒绝退货退款，转给管理员审核
            afterSale.setApplicationStatus((byte) 4); // 待管理员审核（卖家已拒绝）
            afterSale.setSellerRejectReason(rejectReason);
            if (sellerEvidenceImages != null) {
                afterSale.setSellerEvidenceImages(sellerEvidenceImages);
            }
            
            // 更新订单状态为5（售后申请中，等待管理员审核）
            order.setOrderStatus((byte) 5);
            orderDao.updateByPrimaryKeySelective(order);
        }

        return afterSaleDao.updateByPrimaryKeySelective(afterSale) == 1;
    }

    @Override
    public List<AfterSaleModel> getAdminPendingAfterSales() {
        // 获取状态为3（待管理员审核-卖家已同意）或4（待管理员审核-卖家已拒绝）的售后申请
        return afterSaleDao.selectAdminPending();
    }

    @Override
    public AfterSaleModel getAdminAfterSaleDetail(Long afterSaleId) {
        AfterSaleModel afterSale = afterSaleDao.selectByPrimaryKey(afterSaleId);
        if (afterSale == null) {
            return null;
        }

        // 填充订单信息
        OrderModel order = orderDao.selectByPrimaryKey(afterSale.getOrderId());
        if (order != null) {
            afterSale.setOrder(order);
            // 填充商品信息
            IdleItemModel idleItem = idleItemDao.selectByPrimaryKey(order.getIdleId());
            if (idleItem != null) {
                order.setIdleItem(idleItem);
            }
        }

        // 填充买家信息
        if (afterSale.getBuyerId() != null) {
            UserModel buyer = userDao.selectByPrimaryKey(afterSale.getBuyerId());
            afterSale.setBuyer(buyer);
        }

        // 填充卖家信息
        if (afterSale.getSellerId() != null) {
            UserModel seller = userDao.selectByPrimaryKey(afterSale.getSellerId());
            afterSale.setSeller(seller);
        }

        // 获取聊天记录（买家与卖家之间的聊天记录）
        if (afterSale.getBuyerId() != null && afterSale.getSellerId() != null && order != null) {
            List<com.second.hand.trading.server.entity.ChatMessageModel> chatMessages = 
                chatMessageService.getChatDetail(afterSale.getBuyerId(), afterSale.getSellerId(), order.getIdleId());
            // 将聊天记录存储到订单对象中（可以通过扩展OrderModel或创建DTO来实现）
            // 这里暂时不存储，前端可以通过订单ID单独获取
        }

        return afterSale;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adminReviewAfterSale(Long afterSaleId, Byte reviewResult, String reason) {
        AfterSaleModel afterSale = afterSaleDao.selectByPrimaryKey(afterSaleId);
        if (afterSale == null) {
            return false;
        }

        // 只有状态为3（待管理员审核-卖家已同意）或4（待管理员审核-卖家已拒绝）的才能审核
        if (afterSale.getApplicationStatus() != 3 && afterSale.getApplicationStatus() != 4) {
            return false;
        }

        OrderModel order = orderDao.selectByPrimaryKey(afterSale.getOrderId());
        if (order == null) {
            return false;
        }

        Date now = new Date();
        afterSale.setUpdateTime(now);

        if (reviewResult == 1) {
            // 管理员同意退款
            afterSale.setApplicationStatus((byte) 5); // 管理员已同意退款
            
            // 更新订单状态为7（已退款）
            order.setOrderStatus((byte) 7);
            orderDao.updateByPrimaryKeySelective(order);
            
            // TODO: 执行退款逻辑，将资金从平台担保账户退回买家
            // 由于是模拟支付，这里暂时只更新状态
            
        } else if (reviewResult == 2) {
            // 管理员驳回申请
            afterSale.setApplicationStatus((byte) 6); // 管理员已驳回
            // 可以将驳回原因存储在sellerRejectReason字段中，或者新增字段
            afterSale.setSellerRejectReason(reason);
            
            // 更新订单状态为3（交易完成，恢复正常状态）
            order.setOrderStatus((byte) 3);
            orderDao.updateByPrimaryKeySelective(order);
        }

        return afterSaleDao.updateByPrimaryKeySelective(afterSale) == 1;
    }
}

