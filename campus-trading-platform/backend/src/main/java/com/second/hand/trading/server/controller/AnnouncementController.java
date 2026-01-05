package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.dto.ResultVo;
import com.second.hand.trading.server.entity.AnnouncementModel;
import com.second.hand.trading.server.entity.UserModel;
import com.second.hand.trading.server.service.AnnouncementService;
import com.second.hand.trading.server.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import com.second.hand.trading.server.enums.ErrorMsg;

@RestController
@RequestMapping("/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserDao userDao;

    /**
     * 添加公告（管理员和经营性卖家）
     */
    @PostMapping("/add")
    public ResultVo addAnnouncement(@CookieValue(value = "shUserId", required = false) String shUserId,
                                    @Valid @RequestBody AnnouncementModel announcement) {
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        Long uid;
        try {
            uid = Long.valueOf(shUserId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }

        UserModel user = userDao.selectByPrimaryKey(uid);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户不存在");
        }
        Byte role = user.getUserRole();
        // 允许管理员（2）和经营性卖家（1）
        if (role == null || (role != 2 && role != 1)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限发布公告");
        }

        announcement.setCreatorId(uid);
        announcement.setCreatorRole(role);
        announcement.setCreateTime(new Date());
        if (announcement.getIsPinned() != null && announcement.getIsPinned().byteValue() == 1) {
            announcement.setPinTime(new Date());
        }

        boolean ok = announcementService.addAnnouncement(announcement);
        if (ok) {
            return ResultVo.success(announcement);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取公告列表（置顶优先）
     */
    @GetMapping("/list")
    public ResultVo<List<AnnouncementModel>> listAnnouncements() {
        List<AnnouncementModel> list = announcementService.listRecentAnnouncements();
        return ResultVo.success(list);
    }
    /**
     * 获取单个公告
     */
    @GetMapping("/get")
    public ResultVo<AnnouncementModel> getAnnouncement(@RequestParam Long id) {
        AnnouncementModel a = announcementService.getAnnouncement(id);
        if (a == null) return ResultVo.fail(ErrorMsg.PARAM_ERROR);
        return ResultVo.success(a);
    }
    /**
     * 删除公告（管理员/经营性卖家）
     */
    @GetMapping("/delete")
    public ResultVo deleteAnnouncement(@CookieValue(value = "shUserId", required = false) String shUserId,
                                       @RequestParam Long id) {
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        Long uid;
        try { uid = Long.valueOf(shUserId); } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        UserModel user = userDao.selectByPrimaryKey(uid);
        if (user == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户不存在");
        Byte role = user.getUserRole();
        if (role == null || (role != 2 && role != 1)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限");
        }
        if (announcementService.deleteAnnouncement(id)) {
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 更新公告（管理员/经营性卖家）
     */
    @PostMapping("/update")
    public ResultVo updateAnnouncement(@CookieValue(value = "shUserId", required = false) String shUserId,
                                       @Valid @RequestBody AnnouncementModel announcement) {
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        Long uid;
        try { uid = Long.valueOf(shUserId); } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        UserModel user = userDao.selectByPrimaryKey(uid);
        if (user == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户不存在");
        Byte role = user.getUserRole();
        if (role == null || (role != 2 && role != 1)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限");
        }
        announcement.setCreatorId(uid);
        if (announcement.getIsPinned() != null && announcement.getIsPinned().byteValue() == 1) {
            announcement.setPinTime(new Date());
        } else {
            announcement.setIsPinned((byte)0);
            announcement.setPinTime(null);
        }
        if (announcementService.updateAnnouncement(announcement)) {
            return ResultVo.success(announcement);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}

