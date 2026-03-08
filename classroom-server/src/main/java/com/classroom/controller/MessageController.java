package com.classroom.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.classroom.common.result.R;
import com.classroom.entity.Message;
import com.classroom.service.MessageService;
import com.classroom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public R<Void> create(@RequestBody Message message) {
        message.setSenderId(SecurityUtil.getCurrentUserId());
        messageService.save(message);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        messageService.removeById(id);
        return R.ok();
    }

    @GetMapping("/page")
    public R<IPage<Message>> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) Long courseId) {
        return R.ok(messageService.pageList(page, size, courseId));
    }
}
