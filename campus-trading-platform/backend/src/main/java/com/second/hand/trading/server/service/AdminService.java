package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.AdminModel;
import com.second.hand.trading.server.dto.PageVo;

public interface AdminService {

    AdminModel login(String accountNumber, String adminPassword);

    PageVo<AdminModel> getAdminList(int page, int nums);

    boolean addAdmin(AdminModel adminModel);

}
