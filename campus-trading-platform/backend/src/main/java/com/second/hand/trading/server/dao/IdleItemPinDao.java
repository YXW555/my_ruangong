package com.second.hand.trading.server.dao;

import com.second.hand.trading.server.entity.IdleItemPinModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface IdleItemPinDao {
    /**
     * 插入置顶记录
     */
    int insert(IdleItemPinModel record);

    /**
     * 查询用户当前有效的置顶数量
     */
    int countActivePinsByUserId(@Param("userId") Long userId, @Param("currentTime") Date currentTime);

    /**
     * 查询商品是否正在置顶
     */
    IdleItemPinModel selectActivePinByItemId(@Param("idleItemId") Long idleItemId, @Param("currentTime") Date currentTime);

    /**
     * 查询用户的所有置顶记录
     */
    List<IdleItemPinModel> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 立即失效（取消）指定商品当前有效的置顶记录，设置 pin_end_time = currentTime
     */
    int expireActivePinByItemId(@Param("idleItemId") Long idleItemId, @Param("currentTime") Date currentTime);
}

