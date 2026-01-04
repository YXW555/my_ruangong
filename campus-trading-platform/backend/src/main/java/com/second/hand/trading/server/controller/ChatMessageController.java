package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.entity.ChatMessageModel;
import com.second.hand.trading.server.service.ChatMessageService;
import com.second.hand.trading.server.dto.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 站内私信相关接口
 */
@RestController
@RequestMapping("/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    /**
     * 发送一条私信
     */
    @PostMapping("/send")
    public ResultVo sendChatMessage(@CookieValue("shUserId")
                                        @NotNull(message = "登录异常 请重新登录")
                                        @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                    @RequestBody ChatMessageModel chatMessageModel) {
        chatMessageModel.setFromUser(Long.valueOf(shUserId));
        chatMessageModel.setCreateTime(new Date());
        chatMessageModel.setIsRead((byte) 0);
        if (chatMessageService.sendMessage(chatMessageModel)) {
            return ResultVo.success(chatMessageModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 发送一张图片
     */
    @PostMapping("/sendImage")
    public ResultVo sendImageMessage(@CookieValue("shUserId")
                                         @NotNull(message = "登录异常 请重新登录")
                                         @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                     @RequestBody ChatMessageModel chatMessageModel) {
        chatMessageModel.setFromUser(Long.valueOf(shUserId));
        chatMessageModel.setCreateTime(new Date());
        chatMessageModel.setIsRead((byte) 0);
        // 图片消息的内容可以为空，或者前端传一个"[图片]"之类的占位符
        if (chatMessageService.sendImageMessage(chatMessageModel)) {
            return ResultVo.success(chatMessageModel);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 当前用户的会话列表
     */
    @GetMapping("/session/list")
    public ResultVo getSessionList(@CookieValue("shUserId")
                                       @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        return ResultVo.success(chatMessageService.getUserSessionList(Long.valueOf(shUserId)));
    }

    /**
     * 会话详情
     */
    @GetMapping("/session/detail")
    public ResultVo getSessionDetail(@CookieValue("shUserId")
                                        @NotNull(message = "登录异常 请重新登录")
                                        @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                    @RequestParam Long targetUserId,
                                    @RequestParam(required = false) Long idleId) {
        return ResultVo.success(
                chatMessageService.getChatDetail(Long.valueOf(shUserId), targetUserId, idleId)
        );
    }

    /**
     * 获取未读消息总数（私信）
     */
    @GetMapping("/unread/count")
    public ResultVo<Integer> getUnreadCount(@CookieValue("shUserId")
                                       @NotNull(message = "登录异常 请重新登录")
                                       @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        int count = chatMessageService.getUnreadMessageCount(Long.valueOf(shUserId));
        return ResultVo.success(count);
    }
}


