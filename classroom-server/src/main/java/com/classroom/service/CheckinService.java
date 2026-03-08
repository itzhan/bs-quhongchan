package com.classroom.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.classroom.dto.CheckinDTO;
import com.classroom.entity.Checkin;

public interface CheckinService extends IService<Checkin> {
    Checkin createCheckin(CheckinDTO dto);
    void endCheckin(Long id);
    IPage<Checkin> pageList(int page, int size, Long courseId, Integer status);
}
