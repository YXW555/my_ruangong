package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.entity.IdleItemModel;
import com.second.hand.trading.server.service.RecommendService;
import com.second.hand.trading.server.dto.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 推荐服务Controller
 */
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取个性化推荐商品（猜你喜欢）
     */
    @GetMapping("/personalized")
    public ResultVo<List<IdleItemModel>> getPersonalizedRecommendations(
            HttpServletRequest request,
            @RequestParam(value = "limit", defaultValue = "8") int limit) {
        
        // 获取用户ID（从Cookie中）
        Long userId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("shUserId".equals(cookie.getName())) {
                    try {
                        userId = Long.valueOf(cookie.getValue());
                    } catch (NumberFormatException e) {
                        // 忽略无效的Cookie值
                    }
                    break;
                }
            }
        }
        
        List<IdleItemModel> recommendations = recommendService.getPersonalizedRecommendations(userId, limit);
        return ResultVo.success(recommendations);
    }

    /**
     * 获取相似商品推荐
     */
    @GetMapping("/similar")
    public ResultVo<List<IdleItemModel>> getSimilarItems(
            @RequestParam Long itemId,
            @RequestParam(value = "limit", defaultValue = "6") int limit) {
        List<IdleItemModel> similarItems = recommendService.getSimilarItems(itemId, limit);
        return ResultVo.success(similarItems);
    }

    /**
     * 获取热门推荐
     */
    @GetMapping("/hot")
    public ResultVo<List<IdleItemModel>> getHotRecommendations(
            @RequestParam(value = "limit", defaultValue = "8") int limit) {
        List<IdleItemModel> hotItems = recommendService.getHotRecommendations(limit);
        return ResultVo.success(hotItems);
    }
}

