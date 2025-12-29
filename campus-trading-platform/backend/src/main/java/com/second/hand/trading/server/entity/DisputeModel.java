package com.second.hand.trading.server.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 交易纠纷实体类
 * sh_dispute
 */
public class DisputeModel implements Serializable {
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
     * 申请人ID（买家或卖家）
     */
    private Long applicantId;

    /**
     * 申请人信息（关联查询）
     */
    private UserModel applicant;

    /**
     * 纠纷类型：1-商品描述不符，2-商品质量问题，3-未收到货，4-退款问题，5-其他
     */
    private Byte disputeType;

    /**
     * 纠纷原因描述
     */
    private String disputeReason;

    /**
     * 证据图片（JSON格式，存储多个图片URL）
     */
    private String evidenceImages;

    /**
     * 纠纷状态：0-待处理，1-处理中，2-已解决，3-已关闭
     */
    private Byte disputeStatus;

    /**
     * 处理结果：1-退款给买家，2-驳回申请，3-要求退货退款，4-警告用户，5-其他
     */
    private Byte handleResult;

    /**
     * 处理说明
     */
    private String handleDescription;

    /**
     * 处理人ID（管理员ID）
     */
    private Long handlerId;

    /**
     * 处理人信息（关联查询）
     */
    private AdminModel handler;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 处理时间
     */
    private Date handleTime;

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

    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public UserModel getApplicant() {
        return applicant;
    }

    public void setApplicant(UserModel applicant) {
        this.applicant = applicant;
    }

    public Byte getDisputeType() {
        return disputeType;
    }

    public void setDisputeType(Byte disputeType) {
        this.disputeType = disputeType;
    }

    public String getDisputeReason() {
        return disputeReason;
    }

    public void setDisputeReason(String disputeReason) {
        this.disputeReason = disputeReason;
    }

    public String getEvidenceImages() {
        return evidenceImages;
    }

    public void setEvidenceImages(String evidenceImages) {
        this.evidenceImages = evidenceImages;
    }

    public Byte getDisputeStatus() {
        return disputeStatus;
    }

    public void setDisputeStatus(Byte disputeStatus) {
        this.disputeStatus = disputeStatus;
    }

    public Byte getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(Byte handleResult) {
        this.handleResult = handleResult;
    }

    public String getHandleDescription() {
        return handleDescription;
    }

    public void setHandleDescription(String handleDescription) {
        this.handleDescription = handleDescription;
    }

    public Long getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Long handlerId) {
        this.handlerId = handlerId;
    }

    public AdminModel getHandler() {
        return handler;
    }

    public void setHandler(AdminModel handler) {
        this.handler = handler;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }
}

