package com.second.hand.trading.server.service;

import com.second.hand.trading.server.entity.AnnouncementModel;
import java.util.List;

public interface AnnouncementService {
    boolean addAnnouncement(AnnouncementModel announcement);
    List<AnnouncementModel> listRecentAnnouncements();
    AnnouncementModel getAnnouncement(Long id);
    boolean updateAnnouncement(AnnouncementModel announcement);
    boolean deleteAnnouncement(Long id);
}

