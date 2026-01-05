package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.AnnouncementModel;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AnnouncementDao {
    int insert(AnnouncementModel announcement);
    List<AnnouncementModel> selectRecentAnnouncements();
    AnnouncementModel selectByPrimaryKey(Long id);
    int updateByPrimaryKeySelective(AnnouncementModel announcement);
    int deleteByPrimaryKey(Long id);
}

