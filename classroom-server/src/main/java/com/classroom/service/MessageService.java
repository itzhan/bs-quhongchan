package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.Message;

public interface MessageService extends IService<Message> {
    IPage<Message> pageList(int page, int size, Long courseId);
}
