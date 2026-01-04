package com.second.hand.trading.server.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 售后申请实体类
 * sh_after_sale
 */
public class AfterSaleModel implements Serializable {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单信息（关联查询）
     */
    private OrderModel order;

    /**
     * 买家ID（申请人）
     */
    private Long buyerId;

    /**
     * 买家信息（关联查询）
     */
    private UserModel buyer;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 卖家信息（关联查询）
     */
    private UserModel seller;

    /**
     * 申请类型：1-质量问题，2-描述不符，3-未收到货，4-其他
     */
    private Byte applicationType;

    /**
     * 问题描述
     */
    private String problemDescription;

    /**
     * 证据图片（JSON格式）
     */
    private String evidenceImages;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 申请状态：0-待卖家审核，1-卖家已同意，2-卖家已拒绝，3-已完成退款，4-已取消
     */
    private Byte applicationStatus;

    /**
     * 卖家审核时间
     */
    private Date sellerReviewTime;

    /**
     * 卖家审核结果：1-同意，2-拒绝
     */
    private Byte sellerReviewResult;

    /**
     * 卖家拒绝原因
     */
    private String sellerRejectReason;

    /**
     * 卖家举证图片（JSON格式）
     */
    private String sellerEvidenceImages;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    // Getters and Setters
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

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public UserModel getBuyer() {
        return buyer;
    }

    public void setBuyer(UserModel buyer) {
        this.buyer = buyer;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public UserModel getSeller() {
        return seller;
    }

    public void setSeller(UserModel seller) {
        this.seller = seller;
    }

    public Byte getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(Byte applicationType) {
        this.applicationType = applicationType;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getEvidenceImages() {
        return evidenceImages;
    }

    public void setEvidenceImages(String evidenceImages) {
        this.evidenceImages = evidenceImages;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Byte getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(Byte applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Date getSellerReviewTime() {
        return sellerReviewTime;
    }

    public void setSellerReviewTime(Date sellerReviewTime) {
        this.sellerReviewTime = sellerReviewTime;
    }

    public Byte getSellerReviewResult() {
        return sellerReviewResult;
    }

    public void setSellerReviewResult(Byte sellerReviewResult) {
        this.sellerReviewResult = sellerReviewResult;
    }

    public String getSellerRejectReason() {
        return sellerRejectReason;
    }

    public void setSellerRejectReason(String sellerRejectReason) {
        this.sellerRejectReason = sellerRejectReason;
    }

    public String getSellerEvidenceImages() {
        return sellerEvidenceImages;
    }

    public void setSellerEvidenceImages(String sellerEvidenceImages) {
        this.sellerEvidenceImages = sellerEvidenceImages;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

