package com.second.hand.trading.server.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 评价表
 * 对应表：sh_rating
 */
public class RatingModel implements Serializable {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 买家ID（评价人）
     */
    private Long buyerId;

    /**
     * 卖家ID（被评价人）
     */
    private Long sellerId;

    /**
     * 评分（1-5分）
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 评价时间
     */
    private Date createTime;

    /**
     * 买家信息
     */
    private UserModel buyer;

    /**
     * 卖家信息
     */
    private UserModel seller;

    /**
     * 订单信息
     */
    private OrderModel order;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public UserModel getBuyer() {
        return buyer;
    }

    public void setBuyer(UserModel buyer) {
        this.buyer = buyer;
    }

    public UserModel getSeller() {
        return seller;
    }

    public void setSeller(UserModel seller) {
        this.seller = seller;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }
}

