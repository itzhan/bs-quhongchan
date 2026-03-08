package com.classroom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classroom.entity.Announcement;
import com.classroom.mapper.AnnouncementMapper;
import com.classroom.service.AnnouncementService;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public IPage<Announcement> pageList(int page, int size, Long courseId, Integer type, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (courseId != null) wrapper.eq(Announcement::getCourseId, courseId);
        if (type != null) wrapper.eq(Announcement::getType, type);
        if (status != null) wrapper.eq(Announcement::getStatus, status);
        wrapper.orderByDesc(Announcement::getIsTop).orderByDesc(Announcement::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }
}
