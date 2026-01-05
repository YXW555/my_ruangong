package com.second.hand.trading.server.service.impl;

import com.second.hand.trading.server.dao.AnnouncementDao;
import com.second.hand.trading.server.entity.AnnouncementModel;
import com.second.hand.trading.server.service.AnnouncementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementDao announcementDao;

    @Override
    public boolean addAnnouncement(AnnouncementModel announcement) {
        if (announcement.getCreateTime() == null) {
            announcement.setCreateTime(new Date());
        }
        if (announcement.getIsPinned() != null && announcement.getIsPinned().byteValue() == 1) {
            if (announcement.getPinTime() == null) {
                announcement.setPinTime(new Date());
            }
        } else {
            announcement.setIsPinned((byte) 0);
            announcement.setPinTime(null);
        }
        return announcementDao.insert(announcement) == 1;
    }

    @Override
    public List<AnnouncementModel> listRecentAnnouncements() {
        return announcementDao.selectRecentAnnouncements();
    }
    
    @Override
    public AnnouncementModel getAnnouncement(Long id) {
        return announcementDao.selectByPrimaryKey(id);
    }

    @Override
    public boolean updateAnnouncement(AnnouncementModel announcement) {
        return announcementDao.updateByPrimaryKeySelective(announcement) == 1;
    }

    @Override
    public boolean deleteAnnouncement(Long id) {
        return announcementDao.deleteByPrimaryKey(id) == 1;
    }
}

