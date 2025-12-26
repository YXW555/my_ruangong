package com.second.hand.trading.server.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 商家认证申请表
 * 对应表：sh_merchant_application
 */
public class MerchantApplicationModel implements Serializable {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 申请人用户ID
     */
    private Long userId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 营业执照图片URL
     */
    private String businessLicense;

    /**
     * 身份证正面图片URL
     */
    private String idCardFront;

    /**
     * 身份证反面图片URL
     */
    private String idCardBack;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系地址
     */
    private String contactAddress;

    /**
     * 申请理由
     */
    private String applicationReason;

    /**
     * 审核状态（0-待审核，1-已通过，2-已拒绝）
     */
    private Byte status;

    /**
     * 审核管理员ID
     */
    private Long adminId;

    /**
     * 管理员审核意见
     */
    private String adminComment;

    /**
     * 申请时间
     */
    private Date createTime;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 申请人信息
     */
    private UserModel user;

    /**
     * 审核管理员信息
     */
    private AdminModel admin;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getIdCardFront() {
        return idCardFront;
    }

    public void setIdCardFront(String idCardFront) {
        this.idCardFront = idCardFront;
    }

    public String getIdCardBack() {
        return idCardBack;
    }

    public void setIdCardBack(String idCardBack) {
        this.idCardBack = idCardBack;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getApplicationReason() {
        return applicationReason;
    }

    public void setApplicationReason(String applicationReason) {
        this.applicationReason = applicationReason;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public AdminModel getAdmin() {
        return admin;
    }

    public void setAdmin(AdminModel admin) {
        this.admin = admin;
    }
}

