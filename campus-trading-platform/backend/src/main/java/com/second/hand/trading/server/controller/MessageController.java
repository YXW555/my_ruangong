package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.entity.MessageModel;
import com.second.hand.trading.server.service.MessageService;
import com.second.hand.trading.server.dto.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public ResultVo sendMessage(@CookieValue(value = "shUserId", required = false) String shUserId,
                                @RequestBody MessageModel messageModel){
        // Validate login cookie
        if (shUserId == null || shUserId.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }
        Long userId;
        try {
            userId = Long.valueOf(shUserId);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "登录异常 请重新登录");
        }

        messageModel.setUserId(userId);
        messageModel.setCreateTime(new Date());
        try {
            if(messageService.addMessage(messageModel)){
                return ResultVo.success(messageModel);
            } else {
                return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
            }
        } catch (Exception e) {
            // 防御性捕获，打印堆栈便于本地/日志排查，同时返回友好错误给前端
            System.err.println("sendMessage error: " + e.getMessage());
            e.printStackTrace();
            return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
        }
    }

    @GetMapping("/info")
    public ResultVo getMessage(@RequestParam Long id){
        return ResultVo.success(messageService.getMessage(id));
    }

    @GetMapping("/idle")
    public ResultVo getAllIdleMessage(@RequestParam Long idleId){
        return ResultVo.success(messageService.getAllIdleMessage(idleId));
    }

    @GetMapping("/my")
    public ResultVo getAllMyMessage(@CookieValue("shUserId")
                                        @NotNull(message = "登录异常 请重新登录")
                                        @NotEmpty(message = "登录异常 请重新登录") String shUserId){
        return ResultVo.success(messageService.getAllMyMessage(Long.valueOf(shUserId)));
    }

    @GetMapping("/delete")
    public ResultVo deleteMessage(@CookieValue("shUserId")
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                  @RequestParam Long id){
        if(messageService.deleteMessage(id)){
            return ResultVo.success();
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }
}
