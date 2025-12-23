package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.OrderDao;
import com.second.hand.trading.server.dao.RatingDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.entity.OrderModel;
import com.second.hand.trading.server.entity.RatingModel;
import com.second.hand.trading.server.entity.UserModel;
import com.second.hand.trading.server.service.RatingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RatingServiceImpl implements RatingService {

    @Resource
    private RatingDao ratingDao;

    @Resource
    private UserDao userDao;

    @Resource
    private OrderDao orderDao;

    @Override
    public boolean addRating(RatingModel ratingModel) {
        return ratingDao.insert(ratingModel) == 1;
    }

    @Override
    public RatingModel getRatingByOrderId(Long orderId) {
        RatingModel rating = ratingDao.selectByOrderId(orderId);
        if (rating != null) {
            fillRatingInfo(rating);
        }
        return rating;
    }

    @Override
    public List<RatingModel> getRatingsBySellerId(Long sellerId) {
        List<RatingModel> list = ratingDao.selectBySellerId(sellerId);
        if (!list.isEmpty()) {
            fillRatingsInfo(list);
        }
        return list;
    }

    @Override
    public List<RatingModel> getRatingsByBuyerId(Long buyerId) {
        List<RatingModel> list = ratingDao.selectByBuyerId(buyerId);
        if (!list.isEmpty()) {
            fillRatingsInfo(list);
        }
        return list;
    }

    @Override
    public Double getAverageRating(Long sellerId) {
        Double avg = ratingDao.getAverageRating(sellerId);
        return avg != null ? avg : 0.0;
    }

    @Override
    public Integer getRatingCount(Long sellerId) {
        return ratingDao.countBySellerId(sellerId);
    }

    /**
     * 填充评价的关联信息（买家、卖家、订单）
     */
    private void fillRatingInfo(RatingModel rating) {
        if (rating.getBuyerId() != null) {
            UserModel buyer = userDao.selectByPrimaryKey(rating.getBuyerId());
            rating.setBuyer(buyer);
        }
        if (rating.getSellerId() != null) {
            UserModel seller = userDao.selectByPrimaryKey(rating.getSellerId());
            rating.setSeller(seller);
        }
        if (rating.getOrderId() != null) {
            OrderModel order = orderDao.selectByPrimaryKey(rating.getOrderId());
            rating.setOrder(order);
        }
    }

    /**
     * 批量填充评价的关联信息
     */
    private void fillRatingsInfo(List<RatingModel> list) {
        List<Long> userIdList = new ArrayList<>();
        List<Long> orderIdList = new ArrayList<>();
        for (RatingModel rating : list) {
            if (rating.getBuyerId() != null) {
                userIdList.add(rating.getBuyerId());
            }
            if (rating.getSellerId() != null) {
                userIdList.add(rating.getSellerId());
            }
            if (rating.getOrderId() != null) {
                orderIdList.add(rating.getOrderId());
            }
        }

        Map<Long, UserModel> userMap = new HashMap<>();
        if (!userIdList.isEmpty()) {
            List<UserModel> users = userDao.findUserByList(userIdList);
            for (UserModel user : users) {
                userMap.put(user.getId(), user);
            }
        }

        Map<Long, OrderModel> orderMap = new HashMap<>();
        if (!orderIdList.isEmpty()) {
            for (Long orderId : orderIdList) {
                OrderModel order = orderDao.selectByPrimaryKey(orderId);
                if (order != null) {
                    orderMap.put(orderId, order);
                }
            }
        }

        for (RatingModel rating : list) {
            if (rating.getBuyerId() != null) {
                rating.setBuyer(userMap.get(rating.getBuyerId()));
            }
            if (rating.getSellerId() != null) {
                rating.setSeller(userMap.get(rating.getSellerId()));
            }
            if (rating.getOrderId() != null) {
                rating.setOrder(orderMap.get(rating.getOrderId()));
            }
        }
    }
}

