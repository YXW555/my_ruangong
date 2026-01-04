package com.second.hand.trading.server.controller;

import com.second.hand.trading.server.dto.ResultVo;
import com.second.hand.trading.server.entity.AutoReplyTemplateModel;
import com.second.hand.trading.server.enums.ErrorMsg;
import com.second.hand.trading.server.service.AutoReplyTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 自动回复模板控制器
 */
@RestController
@RequestMapping("/auto-reply")
public class AutoReplyTemplateController {

    @Autowired
    private AutoReplyTemplateService autoReplyTemplateService;

    /**
     * 创建或更新自动回复模板
     */
    @PostMapping("/template")
    public ResultVo createOrUpdateTemplate(@CookieValue("shUserId")
                                          @NotNull(message = "登录异常 请重新登录")
                                          @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                          @RequestBody AutoReplyTemplateModel template) {
        if (template.getKeyword() == null || template.getKeyword().trim().isEmpty()) {
            ResultVo resultVo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
            resultVo.setMsg("关键词不能为空");
            return resultVo;
        }
        if (template.getReplyContent() == null || template.getReplyContent().trim().isEmpty()) {
            ResultVo resultVo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
            resultVo.setMsg("回复内容不能为空");
            return resultVo;
        }
        template.setUserId(Long.valueOf(shUserId));
        if (autoReplyTemplateService.createTemplate(template)) {
            return ResultVo.success(template);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 更新自动回复模板
     */
    @PutMapping("/template")
    public ResultVo updateTemplate(@CookieValue("shUserId")
                                  @NotNull(message = "登录异常 请重新登录")
                                  @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                  @RequestBody AutoReplyTemplateModel template) {
        if (template.getId() == null) {
            ResultVo resultVo = ResultVo.fail(ErrorMsg.PARAM_ERROR);
            resultVo.setMsg("模板ID不能为空");
            return resultVo;
        }
        template.setUserId(Long.valueOf(shUserId));
        if (autoReplyTemplateService.updateTemplate(template)) {
            return ResultVo.success(template);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 删除自动回复模板
     */
    @DeleteMapping("/template/{id}")
    public ResultVo deleteTemplate(@CookieValue("shUserId")
                                   @NotNull(message = "登录异常 请重新登录")
                                   @NotEmpty(message = "登录异常 请重新登录") String shUserId,
                                   @PathVariable Long id) {
        if (autoReplyTemplateService.deleteTemplate(id, Long.valueOf(shUserId))) {
            return ResultVo.success(null);
        }
        return ResultVo.fail(ErrorMsg.SYSTEM_ERROR);
    }

    /**
     * 获取当前用户的所有自动回复模板
     */
    @GetMapping("/template/list")
    public ResultVo getTemplateList(@CookieValue("shUserId")
                                   @NotNull(message = "登录异常 请重新登录")
                                   @NotEmpty(message = "登录异常 请重新登录") String shUserId) {
        List<AutoReplyTemplateModel> templates = autoReplyTemplateService.getTemplatesByUserId(Long.valueOf(shUserId));
        return ResultVo.success(templates);
    }
}

