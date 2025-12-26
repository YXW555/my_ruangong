package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.AdminDao;
import com.second.hand.trading.server.dao.MerchantApplicationDao;
import com.second.hand.trading.server.dao.UserDao;
import com.second.hand.trading.server.entity.AdminModel;
import com.second.hand.trading.server.entity.MerchantApplicationModel;
import com.second.hand.trading.server.entity.UserModel;
import com.second.hand.trading.server.service.MerchantApplicationService;
import com.second.hand.trading.server.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class MerchantApplicationServiceImpl implements MerchantApplicationService {

    @Resource
    private MerchantApplicationDao merchantApplicationDao;

    @Resource
    private UserDao userDao;

    @Resource
    private AdminDao adminDao;

    @Resource
    private UserService userService;

    @Override
    public boolean submitApplication(MerchantApplicationModel applicationModel) {
        // 检查是否已有申请
        MerchantApplicationModel existing = merchantApplicationDao.selectByUserId(applicationModel.getUserId());
        if (existing != null && existing.getStatus() == 0) {
            // 已有待审核的申请
            return false;
        }
        applicationModel.setStatus((byte) 0);
        applicationModel.setCreateTime(new Date());
        return merchantApplicationDao.insert(applicationModel) == 1;
    }

    @Override
    public MerchantApplicationModel getApplicationByUserId(Long userId) {
        MerchantApplicationModel application = merchantApplicationDao.selectByUserId(userId);
        if (application != null) {
            fillApplicationInfo(application);
        }
        return application;
    }

    @Override
    public MerchantApplicationModel getApplicationById(Long id) {
        MerchantApplicationModel application = merchantApplicationDao.selectByPrimaryKey(id);
        if (application != null) {
            fillApplicationInfo(application);
        }
        return application;
    }

    @Override
    public List<MerchantApplicationModel> getAllApplications(Byte status) {
        List<MerchantApplicationModel> list = merchantApplicationDao.selectAll(status);
        for (MerchantApplicationModel application : list) {
            fillApplicationInfo(application);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewApplication(Long applicationId, Long adminId, Byte status, String adminComment) {
        MerchantApplicationModel application = merchantApplicationDao.selectByPrimaryKey(applicationId);
        if (application == null || application.getStatus() != 0) {
            return false;
        }

        application.setStatus(status);
        application.setAdminId(adminId);
        application.setAdminComment(adminComment);
        application.setReviewTime(new Date());

        if (merchantApplicationDao.updateByPrimaryKeySelective(application) == 1) {
            // 如果审核通过，更新用户角色为经营性卖家
            if (status == 1) {
                UserModel user = userDao.selectByPrimaryKey(application.getUserId());
                if (user != null) {
                    user.setUserRole((byte) 1); // 1-经营性卖家
                    userDao.updateByPrimaryKeySelective(user);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 填充申请的关联信息
     */
    @Override
    public int countPendingApplications() {
        return merchantApplicationDao.countByStatus((byte) 0);
    }

    /**
     * 填充申请的关联信息
     */
    private void fillApplicationInfo(MerchantApplicationModel application) {
        if (application.getUserId() != null) {
            UserModel user = userDao.selectByPrimaryKey(application.getUserId());
            application.setUser(user);
        }
        if (application.getAdminId() != null) {
            AdminModel admin = adminDao.selectByPrimaryKey(application.getAdminId());
            application.setAdmin(admin);
        }
    }
}

