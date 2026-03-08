package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.entity.Announcement;

public interface AnnouncementService extends IService<Announcement> {
    IPage<Announcement> pageList(int page, int size, Long courseId, Integer type, Integer status);
}
